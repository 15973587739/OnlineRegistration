package com.atguigu.yygh.order.mapper;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    /**
     * 查询预约统计数据
     *
     * @param countQueryVo 预约统计查询对象
     * @return 预约统计数据列表
     */
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo countQueryVo);

}
