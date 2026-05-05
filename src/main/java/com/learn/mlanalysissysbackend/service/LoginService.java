package com.learn.mlanalysissysbackend.service;

import com.learn.mlanalysissysbackend.pojo.Admin;
import com.learn.mlanalysissysbackend.pojo.LoginInfo;
import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.pojo.User;

public interface LoginService {
    LoginInfo loginUser(User user);

    LoginInfo loginAdmin(Admin admin);
}
