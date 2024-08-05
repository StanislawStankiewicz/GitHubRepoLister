package app.githubrepolister.controller;

import app.githubrepolister.dto.RepoInfo;
import app.githubrepolister.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class GithubController {

    private final GithubService githubService;

    @GetMapping(path = "/repos/{ownerLogin}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<RepoInfo> getRepos(@PathVariable String ownerLogin) {
        return githubService.getReposWithBranches(ownerLogin);
    }
}
