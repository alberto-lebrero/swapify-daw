package com.swapify.controller;

import com.swapify.model.Image;
import com.swapify.model.Post;
import com.swapify.model.User;
import com.swapify.repository.CategoryRepository;
import com.swapify.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/posts")
public class PostController {

    private static final String SESSION_USER_KEY = "loggedUser";

    private final PostService postService;
    private final CategoryRepository categoryRepository;

    public PostController(PostService postService, CategoryRepository categoryRepository) {
        this.postService = postService;
        this.categoryRepository = categoryRepository;
    }

    // ── LIST ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String listPosts(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        model.addAttribute("posts", postService.findByUser(loggedUser.getId()));
        model.addAttribute("loggedUser", loggedUser);
        return "posts";
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String showCreateForm(HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        model.addAttribute("postForm", new Post());
        model.addAttribute("isNew", true);
        populateFormModel(model, loggedUser);
        return "post-edit";
    }

    @PostMapping
    public String createPost(
            @Valid @ModelAttribute("postForm") Post post,
            BindingResult errors,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttrs) {

        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        if (errors.hasErrors()) {
            model.addAttribute("isNew", true);
            populateFormModel(model, loggedUser);
            return "post-edit";
        }

        postService.create(loggedUser.getId(), post);
        redirectAttrs.addFlashAttribute("successMsg", "Publicación creada correctamente.");
        return "redirect:/posts";
    }

    // ── EDIT ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, HttpSession session, Model model) {
        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        Post post = postService.findOwnedOrThrow(id, loggedUser.getId());

        // Poblar campos @Transient para el formulario
        List<String> imageUrls = post.getImages().stream()
                .sorted(Comparator.comparingInt(Image::getDisplayOrder))
                .map(Image::getUrl)
                .collect(Collectors.toList());
        post.setImageUrls(imageUrls);
        post.setCategoryId(post.getCategory() != null ? post.getCategory().getId() : null);

        model.addAttribute("postForm", post);
        model.addAttribute("postId", id);
        model.addAttribute("isNew", false);
        populateFormModel(model, loggedUser);
        return "post-edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(
            @PathVariable UUID id,
            @Valid @ModelAttribute("postForm") Post post,
            BindingResult errors,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttrs) {

        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        if (errors.hasErrors()) {
            model.addAttribute("postId", id);
            model.addAttribute("isNew", false);
            populateFormModel(model, loggedUser);
            return "post-edit";
        }

        postService.update(id, loggedUser.getId(), post);
        redirectAttrs.addFlashAttribute("successMsg", "Publicación actualizada correctamente.");
        return "redirect:/posts";
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String deletePost(
            @PathVariable UUID id,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        User loggedUser = getLoggedUser(session);
        if (loggedUser == null) return "redirect:/users/login";

        postService.delete(id, loggedUser.getId());
        redirectAttrs.addFlashAttribute("successMsg", "Publicación eliminada correctamente.");
        return "redirect:/posts";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void populateFormModel(Model model, User loggedUser) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("postTypes", Post.PostType.values());
        model.addAttribute("postStatuses", Post.PostStatus.values());
        model.addAttribute("loggedUser", loggedUser);
    }

    private User getLoggedUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }
}