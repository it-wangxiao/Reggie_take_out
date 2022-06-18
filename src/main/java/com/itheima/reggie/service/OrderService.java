package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrdersDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderService extends IService<Orders> {


    Page<OrdersDto> findPage(Integer page, Integer pageSize, String number, String  beginTime, String endTime);


    boolean submit(Orders orders);

    boolean again(Long id);
}