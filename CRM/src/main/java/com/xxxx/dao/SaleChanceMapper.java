package com.xxxx.dao;


import com.xxxx.base.BaseMapper;
import com.xxxx.query.SaleChanceQuery;
import com.xxxx.vo.SaleChance;

import java.util.List;
import java.util.Map;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {
	/**
	 * //多条件分页查询
	 * @param query
	 * @return
	 */
	public List<SaleChance> selectByParams(SaleChanceQuery query);

	/**
	 * //查询所有销售人员
	 * @return
	 */
	public List<Map<String,Object>> queryAllSales();

	/**
	 * //批量删除操作。。
	 * @param integers
	 */
	public Integer deleteBatchs(Integer[] integers);
}