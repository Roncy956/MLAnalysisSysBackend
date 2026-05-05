package com.learn.mlanalysissysbackend.service.impl;

import com.learn.mlanalysissysbackend.mapper.AdminMapper;
import com.learn.mlanalysissysbackend.mapper.UserMapper;
import com.learn.mlanalysissysbackend.pojo.Admin;
import com.learn.mlanalysissysbackend.pojo.LoginInfo;
import com.learn.mlanalysissysbackend.pojo.User;
import com.learn.mlanalysissysbackend.service.LoginService;
import com.learn.mlanalysissysbackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    AdminMapper adminMapper;

    @Override
    public LoginInfo loginUser(User user) {
        User userDB = userMapper.getUserByName(user.getName());
        // 判断用户是否存在
        if (userDB == null) return null;
        // 判断密码是否正确
        if (!userDB.getPassword().equals(user.getPassword())) return null;
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setId(userDB.getId());
        loginInfo.setName(userDB.getName());
        loginInfo.setRole("user");
        loginInfo.setToken(JwtUtils.generateToken(loginInfo));

        return loginInfo;

    }

    @Override
    public LoginInfo loginAdmin(Admin admin) {
        Admin adminDB = adminMapper.getAdminByName(admin.getName());
        // 判断管理员是否存在
        if (adminDB == null) return null;
        // 判断密码是否正确
        if (!adminDB.getPassword().equals(admin.getPassword())) return null;

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setId(adminDB.getId());
        loginInfo.setName(adminDB.getName());
        loginInfo.setRole("admin");
        loginInfo.setToken(JwtUtils.generateToken(loginInfo));

        return loginInfo;
    }


}
