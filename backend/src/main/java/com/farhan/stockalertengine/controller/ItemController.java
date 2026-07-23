package com.farhan.stockalertengine.controller;

import java.time.LocalDate;
import java.util.Optional;
import com.farhan.stockalertengine.dto.*;
import com.farhan.stockalertengine.model.AvailabilityStatus;
import com.farhan.stockalertengine.model.CampsiteSubscription;
import com.farhan.stockalertengine.model.User;
import com.farhan.stockalertengine.repository.CampsiteSubscriptionRepository;
import com.farhan.stockalertengine.repository.UserRepository;
import com.farhan.stockalertengine.service.CampsiteAvailabilityService;
import com.farhan.stockalertengine.service.CampsiteLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@CrossOrigin(origins = "https://far1203.github.io")
@RestController
@RequestMapping("/api/v1/subscriptions")
public class ItemController {


    private final UserRepository userRepository;
    private final CampsiteSubscriptionRepository campsiteSubscriptionRepository;
    private final CampsiteLookupService lookupService;
    private final CampsiteAvailabilityService availabilityService;

    public ItemController(UserRepository userRepository, CampsiteSubscriptionRepository campsiteSubscriptionRepository,
                          CampsiteLookupService lookupService, CampsiteAvailabilityService availabilityService) {
        this.userRepository = userRepository;
        this.campsiteSubscriptionRepository = campsiteSubscriptionRepository;
        this.lookupService = lookupService;
        this.availabilityService = availabilityService;
    }

    @PostMapping("/track")
    public ResponseEntity<String> trackCampsite(@Valid @RequestBody CampsiteSubscriptionRequest request) {
        try {
            CampsiteMetadata metadata = lookupService.lookupIdsByName(
                    request.getCampgroundName(),
                    request.getState(),
                    request.getCampsiteNumber()
            );

            LocalDate targetTrackingDate = LocalDate.parse(request.getTargetDate());

            if (targetTrackingDate.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Tracking Failed: Cannot track a date that has already passed!");
            }

            User user;
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = new User(request.getEmail());
                userRepository.save(user);
            }

            Optional<CampsiteSubscription> duplicateCheck = campsiteSubscriptionRepository
                    .findByUserAndCampsiteIdAndTargetDate(user, metadata.campsiteId(), targetTrackingDate);

            if (duplicateCheck.isPresent()) {
                return ResponseEntity.badRequest().body("Tracking Duplicate: You are already tracking site " +
                        request.getCampsiteNumber() + " for " + targetTrackingDate + "!");
            }


            AvailabilityStatus initialStatus = availabilityService.checkAvailability(
                    metadata.facilityId(),
                    metadata.campsiteId(),
                    targetTrackingDate
            );

            CampsiteSubscription subscription = new CampsiteSubscription();
            subscription.setCampsiteId(metadata.campsiteId());
            subscription.setFacilityId(metadata.facilityId());
            subscription.setUser(user);
            subscription.setTargetDate(targetTrackingDate);
            subscription.setStatus(initialStatus);
            subscription.setCampsiteName(request.getCampsiteNumber());
            subscription.setCampgroundName(request.getCampgroundName());



            campsiteSubscriptionRepository.save(subscription);


            if (initialStatus == AvailabilityStatus.AVAILABLE) {
                return ResponseEntity.ok("Milestone: Site " + request.getCampsiteNumber() +
                        " is actually AVAILABLE right now! Go book it on Recreation.gov!");
            }

            return ResponseEntity.ok("Successfully tracking site " + request.getCampsiteNumber() +
                    " at " + request.getCampgroundName() + " for " + targetTrackingDate + ". " +
                    "Current status is RESERVED. Background scanner will look for cancellations.");


        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
