package app.githubrepolister.service;

import app.githubrepolister.dto.BranchInfo;
import app.githubrepolister.dto.RepoInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
@RequiredArgsConstructor
public class GithubService {

    private final GithubApiService githubApiService;

    public Flux<RepoInfo> getReposWithBranches(String ownerLogin) {
        return githubApiService.getUserRepos(ownerLogin)
                .filter(repo -> !repo.isFork())
                .flatMap(repo -> githubApiService.getRepoBranches(repo.owner().login(), repo.name())
                        .map(branch -> new BranchInfo(branch.name(), branch.commit().sha()))
                        .collectList()
                        .map(branchInfos -> new RepoInfo(repo.name(), repo.owner().login(), branchInfos))
                );
    }

}
