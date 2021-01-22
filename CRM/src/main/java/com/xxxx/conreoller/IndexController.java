package com.xxxx.conreoller;

import com.xxxx.base.BaseController;
import com.xxxx.service.PermissionService;
import com.xxxx.service.UserService;
import com.xxxx.utils.LoginUserUtil;
import com.xxxx.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {
	@Resource
	private UserService userService;

	@Resource
	private PermissionService permissionService;
	/**
	 * 系统登录页
	 */
	@RequestMapping("index")
	public String index(){
		return "index";
	}
	/**
	 * 系统欢迎页面
	 */
	@RequestMapping("welcome")
	public String welcome(){
		return "welcome";
	}
	/**
	 * 后端管理主⻚⾯
	 * @return
	 */
	@RequestMapping("main")
	public String main(HttpServletRequest request){
		//将用户查询的数据 设置到作用域中
		int id = LoginUserUtil.releaseUserIdFromCookie(request);
		User user = userService.selectByPrimaryKey(id);
		request.setAttribute("user",user);
		//当用户登录时，查询已有的权限码，存放在session作用域中，方便前台使用进行判断
		List<String> permissions =  permissionService.selectUserRoleAclvalue(id);
		request.getSession().setAttribute("permissions",permissions);
		return "main";
	}
}
