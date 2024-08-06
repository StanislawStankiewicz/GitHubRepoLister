package app.githubrepolister.service.githubapiservice;

import app.githubrepolister.dto.Owner;
import app.githubrepolister.dto.ReposResponse;
import app.githubrepolister.service.GithubApiService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

class GetUserReposTests {

    public static MockWebServer mockBackEnd;
    private GithubApiService githubApiService;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void init() {
        String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        githubApiService = new GithubApiService(WebClient.builder(), baseUrl, "token");
    }

    @Test
    void getUserRepos() {
        String mockResponse = """
                [
                    {
                        "id": 1,
                        "name": "repo1",
                        "fork": false,
                        "owner": {
                            "login": "owner"
                        }
                    },
                    {
                        "id": 2,
                        "name": "repo2",
                        "fork": true,
                        "owner": {
                            "login": "owner"
                        }
                    }
                ]
                """;
        mockBackEnd.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Flux<ReposResponse> repos = githubApiService.getUserRepos("owner");

        StepVerifier.create(repos)
                .expectNextMatches(repo -> repo.equals(
                        new ReposResponse("repo1", false, new Owner("owner"))))
                .expectNextMatches(repo -> repo.equals(
                        new ReposResponse("repo2", true, new Owner("owner"))))
                .expectComplete()
                .verify();
    }
}
