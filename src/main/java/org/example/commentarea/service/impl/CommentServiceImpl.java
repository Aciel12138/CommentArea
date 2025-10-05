package org.example.commentarea.service.impl;

import org.example.commentarea.entity.dto.Comment;
import org.example.commentarea.mapper.CommentMapper;
import org.example.commentarea.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public List<Comment> findAllComments() {
        return commentMapper.findAllComments();
    }
    
    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }
}