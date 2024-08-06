package app.githubrepolister.service.githubservice;

import app.githubrepolister.dto.*;
import app.githubrepolister.service.GithubApiService;
import app.githubrepolister.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GetReposWithBranchesTests {

    @Mock
    private GithubApiService githubApiService;

    private GithubService githubService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        githubService = new GithubService(githubApiService);
    }

    @Test
    void getReposWithBranches() {
        when(githubApiService.getUserRepos(anyString())).thenReturn(Flux.just(
                new ReposResponse("repo1", false, new Owner("owner")),
                new ReposResponse("repo2", true, new Owner("owner"))
        ));

        when(githubApiService.getRepoBranches(anyString(), anyString())).thenReturn(Flux.just(
                new BranchResponse("main", new Commit("sha1")),
                new BranchResponse("feature", new Commit("sha2"))
        ));

        Flux<RepoInfo> reposWithBranches = githubService.getReposWithBranches("owner");

        StepVerifier.create(reposWithBranches)
                .expectNextMatches(repo -> repo.equals(
                        new RepoInfo("repo1", "owner", List.of(
                                new BranchInfo("main", "sha1"),
                                new BranchInfo("feature", "sha2")
                        ))))
                .expectComplete()
                .verify();
    }
}
