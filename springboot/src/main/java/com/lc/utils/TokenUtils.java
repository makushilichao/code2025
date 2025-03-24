package com.lc.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lc.entity.Account;
import com.lc.exception.CustomerException;
import com.lc.service.AdminService;
import com.lc.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class TokenUtils {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    static AdminService staticAdminService;
    static UserService staticUserService;

    @PostConstruct
    public void init() {
        staticAdminService = adminService;
        staticUserService = userService;
    }

    public static String genToken(String data, String sign) {
        return JWT.create()
                .withAudience(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1))
                .sign(Algorithm.HMAC256(sign));
    }

    public static Account getCurrentUser() {
        Account account = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            throw new CustomerException("401", "您无权限操作！");
        }
        String audiance = JWT.decode(token).getAudience().get(0);
        String[] split = audiance.split("-");
        String userId = split[0];
        String role = split[1];
        if("ADMIN".equals(role)){
            account = staticAdminService.selectById(userId);
        } else if("USER".equals(role)){
            account = staticUserService.selectById(userId);
        }

        return account;
    }
}
