package org.example.commentarea.service;

import org.example.commentarea.entity.dto.Account;
import org.example.commentarea.entity.vo.request.EmailRegisterVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    Account findAccountNameOrEmail(String text);
    boolean addUser(Account account);
    String registerEmailVerifyCode(String type,String email,String ip);
    String registerEmailAccount(EmailRegisterVO vo);
}