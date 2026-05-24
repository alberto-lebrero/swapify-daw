package com.swapify.controller;

import com.swapify.model.User;
import com.swapify.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private static final String SESSION_USER_KEY = "loggedUser";
    private static final int SESSION_ONE_WEEK = 7 * 24 * 60 * 60;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ── Registro ─────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String showRegister(Model model, HttpSession session) {
        if (session.getAttribute(SESSION_USER_KEY) != null) return "redirect:/";
        model.addAttribute("registerForm", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") User registerForm,
            BindingResult errors,
            Model model,
            HttpSession session) {

        if (errors.hasErrors()) return "register";

        try {
            User user = userService.register(
                registerForm.getUsername(),
                registerForm.getEmail(),
                registerForm.getPassword(),
                registerForm.getConfirmPassword()
            );
            session.setAttribute(SESSION_USER_KEY, user);
            session.setMaxInactiveInterval(SESSION_ONE_WEEK);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "register";
        }
    }

    // ── Login ────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        if (session.getAttribute(SESSION_USER_KEY) != null) return "redirect:/";
        model.addAttribute("loginForm", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("loginForm") User loginForm,
            BindingResult errors,
            Model model,
            HttpSession session) {

        try {
            User user = userService.login(loginForm.getEmail(), loginForm.getPassword());
            session.setAttribute(SESSION_USER_KEY, user);
            session.setMaxInactiveInterval(SESSION_ONE_WEEK);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "login";
        }
    }

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute(SESSION_USER_KEY) == null) return "redirect:/users/login";
        return "redirect:/profile";
    }

    // ── Logout ───────────────────────────────────────────────────────────────

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ── Eliminar cuenta ──────────────────────────────────────────────────────

    @PostMapping("/delete")
    public String deleteAccount(HttpSession session) {
        User loggedUser = (User) session.getAttribute(SESSION_USER_KEY);
        if (loggedUser == null) return "redirect:/users/login";

        userService.deleteAccount(loggedUser.getId());
        session.invalidate();
        return "redirect:/";
    }
}