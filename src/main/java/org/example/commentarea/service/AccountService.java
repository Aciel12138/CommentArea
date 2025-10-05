package org.example.commentarea.service;

import com.baomidou.mybatisplus.extension.service.IService;

import org.example.commentarea.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountNameOrEmail(String accountName);


}
