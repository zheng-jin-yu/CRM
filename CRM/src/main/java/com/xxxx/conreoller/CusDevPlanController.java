package com.xxxx.conreoller;

import com.xxxx.base.BaseController;
import com.xxxx.base.ResultInfo;
import com.xxxx.query.CusDevPlanQuery;
import com.xxxx.service.CusDevPlanService;
import com.xxxx.service.SaleChanceService;
import com.xxxx.utils.AssertUtil;
import com.xxxx.vo.CusDevPlan;
import com.xxxx.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
	@Resource
	private CusDevPlanService cusDevPlanService;

	@Resource
	private SaleChanceService saleChanceService;

	/**
	 * 打开 客户开发计划页面
	 * @return
	 */
	@RequestMapping("index")
	public String index(){
		return "cusDevPlan/cus_dev_plan";
	}

	/**
	 * 		转发跳转到计划数据项页面
	 * @param request
	 * @param sId
	 * @return
	 */
	@RequestMapping("toCusDevPlanDataPage")
	public String toCusDevPlanDataPage(HttpServletRequest request,Integer sId){
		//非空校验
		AssertUtil.isTrue(sId==null,"数据异常，请重试");
		//通过id查询营销机会数据
		SaleChance saleChance = saleChanceService.selectByPrimaryKey(sId);
		//将数据设置在作用域中
		if (saleChance!=null){
			request.setAttribute("saleChance",saleChance);
		}
		return "cusDevPlan/cus_dev_plan_data";
	}

	/**
	 * 多条件分页查询客户计划表
	 * @param query
	 * @return
	 */
	@RequestMapping("list")
	@ResponseBody
	public Map<String,Object> queryByParams(CusDevPlanQuery query){

		return cusDevPlanService.queryByParams(query);
	}

	/**
	 * 添加计划项
	 * @param cusDevPlan
	 */
	@RequestMapping("save")
	@ResponseBody
	public ResultInfo saveCusDevPlan(CusDevPlan cusDevPlan){
		cusDevPlanService.saveCusDevPlan(cusDevPlan);
		return success("计划添加成功");
	}

	/**
	 * 	跳转到计划数据项页面
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("addOrUpdateCusDevPlanPage")
	public String addOrUpdateCusDevPlanPage(Integer id,Integer sid,HttpServletRequest request){
		SaleChance saleChance =saleChanceService.selectByPrimaryKey(sid);
		AssertUtil.isTrue(saleChance==null,"数据不存在");
		if(id!=null){
			AssertUtil.isTrue(id==null,"id没拿到");
			CusDevPlan cusDevPlan=cusDevPlanService.selectByPrimaryKey(id);
			request.setAttribute("cusDevPlan",cusDevPlan);
		}
		request.setAttribute("sid",saleChance.getId());
		return  "cusDevPlan/add_update";
	}

	/**
	 *  更新 计划项
	 * @param cusDevPlan
	 * @return
	 */
	@RequestMapping("update")
	@ResponseBody
	public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
		cusDevPlanService.updateCusDevPlan(cusDevPlan);
		return success("计划项更新成功");
	}

	/**
	 * 删除 计划项
	 * @param id
	 */
	@RequestMapping("delete")
	@ResponseBody
	public ResultInfo deleteCusDevPlan(Integer id){
		cusDevPlanService.deleteCusDevPlan(id);
		return success("数据删除成功");
	}

}
