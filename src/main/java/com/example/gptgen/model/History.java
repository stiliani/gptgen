package com.example.gptgen.model;

import jakarta.persistence.*;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promptid;

    @Column(nullable = false,columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String prompt;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String response;

    @Column(nullable = false)
    private Long userId;

    public History() {}

    public History(String prompt, String response, Long userId) {
        this.prompt = prompt;
        this.response = response;
        this.userId = userId;
    }

    public Long getId() {
        return promptid;
    }
    public void setId(Long id) {
        this.promptid = id;
    }
    public String getPrompt() {
        return prompt;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}