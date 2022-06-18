package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrdersDto;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.utils.BaseContextUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/12 22:21
 * 订单信息
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 后台订单分页查询
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> findPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        Page<OrdersDto> dtoPage = orderService.findPage(page, pageSize, number, beginTime, endTime);

        return R.success("查询成功", dtoPage);
    }


    /**
     * 下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders) {

        boolean result = orderService.submit(orders);


        if (result) {
            return R.success("支付成功");
        }
        return R.error("支付失败");
    }

    /**
     * 更改订单状态
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R start(@RequestBody Orders orders) {

        boolean result = orderService.updateById(orders);
        if (result) {
            return R.success("成功");
        }
        return R.error("失败");
    }

    /**
     * 前台用户订单分页查看
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> list(Integer page, Integer pageSize) {
        if (page != null && pageSize != null) {

            Page<Orders> page1 = new Page<>(page, pageSize);
            Long userId = BaseContextUtil.getCurrentId();

            LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
            qw.eq(Orders::getUserId, userId)
            .orderByDesc(Orders::getOrderTime);
            orderService.page(page1, qw);

            return R.success("查询成功", page1);
        }
        return R.error("参数有误");
    }

    @PostMapping("/again")
    public R again(@RequestBody Orders orders){
        Long id = orders.getId();
        log.info("id = {}",id);
        boolean result = orderService.again(id);
        if (result) {
            return R.success("成功");
        }
        return R.error("失败");
    }
}
