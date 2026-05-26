package com.swapify.controller;

import com.swapify.model.Profile;
import com.swapify.model.User;
import com.swapify.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final String SESSION_USER_KEY = "loggedUser";

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }



    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        Profile profile = profileService.getOrCreate(loggedUser.getId());

        String fullName = buildFullName(profile.getFirstName(), profile.getLastName(), loggedUser.getUsername());
        String avatarInitial = loggedUser.getUsername().substring(0, 1).toUpperCase();

        model.addAttribute("profile", profile);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("displayName", fullName);
        model.addAttribute("avatarInitial", avatarInitial);

        return "profile";
    }


    @GetMapping("/edit")
    public String showEditForm(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        model.addAttribute("profileForm", profileService.getOrCreate(loggedUser.getId()));
        model.addAttribute("loggedUser", loggedUser);

        return "profile-edit";
    }


    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute("profileForm") Profile profileForm,
            BindingResult errors,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        profileService.update(loggedUser.getId(), profileForm);
        redirectAttrs.addFlashAttribute("successMsg", "Perfil actualizado correctamente.");
        return "redirect:/profile";
    }


    @PostMapping("/delete")
    public String deleteProfile(HttpSession session, RedirectAttributes redirectAttrs) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        profileService.delete(loggedUser.getId());
        redirectAttrs.addFlashAttribute("successMsg", "Los datos del perfil han sido eliminados.");
        return "redirect:/profile";
    }

    // Utilidades

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    private String buildFullName(String firstName, String lastName, String fallback) {
        String name = ((firstName != null ? firstName + " " : "") + (lastName != null ? lastName : "")).trim();
        return name.isEmpty() ? fallback : name;
    }
}