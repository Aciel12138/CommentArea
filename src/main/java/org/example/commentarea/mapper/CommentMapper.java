package org.example.commentarea.mapper;

import org.apache.ibatis.annotations.*;
import org.example.commentarea.entity.dto.Comment;
import java.util.List;

@Mapper
public interface CommentMapper {
    
    @Select("SELECT comment_id as commentId, comment_content as commentContent, user_id as userId FROM comment")
    List<Comment> findAllComments();
    
    @Insert("INSERT INTO comment(comment_content, user_id) VALUES(#{commentContent}, #{userId})")
    int insertComment(Comment comment);
}