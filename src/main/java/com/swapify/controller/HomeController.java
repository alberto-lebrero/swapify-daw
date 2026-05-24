package com.swapify.controller;

import com.swapify.model.Post;
import com.swapify.model.User;
import com.swapify.repository.CategoryRepository;
import com.swapify.service.FavoriteService;
import com.swapify.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class HomeController {

    private static final String SESSION_USER_KEY = "loggedUser";

    private final PostService postService;
    private final FavoriteService favoriteService;
    private final CategoryRepository categoryRepository;

    public HomeController(PostService postService, FavoriteService favoriteService, CategoryRepository categoryRepository) {
        this.postService = postService;
        this.favoriteService = favoriteService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) UUID categoryId,
            HttpSession session, Model model) {

        User loggedUser = (User) session.getAttribute(SESSION_USER_KEY);
        Post.PostType postType = (type != null && !type.isBlank()) ? Post.PostType.valueOf(type) : null;

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("posts", postService.findAvailableFiltered(postType, categoryId));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedType", postType);
        model.addAttribute("selectedCategoryId", categoryId);

        if (loggedUser != null) {
            model.addAttribute("favoritedPostIds", favoriteService.getFavoritedPostIds(loggedUser.getId()));
        }
        return "home";
    }
}