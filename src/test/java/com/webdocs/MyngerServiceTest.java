package com.webdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webdocs.service.MyngerService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyngerServiceTest {

    private MockWebServer mockWebServer;
    private MyngerService myngerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        myngerService = new MyngerService(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void signIn_returnsExpectedResponse() throws Exception {
        Map<String, Object> responseBody = Map.of("token", "abc123", "user", "john");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(responseBody)));

        Object result = myngerService.signIn(Map.of("username", "john", "password", "secret"), null).block();

        assertNotNull(result);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/auth/signin", request.getPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void getUsers_returnsList() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"username\":\"alice\"},{\"username\":\"bob\"}]"));

        Object result = myngerService.getUsers("Bearer test-token").block();

        assertNotNull(result);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/users", request.getPath());
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer test-token", request.getHeader("Authorization"));
    }

    @Test
    void sendOtp_isCalledWithCorrectPath() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"message\":\"OTP sent\"}"));

        Object result = myngerService.sendOtp(Map.of("email", "test@example.com"), "Bearer token").block();

        assertNotNull(result);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/notifications/send-otp", request.getPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void getRooms_isCalledWithCorrectPath() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        myngerService.getRooms("Bearer token").block();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/rooms", request.getPath());
        assertEquals("GET", request.getMethod());
    }

    @Test
    void getPresignedUrl_isCalledWithCorrectPath() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("{\"url\":\"https://s3.example.com/presigned\"}"));

        myngerService.getPresignedUrl(Map.of("filename", "photo.jpg"), "Bearer token").block();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/files/presigned-url", request.getPath());
        assertEquals("POST", request.getMethod());
    }
}
