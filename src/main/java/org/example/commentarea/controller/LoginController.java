package org.example.commentarea.controller;

import jakarta.annotation.Resource;
import org.example.commentarea.entity.RestBean;
import org.example.commentarea.entity.dto.Account;
import org.example.commentarea.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    @Resource
    private AccountService accountService;
    
    @PostMapping("/register")
    public RestBean<String> register(@RequestBody Account account) {
        // 设置注册时间
        account.setRegisterTime(new Date());
        
        // 尝试添加用户
        boolean success = accountService.addUser(account);
        if (success) {
            return RestBean.success("用户注册成功");
        } else {
            return RestBean.failure(400, "用户名已存在");
        }
    }
}