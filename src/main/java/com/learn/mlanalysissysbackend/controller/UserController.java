package com.learn.mlanalysissysbackend.controller;

import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.pojo.User;
import com.learn.mlanalysissysbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/add")
    public Result addUser(@RequestBody User user) {
        System.out.println("用户已插入:" + user);
        return Result.success(user);
    }

}
