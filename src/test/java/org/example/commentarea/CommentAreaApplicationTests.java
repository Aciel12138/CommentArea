package org.example.commentarea;

import jakarta.annotation.Resource;
import org.example.commentarea.entity.dto.Account;
import org.example.commentarea.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

@SpringBootTest
class CommentAreaApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private AccountService accountService;
    
    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void testAddUser() {
        Account account = new Account();
        account.setUsername("testuser11");
        account.setPassword("password12311");
        account.setEmail("test@example.com11");
        account.setRegisterTime(new Date());
        account.setRole("user");
        
        boolean result = accountService.addUser(account);
        
        // 验证用户添加成功
        assert result;
        

    }

}