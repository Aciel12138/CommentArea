package org.example.commentarea.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class RequestLogAspect {

    // 定义切点，拦截所有controller包下的所有方法
    @Pointcut("execution(public * org.example.commentarea.controller..*.*(..))")
    public void controllerLog() {
    }

    @Before("controllerLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 获取客户端IP
        String clientIP = getClientIP(request);
        
        // 构建日志信息
        StringBuilder logInfo = new StringBuilder();
        logInfo.append("\n=================== 用户访问日志 ===================\n");
        logInfo.append("请求地址: ").append(request.getRequestURL().toString()).append("\n");
        logInfo.append("请求方法: ").append(request.getMethod()).append("\n");
        logInfo.append("客户IP: ").append(clientIP).append("\n");
        logInfo.append("请求参数: ").append(Arrays.toString(joinPoint.getArgs())).append("\n");
        
        // 获取所有请求头
        logInfo.append("请求头信息: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
            logInfo.append("  ").append(headerName).append(": ").append(headerValue).append("\n");
        }
        
        logInfo.append("请求时间: ").append(new java.util.Date()).append("\n");
        logInfo.append("==================================================");
        
        // 输出到控制台
        System.out.println(logInfo.toString());
    }

    @AfterReturning(returning = "ret", pointcut = "controllerLog()")
    public void doAfterReturning(Object ret) {
        // 可以在这里记录返回结果
        // 如果需要记录返回结果，可以取消下面的注释
        /*
        StringBuilder logInfo = new StringBuilder();
        logInfo.append("\n=================== 响应信息 ===================\n");
        logInfo.append("响应结果: ").append(ret).append("\n");
        logInfo.append("==================================================");
        System.out.println(logInfo.toString());
        */
    }

    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty() && !"unknown".equalsIgnoreCase(xfHeader)) {
            return xfHeader.split(",")[0];
        }

        String xriHeader = request.getHeader("X-Real-IP");
        if (xriHeader != null && !xriHeader.isEmpty() && !"unknown".equalsIgnoreCase(xriHeader)) {
            return xriHeader;
        }

        return request.getRemoteAddr();
    }
}