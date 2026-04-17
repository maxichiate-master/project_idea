package com.driverapp.repository;

import com.driverapp.model.Rating;
import com.driverapp.model.TripRequest;
import com.driverapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByTripAndRater(TripRequest trip, User rater);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.rated = :user")
    Double findAverageScoreByRated(@Param("user") User user);
}
