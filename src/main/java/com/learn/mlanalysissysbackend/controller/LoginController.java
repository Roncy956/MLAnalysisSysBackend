package com.learn.mlanalysissysbackend.controller;

import com.learn.mlanalysissysbackend.pojo.Admin;
import com.learn.mlanalysissysbackend.pojo.LoginInfo;
import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.pojo.User;
import com.learn.mlanalysissysbackend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    LoginService loginService;

    @PostMapping("/user")
    public Result loginUser(@RequestBody User user) {
        LoginInfo loginInfo = loginService.loginUser(user);
        if (loginInfo == null)
            return Result.error("用户名或密码错误！");
        return Result.success(loginInfo);
    }

    @PostMapping("/admin")
    public Result loginAdmin(@RequestBody Admin admin) {
        LoginInfo loginInfo = loginService.loginAdmin(admin);
        if (loginInfo == null)
            return Result.error("用户名或密码错误！");
        return Result.success(loginInfo);
    }
}
