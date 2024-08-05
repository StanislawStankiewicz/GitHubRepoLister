package app.githubrepolister.service;

import app.githubrepolister.dto.BranchInfo;
import app.githubrepolister.dto.BranchResponse;
import app.githubrepolister.dto.RepoInfo;
import app.githubrepolister.dto.ReposResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;


@Log
@Service
public class GithubService {

    private final WebClient webClient;
    private static final String GITHUB_API_URL = "https://api.github.com";

    public GithubService(WebClient.Builder webClientBuilder,
                         @Value("${github.api.token:}") String githubApiToken) {
        WebClient.Builder builder = webClientBuilder.baseUrl(GITHUB_API_URL);

        if (!githubApiToken.isEmpty()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubApiToken);
            log.info("GitHub API token is set to: " + githubApiToken);
        } else {
            log.warning("GitHub API token is not set. Rate limits may be applied.");
        }

        this.webClient = builder.build();
    }

    public Flux<ReposResponse> getUserRepos(String ownerLogin) {
        return webClient.get()
                .uri("/users/{ownerLogin}/repos", ownerLogin)
                .retrieve()
                .bodyToFlux(ReposResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "GitHub user not found"));
                    }
                    return Flux.error(ex);
                });
    }

    public Flux<BranchResponse> getRepoBranches(String owner, String repo) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .bodyToFlux(BranchResponse.class);
    }

    public Flux<RepoInfo> getReposWithBranches(String ownerLogin) {
        return getUserRepos(ownerLogin)
                .filter(repo -> !repo.isFork())
                .flatMap(repo -> getRepoBranches(repo.owner().login(), repo.name())
                        .map(branch -> new BranchInfo(branch.name(), branch.commit().sha()))
                        .collectList()
                        .map(branchInfos -> new RepoInfo(repo.name(), repo.owner().login(), branchInfos))
                );
    }

}
