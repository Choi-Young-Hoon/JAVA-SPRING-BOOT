package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.domain.Post;
import org.example.dto.PostRequest;
import org.example.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/new-post")
    public String newPost() {
        return "newPost";
    }

    @PostMapping("/new-post")
    public String createPost(PostRequest postRequest) {
        this.postService.save(postRequest);
        return "redirect:/post";
    }

    @GetMapping("/post")
    public String showPost(Model model) {
        Post post = this.postService.findById(1L);

        model.addAttribute("post", post);
        return "viewPost";
    }


}
