package com.webdocs.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DeepDiaryService {

    private final WebClient deepDiaryClient;

    public DeepDiaryService(@Qualifier("deepDiaryClient") WebClient deepDiaryClient) {
        this.deepDiaryClient = deepDiaryClient;
    }

    // ---- Projects ----

    public Mono<Object> getProjects(String authHeader) {
        return get("/api/projects", authHeader);
    }

    public Mono<Object> createProject(Object body, String authHeader) {
        return post("/api/projects", body, authHeader);
    }

    public Mono<Object> getProjectById(String projectId, String authHeader) {
        return get("/api/projects/" + projectId, authHeader);
    }

    public Mono<Object> updateProject(String projectId, Object body, String authHeader) {
        return patch("/api/projects/" + projectId, body, authHeader);
    }

    public Mono<Object> deleteProject(String projectId, String authHeader) {
        return delete("/api/projects/" + projectId, authHeader);
    }

    // ---- Tasks ----

    public Mono<Object> getTasks(String authHeader) {
        return get("/api/tasks", authHeader);
    }

    public Mono<Object> createTask(Object body, String authHeader) {
        return post("/api/tasks", body, authHeader);
    }

    public Mono<Object> getTaskById(String taskId, String authHeader) {
        return get("/api/tasks/" + taskId, authHeader);
    }

    public Mono<Object> updateTask(String taskId, Object body, String authHeader) {
        return patch("/api/tasks/" + taskId, body, authHeader);
    }

    public Mono<Object> deleteTask(String taskId, String authHeader) {
        return delete("/api/tasks/" + taskId, authHeader);
    }

    // ---- Roles ----

    public Mono<Object> getRoles(String authHeader) {
        return get("/api/roles", authHeader);
    }

    public Mono<Object> createRole(Object body, String authHeader) {
        return post("/api/roles", body, authHeader);
    }

    // ---- Users (DeepDiary) ----

    public Mono<Object> getUsers(String authHeader) {
        return get("/api/users", authHeader);
    }

    public Mono<Object> createUser(Object body, String authHeader) {
        return post("/api/users", body, authHeader);
    }

    // ---- Time tracking ----

    public Mono<Object> getTime(String authHeader) {
        return get("/api/time", authHeader);
    }

    public Mono<Object> createTime(Object body, String authHeader) {
        return post("/api/time", body, authHeader);
    }

    // ---- Money diary ----

    public Mono<Object> getMoney(String username, String authHeader) {
        String uri = username != null ? "/api/money?username=" + username : "/api/money";
        return get(uri, authHeader);
    }

    public Mono<Object> getMoneyByMonth(String username, String yearMonth, String authHeader) {
        String uri = "/api/money?username=" + username + "&yearMonth=" + yearMonth;
        return get(uri, authHeader);
    }

    public Mono<Object> createMoneyEntry(Object body, String authHeader) {
        return post("/api/money", body, authHeader);
    }

    public Mono<Object> updateMoneyEntry(String entryId, Object body, String authHeader) {
        return patch("/api/money/" + entryId, body, authHeader);
    }

    public Mono<Object> deleteMoneyEntry(String entryId, String authHeader) {
        return delete("/api/money/" + entryId, authHeader);
    }

    // ---- Notifications ----

    public Mono<Object> getNotifications(String authHeader) {
        return get("/api/notifications", authHeader);
    }

    public Mono<Object> createNotification(Object body, String authHeader) {
        return post("/api/notifications", body, authHeader);
    }

    // ---- Private helpers ----

    private Mono<Object> get(String uri, String authHeader) {
        WebClient.RequestHeadersSpec<?> spec = deepDiaryClient.get().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.retrieve().bodyToMono(Object.class);
    }

    private Mono<Object> post(String uri, Object body, String authHeader) {
        WebClient.RequestBodySpec spec = deepDiaryClient.post().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.bodyValue(body != null ? body : Map.of())
                .retrieve()
                .bodyToMono(Object.class);
    }

    private Mono<Object> patch(String uri, Object body, String authHeader) {
        WebClient.RequestBodySpec spec = deepDiaryClient.patch().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.bodyValue(body != null ? body : Map.of())
                .retrieve()
                .bodyToMono(Object.class);
    }

    private Mono<Object> delete(String uri, String authHeader) {
        WebClient.RequestHeadersSpec<?> spec = deepDiaryClient.delete().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.retrieve().bodyToMono(Object.class);
    }
}
