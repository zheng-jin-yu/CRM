package com.xxxx.conreoller;
import com.xxxx.base.BaseController;
import com.xxxx.base.ResultInfo;
import com.xxxx.query.SaleChanceQuery;
import com.xxxx.service.SaleChanceService;
import com.xxxx.utils.AssertUtil;
import com.xxxx.utils.CookieUtil;
import com.xxxx.utils.LoginUserUtil;
import com.xxxx.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
	@Resource
	private SaleChanceService saleChanceService;
	/**
	 * 多条件分页查询 数据
	 * @param saleChanceQuery  前台传的条件对象
	 * @return
	 */
	@GetMapping("list")
	@ResponseBody
	public Map<String,Object> queryByParams(SaleChanceQuery saleChanceQuery, Integer flag,HttpServletRequest request){
		//判断 前台传回的数据flag值，区分客户开发计划和营销机会管理
		if (flag!=null&&flag==1){
			//获取当前登录用户的id
			Integer id = LoginUserUtil.releaseUserIdFromCookie(request);
			saleChanceQuery.setAssignMan(id);
		}
		return saleChanceService.selectByParams(saleChanceQuery);
	}

	/**
	 *  添加数据
	 * @param request		前台传来的请求
	 * @param saleChance	表对象
	 * @return
	 */
	@PostMapping("save")
	@ResponseBody
	public ResultInfo save(HttpServletRequest request, SaleChance saleChance){
		//获取创建人
		String userName = CookieUtil.getCookieValue(request,"userName");
		saleChance.setCreateMan(userName);
		saleChanceService.addSlaChance(saleChance);
		return success();
	}

	/**
	 * 打开营销机会管理页面
	 *
	 * @return
	 */
	@RequestMapping("index")
	public String index(){
		return "saleChance/sale_chance";
	}

	/**
	 *  打开营销 机会修改、添加的页面
	 * @param id	前台传回的id
	 * @param request	前台传回的对象
	 * @return
	 */
	@RequestMapping("toAddUpdatePage")
	public String toAddUpdatePage(Integer id,HttpServletRequest request){
		if (id!=null){
			SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
			AssertUtil.isTrue(saleChance==null,"数据异常，请重试");
			request.setAttribute("saleChance",saleChance);
		}
		return "saleChance/add_update";
	}

	/**
	 * 添加数据
	 * @param saleChance
	 * @return
	 */
	@PostMapping("update")
	@ResponseBody
	public ResultInfo update(SaleChance saleChance){
		saleChanceService.updateSaleChance(saleChance);
		return success();
	}

	/**
	 * 查询所有的销售人员
	 * @return
	 */
	@PostMapping("queryAllSales")
	@ResponseBody
	public List<Map<String,Object>> queryAllSales(){
		return saleChanceService.queryAllSales();
	}

	/**
	 * 删除营销机会数据
	 * @param integers	前台传回的数据
	 * @return	返回成功消息
	 */
	@RequestMapping("deleteBatchs")
	@ResponseBody
	public ResultInfo deleteBatchs(Integer[] integers){
		//调用删除
		saleChanceService.deleteBatchs(integers);
		return success("营销机会删除成功");
	}
	@RequestMapping("updateSaleChanceDevResult")
	@ResponseBody
	public  ResultInfo updateCusDevPlanResult(Integer id,Integer Result){
		saleChanceService.updateCusDevPlanResult(id,Result);
		return success("状态更新成功");
	}
}
