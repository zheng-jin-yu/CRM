package com.xxxx.service;

import com.xxxx.base.BaseService;
import com.xxxx.dao.ModuleMapper;
import com.xxxx.dao.PermissionMapper;
import com.xxxx.dao.RoleMapper;
import com.xxxx.model.TreeModel;
import com.xxxx.utils.AssertUtil;
import com.xxxx.vo.Module;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

	@Resource
	private ModuleMapper moduleMapper;

	@Resource
	private RoleMapper roleMapper;

	@Resource
	private PermissionMapper permissionMapper;

	/**
	 * 查询所有资源
	 * @return
	 */
	public List<TreeModel> queryAllModules(Integer rId){
		//角色非空且存在
		AssertUtil.isTrue(rId==null||roleMapper.selectByPrimaryKey(rId)==null,"角色不存在");
		//查询当前角色拥有的权限
		List<Integer> mIds = permissionMapper.selectPermissionByRid(rId);
		//查询所有的模块
		List<TreeModel> treeModels = moduleMapper.queryAllModules();
		//遍历需要返回到前台的所有资源
		for (TreeModel treeModel : treeModels){
			//获取当前遍历对象的模块 id
			Integer id = treeModel.getId();
			//判断当前角色拥有的权限是否包含了 遍历对象的模块id
			if (mIds.contains(id)){  //当前方法判断某个数据是否存在于这个集合中
				treeModel.setChecked(true);
				treeModel.setOpen(true);
			}
		}
		return treeModels;
	}

	/**
	 * 查询所有资源 资源管理使用
	 * @return
	 */
	public Map<String,Object> queryModules(){
		Map<String,Object> map = new HashMap<>();
		List<Module> modules = moduleMapper.queryModules();
		AssertUtil.isTrue(modules ==null||modules.size()<1,"资源数据异常");
		//准备前台需要的数据
		map.put("code",0);
		map.put("msg","");
		map.put("count",modules.size());
		map.put("data",modules);
		return map;
	}
}
