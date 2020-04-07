package com.servicenow.prhawk.service;

import com.servicenow.prhawk.auth.Auth;
import com.servicenow.prhawk.model.PullRequest;
import com.servicenow.prhawk.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.util.HtmlUtils;

import javax.validation.constraints.NotNull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RepositoryService {

    private final WebTarget webTarget;
    private final String mediaType;
    private final String repositoryPath;
    private final String pullRequestPath;
    private final Integer maxPerPage;
    private final String token;

    public RepositoryService(@Autowired Auth auth,
                             @Value("${url}") String url,
                             @Value("${maxPerPage}") String maxPerPage,
                             @Value("${repositoryPath}") String repositoryPath,
                             @Value("${pullRequestPath}") String pullRequestPath,
                             @Value("${mediaType}") String mediaType) {
        webTarget = ClientBuilder.newClient().target(url);
        this.mediaType = mediaType;
        this.maxPerPage = Integer.valueOf(maxPerPage);
        this.repositoryPath = repositoryPath;
        this.pullRequestPath = pullRequestPath;
        this.token = auth.getToken();
    }

    public List<Repository> getRepositories(String username) {
        return getRepositories(username, null, null, false);
    }

    public List<Repository> getRepositories(@NotNull String username, Integer page, Integer perPage, boolean listPRs) {
        Assert.hasLength(username, "Username is required");

        // Get the first page of repositories
        WebTarget repoTarget = webTarget.queryParam("page", page == null ? 1 : page)
                .queryParam("per_page", perPage == null ? maxPerPage : perPage);
        String path = String.format(repositoryPath, username);
        Response response = repoTarget.path(HtmlUtils.htmlEscape(path))
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .get();
        List<Repository> repositories = new ArrayList<>(response.readEntity(new GenericType<List<Repository>>(){}));

        // If user doesn't specify page number and first page doesn't fit all results, get the rest of the repositories
        if (perPage == null && response.hasLink("next")) {
            int lastPage = Integer.parseInt(response.getLink("last").getUri().getQuery().split("&")[0].split("=")[1]);
            repositories.addAll(getRepositories(username, 2, lastPage));
        }

        // Add pull requests to repositories
        addPullRequests(repositories, listPRs);

        // Sort with most PRs first before returning.
        // Note that this only works correctly when we have all repositories returned.
        // If the user specifies a 'page' or 'per_page', the results on that page will be sorted but results on
        // following pages may contain repositories with a larger number of PRs.
        Collections.sort(repositories);
        return repositories;
    }

    private List<Repository> getRepositories(String username, int firstPage, int lastPage) {
        List<Repository> repositories = new ArrayList<>();
        List<CompletableFuture<List<Repository>>> repositoryFutures = new ArrayList<>(maxPerPage*(1+lastPage-firstPage));
        // Make async calls to get all repositories (all available pages at max results per page)
        for (int i=firstPage; i<= lastPage; i++) {
            repositoryFutures.add(getRepositoryFutures(username, i));
        }
        for (CompletableFuture<List<Repository>> repositoryFuture : repositoryFutures) {
            repositories.addAll(repositoryFuture.join());
        }
        return repositories;
    }

    public void addPullRequests(List<Repository> repositories, boolean listPRs) {
        // Make async calls to get PRs for each repository
        List<CompletableFuture<List<PullRequest>>> pullRequestFutures = repositories.stream()
                .map(this::getPullRequestFutures)
                .collect(Collectors.toList());
        List<List<PullRequest>> pullRequestsList = pullRequestFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        for (int i = 0; i < pullRequestsList.size(); i++) {
            List<PullRequest> pullRequests = pullRequestsList.get(i);
            if (pullRequests != null) Collections.sort(pullRequests);
            if (listPRs) {
                repositories.get(i).setPullRequests(pullRequests);
            }
            repositories.get(i).setPullRequestCount(pullRequests == null ? 0 : pullRequests.size());
        }
    }

    @Async
    private CompletableFuture<List<PullRequest>> getPullRequestFutures(Repository repository) {
        String path = String.format(pullRequestPath, repository.getOwner().getUsername(), repository.getName());
        return CompletableFuture.supplyAsync(() -> webTarget.path(HtmlUtils.htmlEscape(path))
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .accept(mediaType)
                .get(new GenericType<List<PullRequest>>() {}));
    }

    @Async
    private CompletableFuture<List<Repository>> getRepositoryFutures(String username, int page) {
        String path = String.format(repositoryPath, username);
        WebTarget repoTarget = webTarget.queryParam("page", page).queryParam("per_page", maxPerPage);
        return CompletableFuture.supplyAsync(() -> repoTarget.path(HtmlUtils.htmlEscape(path))
                .request()
                .header(HttpHeaders.AUTHORIZATION, "Basic " + token)
                .accept(mediaType)
                .get(new GenericType<List<Repository>>() {}));
    }
}
