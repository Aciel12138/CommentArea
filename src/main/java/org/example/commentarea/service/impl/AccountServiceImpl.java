package org.example.commentarea.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import org.example.commentarea.entity.dto.Account;
import org.example.commentarea.mapper.AccountMapper;
import org.example.commentarea.service.AccountService;
import org.example.commentarea.utils.Const;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User.withUsername(username).password(account.getPassword()).roles(account.getRole()).build();
    }

    public Account findAccountNameOrEmail(String text) {
        return this.query()
                .eq("username", text)
                .or()
                .eq("email", text)
                .one();
    }

    @Override
    public boolean addUser(Account account) {
        // 检查用户名是否已存在
        Account existingAccount = this.query().eq("username", account.getUsername()).one();
        if (existingAccount != null) {
            return false; // 用户名已存在
        }
        
        // 检查邮箱是否已存在
        existingAccount = this.query().eq("email", account.getEmail()).one();
        if (existingAccount != null) {
            return false; // 邮箱已存在
        }
        
        // 设置默认角色
        if (account.getRole() == null || account.getRole().isEmpty()) {
            account.setRole("user");
        }
        
        // 加密密码
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        
        // 保存账户信息
        return this.save(account);
    }




}