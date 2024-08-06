package app.githubrepolister.service.githubapiservice;

import app.githubrepolister.dto.BranchResponse;
import app.githubrepolister.dto.Commit;
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

class GetRepoBranchesTests {

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
    void getRepoBranches() {
        String mockResponse = """
                [
                    {
                        "name": "main",
                        "commit": {
                            "sha": "sha1"
                        }
                    },
                    {
                        "name": "feature",
                        "commit": {
                            "sha": "sha2"
                        }
                    }
                ]
                """;

        mockBackEnd.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        Flux<BranchResponse> branches = githubApiService.getRepoBranches("owner", "repo");

        StepVerifier.create(branches)
                .expectNextMatches(branch -> branch.equals(
                        new BranchResponse("main", new Commit("sha1"))))
                .expectNextMatches(branch -> branch.equals(
                        new BranchResponse("feature", new Commit("sha2"))))
                .expectComplete()
                .verify();
    }
}
