package com.xxxx.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.base.BaseService;
import com.xxxx.dao.CusDevPlanMapper;
import com.xxxx.query.CusDevPlanQuery;
import com.xxxx.utils.AssertUtil;
import com.xxxx.vo.CusDevPlan;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {

	@Resource
	private CusDevPlanMapper cusDevPlanMapper;

	@Resource
	private SaleChanceService saleChanceService;


	/**
	 * //查询营销机会的计划项数据列表
	 * @param query
	 * @return
	 */
	public Map<String,Object> queryByParams(CusDevPlanQuery query){
		Map<String,Object> map = new HashMap<>();
		//分页查询数据
		PageHelper.startPage(query.getPage(),query.getLimit());
		List<CusDevPlan> cusDevPlans = cusDevPlanMapper.queryByParams(query);
		//按照分页条件  格式化数据
		PageInfo<CusDevPlan> cusDevPlanPageInfo = new PageInfo<>(cusDevPlans);
		map.put("code",0);
		map.put("msg","");
		map.put("count",cusDevPlanPageInfo.getTotal());
		map.put("data",cusDevPlanPageInfo.getList());
		return map;
	}

/**
 * 添加计划项
 * 1. 参数校验
 * 营销机会ID 非空 记录必须存在
 * 计划项内容 非空
 * 计划项时间 非空
 * 2. 设置参数默认值
 * is_valid
 * crateDate
 * updateDate
 * 3. 执行添加，判断结果
 *
 */
	public void saveCusDevPlan(CusDevPlan cusDevPlan){
		//1.校验参数  营销机会ID      计划项内容       计划项时间
		checkParms(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanDate(),cusDevPlan.getPlanItem());
		//2.设置参数默认值
		cusDevPlan.setIsValid(1);
		cusDevPlan.setCreateDate(new Date());
		cusDevPlan.setUpdateDate(new Date());
		//3.执行添加 ，判断结果
		int i = insertSelective(cusDevPlan);
		AssertUtil.isTrue(i<1,"计划项添加失败");

	}

	/**
	 * 更新计划选项
	 * @param cusDevPlan
	 */
	public void updateCusDevPlan(CusDevPlan cusDevPlan){
		AssertUtil.isTrue(null==cusDevPlan.getId()||null==selectByPrimaryKey(cusDevPlan.getId()),"更新记录不存在");
		//校验参数
		checkParms(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanDate(),cusDevPlan.getPlanItem());
		//设置默认值
		cusDevPlan.setUpdateDate(new Date());
		//执行添加
		int i = updateByPrimaryKeySelective(cusDevPlan);
		AssertUtil.isTrue(i<1,"计划项记录更新失败");

	}

	/**
	 *删除 计划项
	 * @param id
	 */
	public void deleteCusDevPlan(Integer id){
		//通过id 查询到 该条数据
		CusDevPlan cusDevPlan = selectByPrimaryKey(id);
		//校验是否存在
		AssertUtil.isTrue(cusDevPlan==null,"待删除数据不存在，请核对后操作");
		//设置 默认值 假删
		assert cusDevPlan != null;
		 //验证是否删除
		AssertUtil.isTrue(updateByPrimaryKeySelective(cusDevPlan)<1,"计划项删除失败");
	}

	/**
	 *   校验参数
	 * @param saleChanceId  营销机会ID
	 * @param planDate      计划项时间
	 * @param planItem  	计划项内容
	 */
	private void checkParms(Integer saleChanceId, Date planDate, String planItem) {
		//校验 营销机会ID   非空 记录必须存在
		System.out.println(saleChanceId+"service+++++++++++++++++");
		AssertUtil.isTrue(saleChanceId==null|| saleChanceService.selectByPrimaryKey(saleChanceId)==null,"数据错误，请重试");
		//校验 计划项时间  非空
		AssertUtil.isTrue(planDate==null,"计划时间不能为空");
		//校验 计划项内容  非空
		AssertUtil.isTrue(planItem==null,"计划内容不能为空");

	}

}
