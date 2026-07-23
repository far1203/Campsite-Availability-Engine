package com.farhan.stockalertengine.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "campsite_subscriptions")

public class CampsiteSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String campsiteId;

    @Column(nullable = false)
    private String facilityId;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "campsite_name", nullable = false)
    private String campsiteName;

    @Column(name = "campground_name", nullable = false)
    private String campgroundName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    // Constructors
    public CampsiteSubscription() {
    }

    public CampsiteSubscription(String campsiteId, String facilityId, String campsiteName, String campgroundName, AvailabilityStatus status, User user, LocalDate targetDate) {
        this.campsiteId = campsiteId;
        this.facilityId = facilityId;
        this.targetDate = targetDate;
        this.status = status;
        this.user = user;
        this.campsiteName = campsiteName;
        this.campgroundName = campgroundName;
    }

    // Getters and setters
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getCampsiteId() {return campsiteId;}
    public void setCampsiteId(String campsiteId) {this.campsiteId = campsiteId;}

    public String getFacilityId() {return facilityId;}
    public void setFacilityId(String facilityId) {this.facilityId = facilityId;}

    public AvailabilityStatus getStatus() {return status;}
    public void setStatus(AvailabilityStatus status) {this.status = status;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}

    public LocalDate getTargetDate() {return targetDate;}
    public void setTargetDate(LocalDate targetDate) {this.targetDate = targetDate;}

    public String getCampsiteName() {return campsiteName;}
    public void setCampsiteName(String campsiteName) {this.campsiteName = campsiteName;}

    public String getCampgroundName() {return campgroundName;}
    public void setCampgroundName(String campgroundName) {this.campgroundName = campgroundName;}


}
