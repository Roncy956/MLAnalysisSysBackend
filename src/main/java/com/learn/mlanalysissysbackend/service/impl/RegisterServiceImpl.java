package com.learn.mlanalysissysbackend.service.impl;

import com.learn.mlanalysissysbackend.mapper.AdminMapper;
import com.learn.mlanalysissysbackend.mapper.UserMapper;
import com.learn.mlanalysissysbackend.pojo.Admin;
import com.learn.mlanalysissysbackend.pojo.User;
import com.learn.mlanalysissysbackend.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    AdminMapper adminMapper;

    @Override
    public User registerUser(User user) {
        User userDB = userMapper.getUserByName(user.getName());
        if (userDB != null) {
            return null;
        }

        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.addUser(user);
        return user;
    }

    @Override
    public Admin registerAdmin(Admin admin) {
        Admin adminDB = adminMapper.getAdminByName(admin.getName());
        if (adminDB != null){
            return null;
        }
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.addAdmin(admin);
        return admin;
    }
}
