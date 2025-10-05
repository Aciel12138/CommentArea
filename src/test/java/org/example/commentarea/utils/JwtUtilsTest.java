package org.example.commentarea.utils;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class JwtUtilsTest {

    @Resource
    JwtUtils jwtUtils;
    @Test
    void expireTime() {
        System.out.println(jwtUtils.expireTime());
    }
}