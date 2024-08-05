package app.githubrepolister.dto;


import java.util.List;

public record RepoInfo(String name,
                       String ownerLogin,
                       List<BranchInfo> branches) {
}
