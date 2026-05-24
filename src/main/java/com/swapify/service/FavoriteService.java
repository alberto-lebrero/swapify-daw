package com.swapify.service;

import com.swapify.model.Favorite;
import com.swapify.model.Post;
import com.swapify.model.User;
import com.swapify.repository.FavoriteRepository;
import com.swapify.repository.PostRepository;
import com.swapify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           PostRepository postRepository,
                           UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggle(UUID userId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalStateException("Publicación no encontrada"));

        if (post.getUser().getId().equals(userId)) return;

        if (favoriteRepository.existsByUserIdAndPostId(userId, postId)) {
            favoriteRepository.deleteByUserIdAndPostId(userId, postId);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
            favoriteRepository.save(Favorite.builder().user(user).post(post).build());
        }
    }

    @Transactional(readOnly = true)
    public List<Post> getFavoritePosts(UUID userId) {
        return favoriteRepository.findByUserIdOrderBySavedAtDesc(userId)
                .stream()
                .map(Favorite::getPost)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Set<UUID> getFavoritedPostIds(UUID userId) {
        return favoriteRepository.findByUserIdOrderBySavedAtDesc(userId)
                .stream()
                .map(f -> f.getPost().getId())
                .collect(Collectors.toSet());
    }
}
