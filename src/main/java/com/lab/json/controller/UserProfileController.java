package com.lab.json.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lab.json.entity.UserProfile;
import com.lab.json.repository.UserProfileRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5500")
public class UserProfileController {

    @Autowired
    private UserProfileRepository repository;

    @PostMapping
    public UserProfile create(@RequestBody UserProfile profile, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        profile.setUserId(userId);
        return repository.save(profile);
    }

    @GetMapping("/{id}")
    public UserProfile get(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public UserProfile update(@PathVariable Long id, @RequestBody UserProfile updated) {
        UserProfile p = repository.findById(id).orElseThrow();
        p.setName(updated.getName());
        p.setEmail(updated.getEmail());
        p.setBio(updated.getBio());
        p.setPhone(updated.getPhone());
        p.setImageUrl(updated.getImageUrl());
        return repository.save(p);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "Deleted";
    }
}