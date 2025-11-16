package org.example.commentarea.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
import org.example.commentarea.entity.dto.Account;
import org.example.commentarea.entity.vo.request.EmailRegisterVO;
import org.example.commentarea.mapper.AccountMapper;
import org.example.commentarea.service.AccountService;
import org.example.commentarea.utils.Const;
import org.example.commentarea.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FlowUtils flowUtils;


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

        
        // 保存账户信息
        return this.save(account);
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        if (!verifyLimit(ip)) {
            return "请稍后再试";
        }
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        Map<String, Object> data = Map.of("type", type,"email",email,"code",code,"ip",ip);
        amqpTemplate.convertAndSend("mail", data);
        stringRedisTemplate.opsForValue()
                .set(Const.VERIFY_EMAIL_DATA + email,String.valueOf(code), 3,TimeUnit.MINUTES);

        return null;
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email = vo.getEmail();
        String code=stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if(code==null)
            return "没有你验证码,滚";
        if(!code.equals(vo.getCode()))
            return "验证码错误";
        String username = vo.getUsername();
        String password = passwordEncoder.encode(vo.getPassword());

        Account account = new Account(null, username, password, email, "user", new Date());
        if(addUser(account)){
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
            return null;
        }
        return "用户名已存在";


    }

    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        //如果没有封禁,那么就会将这个key存入redis
        return flowUtils.limitOnceCode(key, 20);
    }

}