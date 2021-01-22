package com.xxxx.service;

import com.xxxx.base.BaseService;
import com.xxxx.dao.PermissionMapper;
import com.xxxx.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {

    @Resource
    private PermissionMapper permissionMapper;

    //查询当前登录用户扮演角色的权限码
    public List<String> selectUserRoleAclvalue(Integer id) {
        return permissionMapper.selectUserRoleAclvalue(id);
    }
}
