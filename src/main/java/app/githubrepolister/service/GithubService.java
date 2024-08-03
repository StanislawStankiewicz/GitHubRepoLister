package app.githubrepolister.service;

import app.githubrepolister.dto.BranchResponse;
import app.githubrepolister.dto.ReposResponse;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubService {

    @Value("${github.api.token:}")
    private String githubApiToken;
    private WebClient webClient;

    private final Log log = LogFactory.getLog(GithubService.class);

    private static final String GITHUB_API_URL = "https://api.github.com";

    @PostConstruct
    public void init() {
        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(GITHUB_API_URL);

        if (!githubApiToken.isEmpty()) {
            webClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubApiToken);
            log.info("GitHub API token is set to: " + githubApiToken);
        } else {
            log.warn("GitHub API token is not set. Rate limits may be applied.");
        }

        this.webClient = webClientBuilder.build();
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
                .flatMap(repo -> getRepoBranches(repo.getOwner().getLogin(), repo.getName())
                        .map(branch -> new BranchInfo(branch.getName(), branch.getCommit().getSha()))
                        .collectList()
                        .map(branchInfos -> new RepoInfo(repo.getName(), repo.getOwner().getLogin(), branchInfos))
                );
    }

    @Data
    @AllArgsConstructor
    public static class RepoInfo {
        private String name;
        private String ownerLogin;
        private List<BranchInfo> branches;
    }

    @Data
    @AllArgsConstructor
    public static class BranchInfo {
        private String name;
        private String lastCommitSha;
    }
}
