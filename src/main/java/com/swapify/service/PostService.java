package com.swapify.service;

import com.swapify.model.Category;
import com.swapify.model.Image;
import com.swapify.model.Post;
import com.swapify.model.User;
import com.swapify.repository.CategoryRepository;
import com.swapify.repository.PostRepository;
import com.swapify.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Post> findAvailable() {
        return postRepository.findByStatusOrderByCreatedAtDesc(Post.PostStatus.AVAILABLE);
    }

    public List<Post> findAvailableFiltered(Post.PostType type, UUID categoryId) {
        if (type != null && categoryId != null)
            return postRepository.findByStatusAndTypeAndCategory_IdOrderByCreatedAtDesc(Post.PostStatus.AVAILABLE, type, categoryId);
        if (type != null)
            return postRepository.findByStatusAndTypeOrderByCreatedAtDesc(Post.PostStatus.AVAILABLE, type);
        if (categoryId != null)
            return postRepository.findByStatusAndCategory_IdOrderByCreatedAtDesc(Post.PostStatus.AVAILABLE, categoryId);
        return postRepository.findByStatusOrderByCreatedAtDesc(Post.PostStatus.AVAILABLE);
    }

    public List<Post> findByUser(UUID userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Post findOwnedOrThrow(UUID postId, UUID userId) {
        return postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Publicación no encontrada o sin permisos"));
    }

    @Transactional
    public Post create(UUID userId, Post formPost) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        Category category = resolveCategory(formPost.getCategoryId());
        Post post = Post.builder()
                .user(user)
                .title(formPost.getTitle())
                .description(formPost.getDescription())
                .type(formPost.getType() != null ? formPost.getType() : Post.PostType.GOOD)
                .status(Post.PostStatus.AVAILABLE)
                .category(category)
                .build();
        applyImages(post, formPost.getImageUrls());
        return postRepository.save(post);
    }

    @Transactional
    public Post update(UUID postId, UUID userId, Post formPost) {
        Post post = findOwnedOrThrow(postId, userId);
        Category category = resolveCategory(formPost.getCategoryId());
        post.setTitle(formPost.getTitle());
        post.setDescription(formPost.getDescription());
        post.setType(formPost.getType() != null ? formPost.getType() : Post.PostType.GOOD);
        post.setStatus(formPost.getStatus() != null ? formPost.getStatus() : Post.PostStatus.AVAILABLE);
        post.setCategory(category);
        post.getImages().clear();
        applyImages(post, formPost.getImageUrls());
        return postRepository.save(post);
    }

    @Transactional
    public void delete(UUID postId, UUID userId) {
        postRepository.delete(findOwnedOrThrow(postId, userId));
    }

    private Category resolveCategory(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId).orElse(null);
    }

    private void applyImages(Post post, List<String> imageUrls) {
        if (imageUrls == null) return;
        int order = 0;
        for (String url : imageUrls) {
            if (url != null && !url.isBlank()) {
                post.getImages().add(Image.builder()
                        .post(post)
                        .url(url.trim())
                        .displayOrder(order++)
                        .build());
            }
        }
    }
}