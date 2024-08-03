package app.githubrepolister.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BranchResponse {
    private String name;

    @JsonProperty("commit")
    private Commit commit;

    @Data
    public static class Commit {
        private String sha;
    }
}
