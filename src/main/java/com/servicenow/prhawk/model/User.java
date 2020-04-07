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
@JsonIgnoreProperties(ignoreUnknown=true)
public class User {

    @JsonProperty
    private long id;

    @JsonProperty("login")
    private String username;

    @Override
    public boolean equals(Object o) {
        return o instanceof User && this.id == ((User) o).getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
