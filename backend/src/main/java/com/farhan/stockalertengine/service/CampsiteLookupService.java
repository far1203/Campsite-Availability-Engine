package com.farhan.stockalertengine.service;

import com.farhan.stockalertengine.dto.CampsiteMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
@Service
public class CampsiteLookupService {

    private final RestClient restClient;

    public CampsiteLookupService(RestClient restClient) {
        this.restClient = restClient;
    }

    public CampsiteMetadata lookupIdsByName(String name, String state, String siteNumber) {

        // fetch facility

        JsonNode response = restClient.get()
                .uri("/facilities?query={query}&state={state}&limit=1", name, state)
                .retrieve()
                .body(JsonNode.class);

        JsonNode results = response.get("RECDATA");

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("No campgrounds found matching: " + name + " in " + state);
        }

        JsonNode firstMatch = results.get(0);
        String facilityId = firstMatch.get("FacilityID").asText();

        // fetch campsite

        JsonNode campsiteResponse = restClient.get()
                .uri("/facilities/" + facilityId + "/campsites?limit=100")
                .retrieve()
                .body(JsonNode.class);

        JsonNode campsiteResults = campsiteResponse.get("RECDATA");
        if (campsiteResults == null || campsiteResults.isEmpty()) {
            throw new RuntimeException("No individual campsites found in campground ID: " + facilityId);
        }

        String campsiteId = null;

        for (JsonNode site : campsiteResults) {
            String currentSiteName = site.get("CampsiteName").asText();


            if (currentSiteName.equalsIgnoreCase(siteNumber)) {

                boolean isReservable = site.get("CampsiteReservable").asBoolean();
                if (!isReservable) {
                    throw new RuntimeException("Campsite " + currentSiteName + " is a closed, locked, or walk-in only site and cannot be tracked.");
                }

                String siteType = site.get("CampsiteType").asText();
                if (siteType != null && siteType.equalsIgnoreCase("MANAGEMENT")) {
                    throw new RuntimeException("Campsite " + currentSiteName + " is a designated park management ground and cannot be tracked.");
                }



                campsiteId = site.get("CampsiteID").asText();
                break;
            }
        }

        if (campsiteId == null) {
            throw new RuntimeException("Site number " + siteNumber + " does not exist at " + name);
        }


        return new CampsiteMetadata(campsiteId, facilityId);

    }

}
