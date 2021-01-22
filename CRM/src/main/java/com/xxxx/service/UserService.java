package com.xxxx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.base.BaseService;
import com.xxxx.base.ResultInfo;
import com.xxxx.dao.UserMapper;
import com.xxxx.dao.UserRoleMapper;
import com.xxxx.query.UserModel;
import com.xxxx.query.UserQuery;
import com.xxxx.utils.AssertUtil;
import com.xxxx.utils.Md5Util;
import com.xxxx.utils.PhoneUtil;
import com.xxxx.utils.UserIDBase64;
import com.xxxx.vo.User;
import com.xxxx.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
	@Resource
	private UserMapper userMapper;

	@Resource
	private UserRoleMapper userRoleMapper;

	/**
	 * 用户登录
	 * 		校验参数是否为空
	 * 		如果为空，抛异常
	 * 		调用dao层查询通过用户名查询数据库数据
	 *        如果未查到，抛异常(用户不存在)
	 *      校验前台传来的密码和数据库中的密码是否一致 (前台密码加密后再校验)
	 *         如果不一致，抛异常(密码错误)
	 *      封装ResultInfo对象给前台（根据前台需求：usermodel对象封装后传到前台使用）
	 * @param userName
	 * @param userPwd
	 * @return
	 */
	public ResultInfo loginCheck(String userName, String userPwd){
		//1.验证参数
		checkLoginData(userName,userPwd);
		//调用dao层查询   通过用户名查询数据库数据，判断账号是否存在
		User user = userMapper.queryUserByName(userName);
		AssertUtil.isTrue(user==null,"账号不存在");
		//校验前台传来的密码和数据库的密码是否一致  （前台密码加密后再校验）
		checkLoginPwd(user.getUserPwd(),userPwd);

		//封装ResultInfo 对象给前台 （根据前台需求，userModel对象封装后传到前台使用）
		ResultInfo resultInfo =buildResultinfo(user);
		return resultInfo;
	}

	/**
	 *  修改密码
	 * @param userId    前台传来的id值
	 * @param oldPassword	前台传来的原密码
	 * @param newPassword	前台传来的新密码
	 * @param confirmPassword	前台传来的确认密码
	 */
	public void userUpdate(Integer userId,String oldPassword,String newPassword,String confirmPassword){
		//确保用户是否是登录状态获取cookie中的id    非空 查询数据库
		AssertUtil.isTrue(userId==null,"用户未登录");
		User user = userMapper.selectByPrimaryKey(userId);
		AssertUtil.isTrue(user==null,"用户状态异常");
		//校验密码数据
		checkUpdateData(oldPassword,newPassword,confirmPassword,user.getUserPwd());
		//执行修改操作，返回ResultInfo
		user.setUserPwd(Md5Util.encode(newPassword));
		user.setUpdateDate(new Date());
		//判断是否修改成功
		AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"密码修改失败");
	}

	/**
	 * 密码校验
	 * 		1.确保用户是否是登录状态获取cookie中的id 非空 查询数据库
	 *      2.校验老密码 非空  老密码必须要跟数据库中密码一致
	 *      3.新密码    非空  新密码不能和原密码一致
	 *      4.确认密码  非空  确认必须和新密码一致
	 *      5.执行修改操作，返回ResultInfo
	 * @param oldPassword	前台传来的原密码
	 * @param newPassword	前台传来的新密码
	 * @param confirmPassword 前台传来的确认密码
	 * @param dbPassword	数据库查询的密码
	 */
	private void checkUpdateData(String oldPassword, String newPassword, String confirmPassword, String dbPassword) {
		//校验原密码  非空   原密码需与数据库密码一致
		AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原密码不存在");
		AssertUtil.isTrue(!dbPassword.equals(Md5Util.encode(oldPassword)),"原密码输入有误");

		//新密码  非空  新密码不能与原密码一致
		AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
		AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不能与原密码一致");
		//确认密码  非空  确认必须和新密码一致
		AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"确认密码不能为空");
		AssertUtil.isTrue(!confirmPassword.equals(newPassword),"确认密码必须与新密码一致");
	}


	/**
	 * 校验前台传来的密码和数据库中的密码是否一致（前台密码加密后再校验）
	 * @param dbPwd   前台传的密码
	 * @param userPwd 数据库密码
	 */
	private void checkLoginPwd(String dbPwd, String userPwd) {
		//将传来的密码加密后再校验
		String encodePwd = Md5Util.encode(userPwd);
		//校验
		AssertUtil.isTrue(!encodePwd.equals(dbPwd),"用户密码错误");
	}

	/**
	 * 准备前台cookie 需要的数据    userModel
	 * @param user
	 * @return
	 */
	private ResultInfo buildResultinfo(User user) {
		ResultInfo resultInfo = new ResultInfo();
		//封装userModel  cookie 需要的数据
		UserModel userModel = new UserModel();
		//将userid 加密
		String id = UserIDBase64.encoderUserID(user.getId());
		//存入数据
		userModel.setUserId(id);
		userModel.setUserName(user.getUserName());
		userModel.setTrueName(user.getTrueName());

		resultInfo.setResult(userModel);
		return resultInfo;
	}

	/**
	 *  用户登录非空校验
	 * @param userName  用户姓名
	 * @param userPwd	用户密码
	 */
	private void checkLoginData(String userName, String userPwd) {
		AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
		AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
	}

	/**
	 * //分页多条件查询
	 * @param query
	 * @return
	 */
	public Map<String,Object> selectByParams(UserQuery query){
		Map<String,Object> map = new HashMap<>();
		//分页
		PageHelper.startPage(query.getPage(),query.getLimit());

		List<User> users= userMapper.selectByParams(query);
		//按照分页条件  格式化数据
		PageInfo<User> userPageInfo = new PageInfo( users);
		map.put("code",0);
		map.put("msg","");
		map.put("count",userPageInfo.getTotal());
		map.put("data",userPageInfo.getList());
		return map;

	};

	/**
	 * 用户添加
	 * 		1.参数校验
	 * 		2.设置默认值
	 * 		3.执行添加操作
	 * @param user
	 */
	public void saveUser(User user){
		//参数校验
		User users=userMapper.queryUserByName(user.getUserName());
		AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()),"用户名不能为空");
		AssertUtil.isTrue(users!=null,"用户名已存在");
		checkparams(user.getEmail(),user.getPhone());
		//设置默认值
		user.setUpdateDate(new Date());
		user.setIsValid(1);
		user.setCreateDate(new Date());
		//设置密码   默认为123456 （MD5加密）
		user.setUserPwd(Md5Util.encode("123456"));
		//执行添加操作   设置对应sql属性，主键返回到user对象中
		AssertUtil.isTrue(userMapper.insertHasKey(user)<1,"用户添加失败");
		//用户角色分配
		relationUserRole(user.getId(),user.getRoleIds());
	}

	/**
	 * 用户角色分配
	 *
	 * @param id		用户id
	 * @param roleIds	 角色id
	 */
	private void relationUserRole(Integer id, String roleIds) {
		//修改角色操作：查询是否原来就有角色，如果有直接删除再绑定
		Integer count = userRoleMapper.countUserRole(id);
		if (count>0){
			AssertUtil.isTrue(userRoleMapper.deleteUserRoleById(id)!=count,"原有角色删除失败");
		}
		AssertUtil.isTrue(roleIds==null,"角色不存在");
		//准备一个容器接收遍历出来的新对象，新数据
		List<UserRole> urs = new ArrayList<>();
		//切割获取的每一个id
		String [] splits = roleIds.split(",");
		for (String idStr : splits){
			UserRole userRole = new UserRole();
			userRole.setUserId(id);
			userRole.setRoleId(Integer.parseInt(idStr));
			userRole.setCreateDate(new Date());
			userRole.setUpdateDate(new Date());
		urs.add(userRole);
		}
		//执行批量添加操作
		AssertUtil.isTrue(userRoleMapper.insertBatch(urs)!=splits.length,"角色绑定失败");
	}

	/**
	 * 修改用户
	 * 		1.参数校验
	 * 		2.设置默认值  update_date
	 * 		3.执行修改操作
	 * @param user
	 */
	public void updateUser(User user){
		//  id   非空   唯一
		AssertUtil.isTrue(null==user.getId() ||null==userMapper.selectByPrimaryKey(user.getId()),"数据异常，请重试");
		//用户名  非空  唯一
		AssertUtil.isTrue(null==user.getUserName(),"用户名不能为空");
		User dbUser = userMapper.queryUserByName(user.getUserName());
		AssertUtil.isTrue(dbUser!=null && user.getId()!=dbUser.getId(),"用户名已存在");
		//校验邮箱  手机号
		checkparams(user.getEmail(),user.getPhone());
		//设置默认值
		user.setUpdateDate(new Date());
		//执行修改操作
		int i = userMapper.updateByPrimaryKeySelective(user);
		AssertUtil.isTrue(i<1,"数据修改失败，请重试");
		//用户角色分配
		relationUserRole(user.getId(),user.getRoleIds());

	}
	/**
	 * 校验邮箱 手机号
	 * @param email
	 * @param phone
	 */
	private void checkparams(String email, String phone) {
		AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
		AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
		AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式错误");
	}

	/**
	 * (批量)  删除
	 * @param integers
	 */
	@Transactional
	public void deleteUserBatch(Integer[] integers){
		//参数校验
		AssertUtil.isTrue(null==integers||0==integers.length,"请选择需要删除的用户");
		//执行操作
		int i = userMapper.deleteUserBatch(integers);
		AssertUtil.isTrue(i!=integers.length,"用户删除失败");
	}
}
