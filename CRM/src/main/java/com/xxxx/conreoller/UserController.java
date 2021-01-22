package com.xxxx.conreoller;

import com.xxxx.base.BaseController;
import com.xxxx.base.ResultInfo;
import com.xxxx.query.UserQuery;
import com.xxxx.service.UserService;
import com.xxxx.utils.LoginUserUtil;
import com.xxxx.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
	@Resource
	private UserService userService;

	/**
	 * 用户登录
	 * @param userName  前台传来的用户名
 	 * @param userPwd	前台传来的密码
	 * @return
	 */
	@PostMapping("login")
	@ResponseBody
	public ResultInfo login(String userName,String userPwd){
		return userService.loginCheck(userName,userPwd);
	}

	/**
	 * 修改密码
	 * @param request      前台传来的数据
	 * @param oldPassword	前台传来的原密码
	 * @param newPassword	前台传来的新密码
	 * @param confirmPassword	前台传来的确认密码
	 * @return
	 */
	@PostMapping("update")
	@ResponseBody
	public ResultInfo update(HttpServletRequest request,String oldPassword, String newPassword, String confirmPassword){
		int id = LoginUserUtil.releaseUserIdFromCookie(request);
		userService.userUpdate(id,oldPassword,newPassword,confirmPassword);
		return success();
	}
	//打开修改密码页面
	@RequestMapping("toPasswordPage")
	public String toPasswordPage(){
		return "user/password";
	}

	/**
	 *  分页多条件查询用户数据
	 * @param query
	 * @return
	 */
	@RequestMapping("list")
	@ResponseBody
	public Map<String, Object> selectByParams(UserQuery query){
		return userService.selectByParams(query);
	}
	/**
	 * 进入用户模块页面
	 * @return
	 */
	@RequestMapping("index")
	public String index(){
		return "user/user";
	}

	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	@PostMapping("save")
	@ResponseBody
	public ResultInfo saveUser(User user){
		userService.saveUser(user);
		return success("用户添加成功");
	}

	/**
	 * 更新用户
	 * @param user
	 * @return
	 */
	@PostMapping("updateUser")
	@ResponseBody
	public ResultInfo updateUser(User user){
		userService.updateUser(user);
		return success("用户修改成功");
	}

	/**
	 * 进入用户添加 修改页面
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("toUpdateAddPage")
	public String addUserPage(Integer id,HttpServletRequest request){
		if (id!=null){
			User user = userService.selectByPrimaryKey(id);
			request.setAttribute("user",user);
		}
		return "user/add_update";
	}

	/**
	 * 	(批量)  删除 用户
	 * @param integers
	 * @return
	 */
	@PostMapping("deleteBatch")
	@ResponseBody
	public ResultInfo deleteUserBatch(Integer[] integers){
		userService.deleteUserBatch(integers);
		return success("用户记录删除成功");
	}
}

