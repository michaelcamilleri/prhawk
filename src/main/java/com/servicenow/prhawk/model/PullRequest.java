package com.servicenow.prhawk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest implements Comparable<PullRequest>  {

    @JsonProperty private long id;
    @JsonProperty private int number;
    @JsonProperty private String state;
    @JsonProperty private String title;
    @JsonProperty("html_url") private String url;

    @Override
    public boolean equals(Object o) {
        return o instanceof PullRequest && this.id == ((PullRequest) o).getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(PullRequest r) {
        return this.number - r.number; // Ascending order
    }
}
