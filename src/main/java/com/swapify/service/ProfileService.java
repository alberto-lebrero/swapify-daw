package com.swapify.service;

import com.swapify.model.Profile;
import com.swapify.model.User;
import com.swapify.repository.ProfileRepository;
import com.swapify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Profile getOrCreate(UUID userId) {
        return profileRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + userId));
            return profileRepository.save(Profile.builder().user(user).build());
        });
    }

    @Transactional
    public Profile update(UUID userId, Profile profileForm) {
        Profile profile = getOrCreate(userId);
        profile.setFirstName(profileForm.getFirstName());
        profile.setLastName(profileForm.getLastName());
        profile.setBio(profileForm.getBio());
        profile.setCity(profileForm.getCity());
        profile.setAvatarUrl(profileForm.getAvatarUrl());
        return profileRepository.save(profile);
    }

    @Transactional
    public void delete(UUID userId) {
        profileRepository.findByUserId(userId).ifPresent(profileRepository::delete);
    }
}