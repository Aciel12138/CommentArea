package org.example.commentarea.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.example.commentarea.entity.RestBean;
import org.example.commentarea.entity.dto.Account;
import org.example.commentarea.entity.vo.request.EmailRegisterVO;
import org.example.commentarea.service.AccountService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.function.Supplier;

@Validated
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    @Resource
    private AccountService accountService;

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        return this.messageHandle(() ->
                accountService.registerEmailAccount(vo));
    }

    @GetMapping("/verify")
        public RestBean<Void> verifyEmail(@RequestParam @Pattern(regexp = "register|reset") String type,
                                            @RequestParam String email,
                                           HttpServletRequest request) {
        return this.messageHandle(()->accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr()));

        }
    private <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}