package com.xxxx.dao;

import com.xxxx.base.BaseMapper;
import com.xxxx.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
    //获取某个用户对应的角色数量
    Integer countUserRole(Integer id);

    //删除某个用户下的所有角色
    Integer deleteUserRoleById(Integer id);

}