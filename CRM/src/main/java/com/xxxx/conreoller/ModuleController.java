package com.xxxx.conreoller;

import com.xxxx.base.BaseController;
import com.xxxx.model.TreeModel;
import com.xxxx.service.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

	@Resource
	private ModuleService moduleService;

	/**
	 *  查询所有资源
	 * @param rId
	 * @return
	 */
	@RequestMapping("queryAllModules")
	@ResponseBody
	public List<TreeModel> queryAllModules(Integer rId){
		return moduleService.queryAllModules(rId);
	}

	//查询所有资源  资源管理使用
	@RequestMapping("list")
	@ResponseBody
	public Map<String,Object> queryModules(){
		return moduleService.queryModules();
	}

	//跳转到模块页面
	@RequestMapping("index")
	public String index(){
		return "module/module";
	}
}
