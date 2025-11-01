package org.example.commentarea.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("comment")
public class Comment {
    private Integer commentId;
    
    private String commentContent;

    @TableField("user_id")
    private Integer userId;
    private Integer pageId;
    
    // Getters and setters are provided by Lombok @Data annotation
}