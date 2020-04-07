package com.servicenow.prhawk.service;

import com.servicenow.prhawk.model.Repository;
import com.servicenow.prhawk.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RepositoryServiceTests {

    @Autowired private RepositoryService repositoryService;
    @Mock private Response response;
    @Mock private Invocation.Builder builder;

    @BeforeEach
    public void init() throws NoSuchFieldException {
        WebTarget webTarget = Mockito.mock(WebTarget.class);
        FieldSetter.setField(repositoryService, repositoryService.getClass().getDeclaredField("webTarget"), webTarget);
        when(webTarget.queryParam(anyString(), any())).thenReturn(webTarget);
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(builder);
        when(builder.accept(anyString())).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.get()).thenReturn(response);
    }

    @Test
    public void testGetRepositoriesWithNullUsername() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            repositoryService.getRepositories(null);
        });
        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    public void testGetRepositoriesWithEmptyUsername() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            repositoryService.getRepositories("");
        });
        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    public void testGetRepositoryWithEmptyResponse() {
        when(response.readEntity(new GenericType<List<Repository>>(){})).thenReturn(getTestRepositories(0));
        List<Repository> repositories = repositoryService.getRepositories("testUser");
        assertEquals(0, repositories.size());
    }

    @Test
    public void testGetRepositoryResponse() {
        when(response.readEntity(new GenericType<List<Repository>>(){})).thenReturn(getTestRepositories(10));
        List<Repository> repositories = repositoryService.getRepositories("testUser");
        assertEquals(10, repositories.size());
        assertEquals(5, repositories.get(4).getId());
    }

    private List<Repository> getTestRepositories(int count) {
        List<Repository> repositories = new ArrayList<>(count);
        User user = new User(0, "testUser");
        for (int i=1; i<=count; i++) {
            Repository repository = new Repository();
            repository.setId(i);
            repository.setName(String.valueOf(i));
            repository.setOwner(user);
            repository.setUrl("https://"+i);
            repository.setPullRequestCount(5);
            repositories.add(repository);
        }
        return repositories;
    }
}
