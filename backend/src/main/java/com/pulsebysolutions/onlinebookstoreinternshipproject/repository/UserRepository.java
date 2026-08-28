package com.pulsebysolutions.onlinebookstoreinternshipproject.repository;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByEmail(String email);
}
