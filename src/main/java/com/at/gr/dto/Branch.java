package com.at.gr.dto;

public record Branch(String name, Commit commit) {

    public record Commit(String sha) {
    }
}
