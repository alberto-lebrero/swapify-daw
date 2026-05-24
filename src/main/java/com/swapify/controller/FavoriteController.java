package com.swapify.controller;

import com.swapify.model.User;
import com.swapify.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private static final String SESSION_USER_KEY = "loggedUser";

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String viewFavorites(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        model.addAttribute("posts", favoriteService.getFavoritePosts(loggedUser.getId()));
        model.addAttribute("favoritedPostIds", favoriteService.getFavoritedPostIds(loggedUser.getId()));
        model.addAttribute("loggedUser", loggedUser);
        return "favorites";
    }

    @PostMapping("/{postId}/toggle")
    public String toggle(
            @PathVariable UUID postId,
            HttpSession session,
            @RequestHeader(value = "Referer", required = false) String referer) {

        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        favoriteService.toggle(loggedUser.getId(), postId);
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/");
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }
}
