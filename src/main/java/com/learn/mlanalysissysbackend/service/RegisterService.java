package com.learn.mlanalysissysbackend.service;

import com.learn.mlanalysissysbackend.pojo.Admin;
import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.pojo.User;

public interface RegisterService {
    User registerUser(User user);

    Admin registerAdmin(Admin admin);
}
