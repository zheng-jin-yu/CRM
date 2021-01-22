package com.xxxx.dao;

import com.xxxx.base.BaseMapper;
import com.xxxx.query.CusDevPlanQuery;
import com.xxxx.vo.CusDevPlan;

import java.util.List;

public interface CusDevPlanMapper  extends BaseMapper<CusDevPlan,Integer>{

    List<CusDevPlan> queryByParams(CusDevPlanQuery query);
}