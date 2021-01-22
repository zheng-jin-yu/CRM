package com.xxxx.dao;

import com.xxxx.base.BaseMapper;
import com.xxxx.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper <Permission,Integer>{

	//判断当前角色原来是否有资源
	Integer countPermission(Integer roleId);

	//将原有的资源全部删除
	Integer deletePermissionByRoleId(Integer roleId);

	// 查询当前角色拥有的权限
	List<Integer> selectPermissionByRid(Integer rId);

	//查询当前登录用户扮演角色的权限码
	List<String> selectUserRoleAclvalue(Integer id);
}