package com.sentinelgate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SentinelGateSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String loginAndGetToken(String username, String password) throws Exception {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    @Test
    @DisplayName("UC-01 & UC-02: Operator login olup telemetri kayıtlarını okuyabilmeli")
    void operatorCanLoginAndReadTelemetry() throws Exception {
        String token = loginAndGetToken("operator", "Oper123!");

        mockMvc.perform(get("/api/v1/telemetry")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("UC-03: Sensor telemetri gönderebilmeli, yetkisiz Operator POST denemesi 403 dönmeli")
    void sensorCanPostTelemetryWhileOperatorGetsForbidden() throws Exception {
        String sensorToken = loginAndGetToken("sensor-01", "Sensor123!");
        String operatorToken = loginAndGetToken("operator", "Oper123!");

        String telemetryPayload = """
                {
                  "sensorId": "RADAR-TEST-01",
                  "facilityZone": "NORTH-SECTOR",
                  "metricType": "TEMPERATURE",
                  "metricValue": 42.5,
                  "unit": "CELSIUS",
                  "severity": "INFO"
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry")
                        .header("Authorization", "Bearer " + sensorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telemetryPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.payloadHash").isNotEmpty());

        mockMvc.perform(post("/api/v1/telemetry")
                        .header("Authorization", "Bearer " + operatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telemetryPayload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("UC-04: Sadece ADMIN audit log kayıtlarını görüntüleyebilmeli")
    void onlyAdminCanAccessAuditLogs() throws Exception {
        String adminToken = loginAndGetToken("admin", "Admin123!");
        String operatorToken = loginAndGetToken("operator", "Oper123!");

        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
