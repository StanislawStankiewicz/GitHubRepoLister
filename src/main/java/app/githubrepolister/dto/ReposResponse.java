package app.githubrepolister.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public record ReposResponse(String name,
                            @JsonProperty("fork") boolean isFork,
                            Owner owner) {
}
