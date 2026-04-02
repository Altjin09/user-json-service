package com.lab.json.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lab.json.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
}