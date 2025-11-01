package org.example.commentarea.service.impl;

import jakarta.annotation.Resource;
import org.example.commentarea.entity.dto.Comment;
import org.example.commentarea.mapper.CommentMapper;
import org.example.commentarea.service.AccountService;
import org.example.commentarea.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    @Resource
    private AccountService accountService;
    
    @Override
    public List<Comment> findAllComments(Integer pageId) {
        return commentMapper.findAllComments(pageId);
    }
    
    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }
}