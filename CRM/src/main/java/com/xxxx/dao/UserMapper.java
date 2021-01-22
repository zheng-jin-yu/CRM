package com.xxxx.dao;

import com.xxxx.base.BaseMapper;
import com.xxxx.query.UserQuery;
import com.xxxx.vo.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User,Integer> {

    //通过用户名查询数据
	public User queryUserByName(String userName);

	//分页多条件查询
	public List<User> selectByParams(UserQuery query);

	//(批量)  删除操作
	public Integer deleteUserBatch(Integer[] integers);
}