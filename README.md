# GitHub Repo Lister

## Description

GitHub Repo Lister is a Spring Boot application designed to interact with the GitHub API. It provides endpoints to list repositories and their branches for a given GitHub user. The application handles errors gracefully and provides custom error responses.

## Setup

### Clone the repository

```bash
git clone <repository-url>
cd <repository-directory>
```

### Configuration

Update the `application.properties` file located in the `src/main/resources` directory with your GitHub personal access token:

```properties
github.api.token=your_github_personal_access_token
```

Alternatively, you can supply the GitHub token as an environment variable. If no token is provided, the rate is limited to 60 requests per hour.

### Build and Run the Application

#### Using Gradle

To build and run the application using Gradle:

```bash
./gradlew build
./gradlew bootRun
```

#### Using Docker

To build and run the application using Docker:

1. Build the Docker image:

    ```bash
    docker build -t github-repo-lister .
    ```

2. Run the Docker container:

    ```bash
    docker run -p 8080:8080 -e GITHUB_API_TOKEN=your_github_personal_access_token github-repo-lister
    ```

Note: The `GITHUB_API_TOKEN` environment variable is optional. If it is not provided, the rate is limited to 60 requests per hour.

## Usage

The application exposes the following endpoints:

- **List Repositories**

    ```
    GET /api/github/repos/{username}
    ```

  Example:

    ```bash
    curl -X GET "http://localhost:8080/api/github/repos/StanislawStankiewicz"
    ```
  
  Response:
    ```json
    data:{"name":"Blog-SpringBoot","owner_login":"StanislawStankiewicz","branches":[{"name":"main","last_commit_sha":"67536846f0997518c1294727ba4a5fdedc5b530c"}]}
    
    data:{"name":"InImageEncryption","owner_login":"StanislawStankiewicz","branches":[{"name":"main","last_commit_sha":"ee4d5cc042fad11be89492a88495f71aacf38767"}]}
    
    data:{"name":"TrainTickets-NodeJS","owner_login":"StanislawStankiewicz","branches":[{"name":"main","last_commit_sha":"1a4c183cc41836788a56718886441d1fda1ebc12"}]}
    
    data:{"name":"GSBlockShuffle","owner_login":"StanislawStankiewicz","branches":[{"name":"GameLogic","last_commit_sha":"52981c1d7be260322cd9baa46a28cd7256054264"},{"name":"gsm+tm-rework","last_commit_sha":"51818439a730690cc3a4e9618d785feedc55e1c9"},{"name":"master","last_commit_sha":"b1443eeb2a1457692a29d97d7fa824d15664209f"}]}
    
    data:{"name":"StanislawStankiewicz","owner_login":"StanislawStankiewicz","branches":[{"name":"main","last_commit_sha":"7e40e1353b3fc89dd4c984fede1aaa42427b2031"}]}
    
    data:{"name":"TextEditor","owner_login":"StanislawStankiewicz","branches":[{"name":"master","last_commit_sha":"870b2fdce06ebbbf0b84e8eb2347918c477a8f9b"}]}
    
    data:{"name":"StanislawStankiewicz.github.io","owner_login":"StanislawStankiewicz","branches":[{"name":"gh-pages","last_commit_sha":"dcff0a2875b8a266594ec863c3684d04a6f2e417"},{"name":"main","last_commit_sha":"aeb3f3c801144449de683121d9c1f8f1638dc6f3"},{"name":"source-code","last_commit_sha":"e01ed53bac1c537d79dfe568715cd3b9f309191b"}]}
    
    data:{"name":"Wordle-fullstack","owner_login":"StanislawStankiewicz","branches":[{"name":"main","last_commit_sha":"1f856b5c248c5592e34a7bf8903d43e1fd44d1e0"}]}
    
    data:{"name":"WordleSolver","owner_login":"StanislawStankiewicz","branches":[{"name":"main","last_commit_sha":"53595697e6136ac3b8ac8bcd8e0564392d3ccda7"}]}
    
    data:{"name":"TwitchPointsTracker","owner_login":"StanislawStankiewicz","branches":[{"name":"master","last_commit_sha":"08f9de09f63ac29c4647dcbd106f262eb16688f0"}]}
    ```
