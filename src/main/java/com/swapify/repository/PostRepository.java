package com.swapify.repository;

import com.swapify.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findByStatusOrderByCreatedAtDesc(Post.PostStatus status);

    List<Post> findByStatusAndTypeOrderByCreatedAtDesc(Post.PostStatus status, Post.PostType type);

    List<Post> findByStatusAndCategory_IdOrderByCreatedAtDesc(Post.PostStatus status, UUID categoryId);

    List<Post> findByStatusAndTypeAndCategory_IdOrderByCreatedAtDesc(Post.PostStatus status, Post.PostType type, UUID categoryId);

    List<Post> findByStatusAndUserIdNotOrderByCreatedAtDesc(Post.PostStatus status, UUID userId);

    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Post> findByIdAndUserId(UUID id, UUID userId);
}
