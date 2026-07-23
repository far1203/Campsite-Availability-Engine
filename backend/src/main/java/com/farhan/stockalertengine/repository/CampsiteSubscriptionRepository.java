package com.farhan.stockalertengine.repository;

import com.farhan.stockalertengine.model.CampsiteSubscription;
import com.farhan.stockalertengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.farhan.stockalertengine.model.AvailabilityStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;





public interface CampsiteSubscriptionRepository extends JpaRepository<CampsiteSubscription, Long> {



    List<CampsiteSubscription> findAllByStatus(AvailabilityStatus status);

    Optional<CampsiteSubscription> findByUserAndCampsiteIdAndTargetDate(User user, String campsiteId, LocalDate targetDate);
}
