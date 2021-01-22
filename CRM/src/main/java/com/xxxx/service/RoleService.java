package com.xxxx.service;

import com.xxxx.base.BaseService;
import com.xxxx.dao.ModuleMapper;
import com.xxxx.dao.PermissionMapper;
import com.xxxx.dao.RoleMapper;
import com.xxxx.utils.AssertUtil;
import com.xxxx.vo.Permission;
import com.xxxx.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role,Integer> {
	@Resource
	private RoleMapper roleMapper;

	@Resource
	private PermissionMapper permissionMapper;

	@Resource
	private ModuleMapper moduleMapper;

	/**
	 * 查询所有角色的id 和 名称
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> queryAllRoles(Integer id){
		return roleMapper.queryAllRoles(id);

	}

	/**
	 * 	新增角色
	 * @param role
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveRole(Role role){
		AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名!");
		Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
		AssertUtil.isTrue(null !=temp,"该角色已存在!");
		//设置默认值
		role.setIsValid(1);
		role.setCreateDate(new Date());
		role.setUpdateDate(new Date());
		//进行添加操作
		AssertUtil.isTrue(insertSelective(role)<1,"角色记录添加失败!");
	}

	/**
	 *  修改角色
	 * @param role
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void  updateRole(Role role){
		AssertUtil.isTrue(null==role.getId()||null==selectByPrimaryKey(role.getId()),"待修改的记录不存在!");
		AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名!");
		Role temp = roleMapper.queryRoleByRoleName(role.getRoleName());
		AssertUtil.isTrue(null !=temp && !(temp.getId().equals(role.getId())),"该角色已存在!");
		role.setUpdateDate(new Date());
		AssertUtil.isTrue(updateByPrimaryKeySelective(role)<1,"角色记录更新失败!");
	}

	/**
	 *  删除角色
	 * @param roleId
	 */
	@Transactional
	public void deleteRole(Integer roleId){
		Role temp =selectByPrimaryKey(roleId);
		AssertUtil.isTrue(null==roleId||null==temp,"待删除的记录不存在!");
		temp.setIsValid(0);
		AssertUtil.isTrue(updateByPrimaryKeySelective(temp)<1,"角色记录删除失败!");
	}


	/**
	 * 添加权限
	 * @param roleId
	 * @param mIds
	 */
	public void addGrant(Integer roleId, Integer[] mIds) {
		//判断角色是否存在
		Role role = roleMapper.selectByPrimaryKey(roleId);
		AssertUtil.isTrue(role == null,"角色不存在");
		//判断需要绑定的权限是否传输过来
		// AssertUtil.isTrue(mIds == null || mIds.length < 1,"需要绑定的模块不存在");
		//判断当前角色原来是否有资源
		Integer count = permissionMapper.countPermission(roleId);
		if(count > 0){
			//将原有的资源全部删除
			AssertUtil.isTrue(permissionMapper.deletePermissionByRoleId(roleId) != count,"数据异常请重试");
		}
		//给角色绑定权限
		List<Permission> permissions = new ArrayList<>();
		for(Integer mid:mIds){
			Permission permission = new Permission();

			permission.setRoleId(roleId);
			permission.setModuleId(mid);
			permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());//设置权限码  需要去module表中查询得到
			permission.setCreateDate(new Date());
			permission.setUpdateDate(new Date());

			permissions.add(permission);
		}

		//执行批量添加操作，绑定多个权限
		AssertUtil.isTrue(permissionMapper.insertBatch(permissions) != permissions.size(),"权限绑定失败");

	}
}
