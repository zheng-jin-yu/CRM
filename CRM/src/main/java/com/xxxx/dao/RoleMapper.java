package com.xxxx.dao;

import com.xxxx.base.BaseMapper;
import com.xxxx.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    // 查询角色列表
    public List<Map<String,Object>> queryAllRoles(Integer id);

    /*通过名称查询角色数据*/
    public Role queryRoleByRoleName(String roleName);
}