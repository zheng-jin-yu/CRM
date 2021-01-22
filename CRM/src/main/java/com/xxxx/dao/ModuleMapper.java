package com.xxxx.dao;

import com.xxxx.base.BaseMapper;
import com.xxxx.model.TreeModel;
import com.xxxx.vo.Module;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

	//查询所有资源  授权使用
	public List<TreeModel> queryAllModules();

	//查询所有资源  资源管理使用
	public List<Module> queryModules();
}