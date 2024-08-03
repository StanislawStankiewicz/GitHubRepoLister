package app.githubrepolister.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReposResponse {
    private String name;
    private boolean fork;

    @JsonProperty("owner")
    private Owner owner;

    @Data
    public static class Owner {
        private String login;
    }
}
