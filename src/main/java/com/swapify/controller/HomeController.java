package com.swapify.controller;

import com.swapify.model.User;
import com.swapify.service.FavoriteService;
import com.swapify.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final String SESSION_USER_KEY = "loggedUser";

    private final PostService postService;
    private final FavoriteService favoriteService;

    public HomeController(PostService postService, FavoriteService favoriteService) {
        this.postService = postService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute(SESSION_USER_KEY);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("posts", postService.findAvailable());
        if (loggedUser != null) {
            model.addAttribute("favoritedPostIds", favoriteService.getFavoritedPostIds(loggedUser.getId()));
        }
        return "home";
    }
}