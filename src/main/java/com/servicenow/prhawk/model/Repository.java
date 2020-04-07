package com.servicenow.prhawk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Repository implements Comparable<Repository> {

    @JsonProperty private long id;
    @JsonProperty private String name;
    @JsonProperty("html_url") private String url;
    @JsonProperty("owner") private User owner;
    private List<PullRequest> pullRequests;
    private Integer pullRequestCount;

    @Override
    public boolean equals(Object o) {
        return o instanceof Repository && this.id == ((Repository) o).getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(Repository r) {
        return r.getPullRequestCount() - this.pullRequestCount; // Descending order
    }
}
