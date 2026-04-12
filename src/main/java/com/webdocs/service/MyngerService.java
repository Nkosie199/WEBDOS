package com.webdocs.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class MyngerService {

    private final WebClient myngerClient;

    public MyngerService(@Qualifier("myngerClient") WebClient myngerClient) {
        this.myngerClient = myngerClient;
    }

    // ---- Auth ----

    public Mono<Object> signIn(Object body, String authHeader) {
        return post("/api/auth/signin", body, authHeader);
    }

    public Mono<Object> signUp(Object body, String authHeader) {
        return post("/api/auth/signup", body, authHeader);
    }

    public Mono<Object> confirmSignUp(Object body, String authHeader) {
        return post("/api/auth/confirm-signup", body, authHeader);
    }

    // ---- Notifications ----

    public Mono<Object> sendOtp(Object body, String authHeader) {
        return post("/api/notifications/send-otp", body, authHeader);
    }

    public Mono<Object> verifyOtp(Object body, String authHeader) {
        return post("/api/notifications/verify-otp", body, authHeader);
    }

    public Mono<Object> forgotPassword(Object body, String authHeader) {
        return post("/api/notifications/forgot-password", body, authHeader);
    }

    public Mono<Object> sendEmail(Object body, String authHeader) {
        return post("/api/notifications/send-email", body, authHeader);
    }

    // ---- Users ----

    public Mono<Object> getUsers(String authHeader) {
        return get("/api/users", authHeader);
    }

    public Mono<Object> createUser(Object body, String authHeader) {
        return post("/api/users", body, authHeader);
    }

    public Mono<Object> getUserByUsername(String username, String authHeader) {
        return get("/api/users/" + username, authHeader);
    }

    public Mono<Object> updateUser(String username, Object body, String authHeader) {
        return patch("/api/users/" + username, body, authHeader);
    }

    public Mono<Object> deleteUser(String username, String authHeader) {
        return delete("/api/users/" + username, authHeader);
    }

    // ---- Messages ----

    public Mono<Object> getMessages(String authHeader) {
        return get("/api/messages", authHeader);
    }

    public Mono<Object> createMessage(Object body, String authHeader) {
        return post("/api/messages", body, authHeader);
    }

    public Mono<Object> getMessage(String messageId, String authHeader) {
        return get("/api/messages/" + messageId, authHeader);
    }

    public Mono<Object> updateMessage(String messageId, Object body, String authHeader) {
        return patch("/api/messages/" + messageId, body, authHeader);
    }

    public Mono<Object> deleteMessage(String messageId, String authHeader) {
        return delete("/api/messages/" + messageId, authHeader);
    }

    // ---- Rooms ----

    public Mono<Object> getRooms(String authHeader) {
        return get("/api/rooms", authHeader);
    }

    public Mono<Object> createRoom(Object body, String authHeader) {
        return post("/api/rooms", body, authHeader);
    }

    public Mono<Object> getRoom(String roomId, String authHeader) {
        return get("/api/rooms/" + roomId, authHeader);
    }

    public Mono<Object> updateRoom(String roomId, Object body, String authHeader) {
        return patch("/api/rooms/" + roomId, body, authHeader);
    }

    public Mono<Object> deleteRoom(String roomId, String authHeader) {
        return delete("/api/rooms/" + roomId, authHeader);
    }

    // ---- Files ----

    public Mono<Object> getPresignedUrl(Object body, String authHeader) {
        return post("/api/files/presigned-url", body, authHeader);
    }

    // ---- Storage (S3) — endpoints use {userId} as path variable ----

    /** GET /api/storage/files/{userId} — list all files for a user */
    public Mono<Object> listFiles(String userId, String authHeader) {
        return get("/api/storage/files/" + userId, authHeader);
    }

    /** GET /api/storage/recent/{userId}?limit=N — recent files */
    public Mono<Object> getRecentFiles(String userId, int limit, String authHeader) {
        return get("/api/storage/recent/" + userId + "?limit=" + limit, authHeader);
    }

    /** GET /api/storage/dashboard/{userId} — full dashboard data (DashboardData) */
    public Mono<Object> getDashboardData(String userId, String authHeader) {
        return get("/api/storage/dashboard/" + userId, authHeader);
    }

    /** GET /api/storage/count/{userId} — total file count */
    public Mono<Object> getFileCount(String userId, String authHeader) {
        return get("/api/storage/count/" + userId, authHeader);
    }

    /** GET /api/storage/size/{userId} — total used size in MB */
    public Mono<Object> getTotalSize(String userId, String authHeader) {
        return get("/api/storage/size/" + userId, authHeader);
    }

    /** GET /api/storage/limit/{userId} — storage limit in MB */
    public Mono<Object> getStorageLimit(String userId, String authHeader) {
        return get("/api/storage/limit/" + userId, authHeader);
    }

    /** GET /api/storage/analytics/{userId} — uploads per day map */
    public Mono<Object> getAnalytics(String userId, String authHeader) {
        return get("/api/storage/analytics/" + userId, authHeader);
    }

    /** GET /api/storage/types/{userId} — file type breakdown map */
    public Mono<Object> getFileTypes(String userId, String authHeader) {
        return get("/api/storage/types/" + userId, authHeader);
    }

    /** POST /api/files/presigned-url — get presigned S3 upload URL */
    public Mono<Object> getPresignedUploadUrl(Object body, String authHeader) {
        return post("/api/files/presigned-url", body, authHeader);
    }

    /** GET /api/stream/token?userId={userId} — get a stream chat token for a user */
    public Mono<Object> getStreamToken(String userId) {
        return get("/api/stream/token?userId=" + userId, null);
    }

    // ---- Transactions ----

    public Mono<Object> getUserTransactions(String username, String authHeader) {
        return get("/api/transactions?username=" + username, authHeader);
    }

    // ---- Camel / misc ----

    public Mono<Object> sendToCamel(Object body, String authHeader) {
        return post("/api/camel", body, authHeader);
    }

    // ---- Private helpers ----

    private Mono<Object> get(String uri, String authHeader) {
        WebClient.RequestHeadersSpec<?> spec = myngerClient.get().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.retrieve().bodyToMono(Object.class);
    }

    private Mono<Object> post(String uri, Object body, String authHeader) {
        WebClient.RequestBodySpec spec = myngerClient.post().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.bodyValue(body != null ? body : Map.of())
                .retrieve()
                .bodyToMono(Object.class);
    }

    private Mono<Object> patch(String uri, Object body, String authHeader) {
        WebClient.RequestBodySpec spec = myngerClient.patch().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.bodyValue(body != null ? body : Map.of())
                .retrieve()
                .bodyToMono(Object.class);
    }

    private Mono<Object> delete(String uri, String authHeader) {
        WebClient.RequestHeadersSpec<?> spec = myngerClient.delete().uri(uri);
        if (authHeader != null) {
            spec = spec.header("Authorization", authHeader);
        }
        return spec.retrieve().bodyToMono(Object.class);
    }
}
