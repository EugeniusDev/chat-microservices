package com.edu.froliak.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/config-test")
@RefreshScope
public class ConfigTestController {

    @Value("${project.title:Default Project Title}")
    private String projectTitle;

    @Value("${user-service-specific.greeting:Default Greeting}")
    private String specificGreeting;

    @Value("${user-service-specific.max-users:0}")
    private int maxUsers;


    @GetMapping("/project-title")
    public ResponseEntity<String> getProjectTitle() {
        return ResponseEntity.ok("Project Title from Config Server: " + projectTitle);
    }

    @GetMapping("/greeting")
    public ResponseEntity<String> getSpecificGreeting() {
        return ResponseEntity.ok("Specific Greeting: " + specificGreeting + ", Max Users: " + maxUsers);
    }
}