package com.learn.mlanalysissysbackend.mapper;

import com.learn.mlanalysissysbackend.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getUserByName(String name);

    Boolean addUser(User user);
}
