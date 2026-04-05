package com.webdocs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webdocs.service.DeepDiaryService;
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

class DeepDiaryServiceTest {

    private MockWebServer mockWebServer;
    private DeepDiaryService deepDiaryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        deepDiaryService = new DeepDiaryService(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getProjects_callsCorrectEndpoint() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"id\":\"proj1\",\"name\":\"MyProject\"}]"));

        Object result = deepDiaryService.getProjects("Bearer token").block();

        assertNotNull(result);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/projects", request.getPath());
        assertEquals("GET", request.getMethod());
        assertEquals("Bearer token", request.getHeader("Authorization"));
    }

    @Test
    void createTask_callsCorrectEndpoint() throws Exception {
        Map<String, Object> taskBody = Map.of("title", "Implement feature X", "projectId", "proj1");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(taskBody)));

        Object result = deepDiaryService.createTask(taskBody, "Bearer token").block();

        assertNotNull(result);
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/tasks", request.getPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void getTime_callsCorrectEndpoint() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        deepDiaryService.getTime("Bearer token").block();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/time", request.getPath());
        assertEquals("GET", request.getMethod());
    }

    @Test
    void getNotifications_callsCorrectEndpoint() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[]"));

        deepDiaryService.getNotifications("Bearer token").block();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/notifications", request.getPath());
        assertEquals("GET", request.getMethod());
    }

    @Test
    void deleteProject_callsCorrectEndpoint() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));

        deepDiaryService.deleteProject("proj1", "Bearer token").block();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/api/projects/proj1", request.getPath());
        assertEquals("DELETE", request.getMethod());
    }
}
