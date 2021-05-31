package com.lagou.mapper;

import com.lagou.pojo.User;

public interface IUserMapper {

    int saveUser(User user);

    int updateUser(User user);

    int deleteUser(Integer userId);
}
