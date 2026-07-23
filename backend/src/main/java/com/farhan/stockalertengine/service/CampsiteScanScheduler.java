package com.farhan.stockalertengine.service;

import com.farhan.stockalertengine.model.AvailabilityStatus;
import com.farhan.stockalertengine.model.CampsiteSubscription;
import com.farhan.stockalertengine.repository.CampsiteSubscriptionRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CampsiteScanScheduler {

    private final CampsiteSubscriptionRepository repository;
    private final CampsiteAvailabilityService availabilityService;
    private final JavaMailSender mailSender;

    public CampsiteScanScheduler(CampsiteSubscriptionRepository repository,
                                 CampsiteAvailabilityService availabilityService,
                                 JavaMailSender mailSender) {
        this.repository = repository;
        this.availabilityService = availabilityService;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 300000)
    public void scanTrackedCampsites() {
        System.out.println("Background scan engine started...");

        List<CampsiteSubscription> subscriptions = repository.findAllByStatus(AvailabilityStatus.RESERVED);

        for (CampsiteSubscription sub : subscriptions) {
            try {




                if (sub.getTargetDate().isBefore(java.time.LocalDate.now())) {
                    System.out.println("LOG: Subscription ID " + sub.getId() + " has naturally passed. Archiving as EXPIRED.");

                    sub.setStatus(AvailabilityStatus.EXPIRED);
                    repository.save(sub);

                    continue;

                }

                Thread.sleep(2000);


                AvailabilityStatus currentLiveStatus = availabilityService.checkAvailability(
                        sub.getFacilityId(),
                        sub.getCampsiteId(),
                        sub.getTargetDate()
                );

                if (currentLiveStatus == AvailabilityStatus.AVAILABLE) {
                    System.out.println("CANCELLATION DETECTED! Site " + sub.getCampsiteId() + " is open!");

                    sub.setStatus(AvailabilityStatus.AVAILABLE);
                    repository.save(sub);

                    // notification hook

                    SimpleMailMessage message = new SimpleMailMessage();

                    message.setTo(sub.getUser().getEmail());

                    message.setSubject("⛺Campsite Cancellation Caught!");

                    String bookingUrl = "https://recreation.gov/camping/campgrounds/" + sub.getFacilityId();


                    String emailBodyText = "Great news!\n\n" +
                            "A cancellation was just detected for your tracked campsite spot.\n\n" +
                            "• Campground: " + sub.getCampgroundName() + "\n" +
                            "• Campsite Number: " + sub.getCampsiteName() + "\n" +
                            "• Target Date: " + sub.getTargetDate() + "\n\n" +
                            "Click the link below to open the campground map and claim your reservation before someone else grabs it:\n" +
                            bookingUrl + "\n\n" +
                            "Happy Camping,\nYour Automated Tracker Bot";

                    message.setText(emailBodyText);
                    mailSender.send(message);
                }










            } catch (Exception e) {
                System.err.println("Skipping subscription row ID" + sub.getId() + " due to API error: " + e.getMessage());
            }
        }
    }


}
