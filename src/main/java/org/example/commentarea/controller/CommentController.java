package org.example.commentarea.controller;

import org.example.commentarea.entity.RestBean;
import org.example.commentarea.entity.dto.Comment;
import org.example.commentarea.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @GetMapping("/list")
    public RestBean<List<Comment>> listComments() {
        List<Comment> comments = commentService.findAllComments();
        return RestBean.success(comments);
    }
    
    @PostMapping("/add")
    public RestBean<String> addComment(@RequestBody Comment comment) {
        int result = commentService.insertComment(comment);
        if (result > 0) {
            return RestBean.success("评论添加成功");
        } else {
            return RestBean.failure(400, "评论添加失败");
        }
    }
}