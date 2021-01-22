package com.xxxx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.base.BaseService;
import com.xxxx.dao.SaleChanceMapper;
import com.xxxx.query.SaleChanceQuery;
import com.xxxx.utils.AssertUtil;
import com.xxxx.utils.PhoneUtil;
import com.xxxx.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

	@Resource
	private SaleChanceMapper saleChanceMapper;

	/**
	 * 多条件查询
	 * @param saleChanceQuery  前台传回的参数条件
	 * @return
	 */
	public Map<String,Object> selectByParams(SaleChanceQuery saleChanceQuery){
		Map<String,Object> map = new HashMap<>();
		PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
		List<SaleChance> saleChances = saleChanceMapper.selectByParams(saleChanceQuery);
		//按照分页条件  格式化数据
		PageInfo<SaleChance> saleChancePageInfo = new PageInfo<>(saleChances);
		map.put("code",0);
		map.put("msg","");
		map.put("count",saleChancePageInfo.getTotal());
		map.put("data",saleChancePageInfo.getList());
		return map;
	}
	/**
	 * 添加数据
	 * 		1.校验参数
	 *          customerName   客户名称 非空
	 *          linkMan       联系人   非空
	 *          linkPhone      手机号码 非空  手机号11位正则校验
	 *      2.设置默认值
	 *          is_valid     数据有效   0无效 1有效
	 *          create_date  数据创建时间
	 *          update_date  数据修改时间
	 *          create_man   数据的创建人  当前登录用户（交给controller层从cookie获取）直接设置到 salechance对象中
	 *
	 *          判断用户是否设置了分配人
	 *              如果分配了
	 *                  assign_man   分配人
	 *                  assign_time  分配时间
	 *                  state        已分配 分配状态  0未分配 1已分配
	 *                  dev_result   开发中 开发状态  0-未开发 1-开发中 2-开发成功 3-开发失败
	 *              如果未分配
	 *                  state        未分配 分配状态  0未分配 1已分配
	 *                  dev_result   未开发 开发状态  0-未开发 1-开发中 2-开发成功 3-开发失败
	 *       3.执行添加操作，判断是否添加成功
	 */
	public void addSlaChance(SaleChance saleChance){
		//校验参数
		checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
		//设置默认值
		saleChance.setIsValid(1);	//是否为有效数据
		saleChance.setUpdateDate(new Date());	//数据修改时间
		saleChance.setCreateDate(new Date());	//数据创建时间

		//判断用户是否设置了分配人
		if (StringUtils.isBlank(saleChance.getAssignMan())){
			//未分配状态
			saleChance.setState(0);		//分配状态
			saleChance.setDevResult(0);	//开发状态
		}else {
			//分配了人员
			saleChance.setAssignTime(new Date());  //分配的时间
			saleChance.setState(1);
			saleChance.setDevResult(1);
		}

		// 执行添加操作  判断是否添加成功
		AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)<1,"营销机会数据添加失败");
	}

	/**
	 * 校验添加的数据
	 * 		customerName   客户名称 非空
	 * 		linkMan       联系人   非空
	 * 		linkPhone      手机号码 非空  手机号11位正则校验
	 * @param customerName
	 * @param linkMan
	 * @param linkPhone
	 */
	private void checkParams(String customerName, String linkMan, String linkPhone) {
		AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能为空");
		AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
		AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"手机号码不能为空");
		//校验手机号是否符合规范
		AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号不符合规范");
	}

	/**
	 * 修改数据
	 * 		1.校验参数
	 *          id属性是必须存在的，查询数据库校验
	 *          customerName   客户名称 非空
	 *          linkMan       联系人   非空
	 *          linkPhone      手机号码 非空  手机号11位正则校验
	 *      2.默认值
	 *          update_date  修改时间
	 *
	 *          判断是否指派了工作人员
	 *              1.修改前没有分配人
	 *                  修改后没有分配人
	 *                      不做任何操作
	 *                  修改后有分配人
	 *                      dev_result  开发状态
	 *                      assign_time 分配时间
	 *                      state       分配状态
	 *
	 *              2.修改前有分配人
	 *                  修改后没有分配人
	 *                      assign_time 分配时间 null
	 *                      dev_result  开发状态
	 *                      state       分配状态 0
	 *                  修改后有分配人
	 *                      判断更改后的人员和更改前的人员有没有变动
	 *                          没有变动不做操作
	 *                          有变动，assign_time最新的时间
	 *     3.执行修改操作，判断是否修改成功
	 * @param saleChance
	 */
	public void updateSaleChance(SaleChance saleChance){
		//判断id是否存在
		AssertUtil.isTrue(saleChance.getId()==null,"数据异常，请重试");
		//校验非空参数
		checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
		//设置默认值
		saleChance.setUpdateDate(new Date());

		//通过现有的id 查询修改之前的数据
		SaleChance dbSaleChance = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
		AssertUtil.isTrue(dbSaleChance==null,"数据异常，请重试");

		//判断原有数据中是否有分配人
		if (StringUtils.isBlank(dbSaleChance.getAssignMan())){
			//进入当前判断说明修改前没有分配人

			//判断修改后是否有分配人
			if (!StringUtils.isBlank(saleChance.getAssignMan())){
				//修改后有分配人
				saleChance.setAssignTime(new Date());
				saleChance.setState(1);
				saleChance.setDevResult(1);
			}
			//进入这里是修改后没有分配人，什么都不做
		}else {
			//进入当前是 说明修改前有分配人
			//判断修改后是否有分配人
			if (StringUtils.isBlank(saleChance.getAssignMan())){
				//修改后没有分配人
				saleChance.setAssignTime(null);
				saleChance.setState(0);
				saleChance.setDevResult(0);
			}else {
				//修改后有分配人
				//判断前后分配人是否变化
				if (!dbSaleChance.getAssignMan().equals(saleChance.getAssignMan())){
					//不是一个人 有变化
					saleChance.setAssignTime(new Date());
				}else {
					//相同的分配人  那么前台后台都没有设置分配的时间，那么结合刚修改的sql条件，那么原有的数据机会被更改
					saleChance.setAssignTime(new Date());
				}
			}
		}
		//执行修改操作
		int i = saleChanceMapper.updateByPrimaryKeySelective(saleChance);
		AssertUtil.isTrue(i<1,"营销数据修改失败");
	}

	/**
	 * 查询所有的销售人员
	 * @return
	 */
	public List<Map<String,Object>> queryAllSales(){
		return saleChanceMapper.queryAllSales();
	}

	public void deleteBatchs(Integer[] integers){
		//非空校验
		AssertUtil.isTrue(integers==null||integers.length==0,"请选择需要删除的数据");
		//执行删除数据操作
		int i = saleChanceMapper.deleteBatchs(integers);
		AssertUtil.isTrue(i<0,"营销机会数据删除失败");
	}
	public void updateCusDevPlanResult(Integer id, Integer Result){
		//校验 参数
		AssertUtil.isTrue(id==null,"待更新数据不存在");
		//根据参数查询数据
		SaleChance saleChance = selectByPrimaryKey(id);
		//校验数据
		AssertUtil.isTrue(saleChance==null,"待更新数据不存在");
		//更新数据状态
		saleChance.setDevResult(Result);
		//校验更新是否成功
		int i = updateByPrimaryKeySelective(saleChance);
		AssertUtil.isTrue(i<1,"更新失败");
	}
}
