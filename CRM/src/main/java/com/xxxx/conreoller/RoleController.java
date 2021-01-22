package com.xxxx.conreoller;

import com.xxxx.base.BaseController;
import com.xxxx.base.ResultInfo;
import com.xxxx.query.RoleQuery;
import com.xxxx.service.RoleService;
import com.xxxx.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

	@Resource
	private RoleService roleService;

	/**
	 *  查询角色列表
	 * @return
	 */

	@RequestMapping("queryAllRoles")
	@ResponseBody
	public List<Map<String,Object>> queryAllRoles(Integer id){
		return roleService.queryAllRoles(id);
	}

	/**
	 *   展示所有的角色信息
	 * @param query
	 * @return
	 */
	@RequestMapping("list")
	@ResponseBody
	public Map<String,Object> userList(RoleQuery query){
		return roleService.queryByParamsForTable(query);
	}

	/**
	 * 跳转到角色管理页面
	 *
	 * @return
	 */
	@RequestMapping("index")
	public String roleIndex(){
		return "role/role";
	}

	//加载添加/修改页面
	@RequestMapping("addOrUpdateRolePage")
	public String addUserPage(Integer id, HttpServletRequest request){
		if(null !=id){
			request.setAttribute("role",roleService.selectByPrimaryKey(id));
		}
		return "role/add_update";
	}

	/**
	 *  添加角色
	 * @param role
	 * @return
	 */
	@RequestMapping("save")
	@ResponseBody
	public ResultInfo saveRole(Role role){
		roleService.saveRole(role);
		return success("角色记录添加成功");
	}

	/**
	 *  角色修改
	 * @param role
	 * @return
	 */
	@RequestMapping("update")
	@ResponseBody
	public ResultInfo updateRole(Role role){
		roleService.updateRole(role);
		return success("角色记录更新成功");
	}

	/**
	 *   删除角色
	 * @param id
	 * @param abc
	 * @return
	 */
	@RequestMapping("delete")
	@ResponseBody
	public ResultInfo deleteRole(Integer id,@RequestParam(value = "abc") List<Integer> abc){
		for(Integer a:abc){
			System.out.println(a);
		}
		roleService.deleteRole(id);
		return success("角色记录删除成功");
	}

}
