package com.atipera.gr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Branch {

    private String name;

    private Commit commit;

    @Data
    public static class Commit {

        private String sha;
    }

    public Branch(String name, Commit commit) {
        this.name = name;
        this.commit = commit;
    }
}
