package com.servicenow.prhawk.controller;

import com.servicenow.prhawk.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotEmpty;

@Controller
public class WebController {

    private final RepositoryService repositoryService;

    @Autowired
    public WebController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/")
    public String getMain() {
        return "prhawk";
    }

    @GetMapping("/user/{username}")
    public String getRepos(@PathVariable @NotEmpty String username,
                           @RequestParam(required = false) Integer page,
                           @RequestParam(required = false) Integer perPage,
                           @RequestParam(required = false, defaultValue = "false") boolean listPRs,
                           Model model) {
        // add input validation, error handling
        model.addAttribute("repos", repositoryService.getRepositories(username, page, perPage, listPRs));
        return "repos";
    }
}
