package org.example.commentarea.service;

import org.example.commentarea.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account findAccountNameOrEmail(String text);
    boolean addUser(Account account);
}