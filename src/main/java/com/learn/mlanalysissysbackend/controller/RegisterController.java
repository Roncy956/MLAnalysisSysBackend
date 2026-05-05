package com.learn.mlanalysissysbackend.controller;

import com.learn.mlanalysissysbackend.pojo.Admin;
import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.pojo.User;
import com.learn.mlanalysissysbackend.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    RegisterService registerService;

    @Value("${admin-key.key}")
    private String inviteKey;

    // 管理员登录
    @PostMapping("/user")
    public Result registerUser(@RequestBody User user) {
        User userDB = registerService.registerUser(user);
        if (userDB == null) {
            return Result.error("用户名'" + user.getName() + "'已存在！");
        }
        return Result.success(userDB);
    }

    // 管理员注册
    @PostMapping("/admin")
    public Result registerAdmin(@RequestBody Admin admin) {
        Admin adminDB = registerService.registerAdmin(admin);
        if (adminDB == null) {
            return Result.error("用户名'" + admin.getName() + "'已存在！");
        }
        return Result.success(adminDB);
    }

    // 验证管理员邀请码
    @GetMapping("/invite/admin")
    public Result checkInviteKey(@RequestParam("key") String key) {
        if (inviteKey.equals(key)) {
            return Result.success();
        }
        return Result.error("邀请码错误");
    }
}
