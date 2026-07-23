package com.farhan.stockalertengine.service;


import com.farhan.stockalertengine.model.AvailabilityStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CampsiteAvailabilityService {

    private final RestClient liveClient;

    public CampsiteAvailabilityService() {
        this.liveClient = RestClient.builder()
                .baseUrl("https://www.recreation.gov")
                .build();
    }

    public AvailabilityStatus checkAvailability(String facilityId, String campsiteId, LocalDate date) {

        String startOfMonth = date.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + "T00:00:00.000Z";

        JsonNode response = liveClient.get()
                .uri("/api/camps/availability/campground/{facilityId}/month?start_date={startDate}",
                        facilityId, startOfMonth)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || response.get("campsites") == null) {
            throw new RuntimeException("Empty response or missing campsites container from server.");
        }

        JsonNode campsite = response.get("campsites").get(campsiteId);

        if (campsite == null) {
            throw new RuntimeException("Campsite not found in response: " + campsiteId);
        }

        JsonNode calendarGrid = campsite.get("availabilities");
        if (calendarGrid == null) {
            throw new RuntimeException("Missing calendar grid matrix for campsite: " + campsiteId);
        }

        String targetDateKey = date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T00:00:00Z";

        JsonNode statusNode = calendarGrid.get(targetDateKey);

        if (statusNode == null) {
            throw new RuntimeException("No availability data for date: " + targetDateKey);
        }

        String status = statusNode.asText();


        return status.equals("Available") ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.RESERVED;


    }
}
