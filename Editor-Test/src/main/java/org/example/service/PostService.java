package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.Post;
import org.example.dto.PostRequest;
import org.example.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post save(PostRequest request) {
        return this.postRepository.save(request.toEntity());
    }

    public List<Post> findAll() {
        return this.postRepository.findAll();
    }

    public Post findById(Long id) {
        return this.postRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found"));
    }
}
