package org.example.commentarea.service;

import org.example.commentarea.entity.dto.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> findAllComments(Integer pageId);
    int insertComment(Comment comment);
}