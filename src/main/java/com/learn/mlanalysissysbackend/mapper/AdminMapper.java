package com.learn.mlanalysissysbackend.mapper;

import com.learn.mlanalysissysbackend.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    Admin getAdminByName(String name);

    Boolean addAdmin(Admin admin);
}
