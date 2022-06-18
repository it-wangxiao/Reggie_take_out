package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.entity.dto.OrdersDto;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import com.itheima.reggie.utils.BaseContextUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/12 22:20
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 订单信息分页查询
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page<OrdersDto> findPage(Integer page, Integer pageSize, String number, String beginTime, String endTime) {


        Page<Orders> orderPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(number), Orders::getNumber, number).between(beginTime != null && endTime != null, Orders::getOrderTime, beginTime, endTime);
        Page<Orders> ordersPage = this.page(orderPage, qw);

        Page<OrdersDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(orderPage, dtoPage, "records");

        List<Orders> records = orderPage.getRecords();

        List<OrderDetail> orderDetails = orderDetailService.list();

        ArrayList<OrdersDto> ordersDtoArrayList = new ArrayList<>();

        for (Orders record : records) {

            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(record, ordersDto);

            for (OrderDetail orderDetail : orderDetails) {
                if (orderDetail.getOrderId().equals(record.getId())) {
                    ordersDto.setUserName(record.getUserName());
                    ordersDtoArrayList.add(ordersDto);
                    break;
                }
            }


        }

        dtoPage.setRecords(ordersDtoArrayList);

        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submit(Orders orders) {
        Long userId = BaseContextUtil.getCurrentId();
        User user = userService.getById(userId);

        orders.setNumber(UUID.randomUUID().toString());
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(qw);

        AtomicInteger sum = new AtomicInteger(0);
        for (ShoppingCart shoppingCart : shoppingCarts) {


            sum.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
        }
        orders.setAmount(new BigDecimal(sum.get()));
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());

        boolean orderResult = this.save(orders);

        if (!orderResult) {
            return false;
        }
        ArrayList<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(addressBook.getConsignee());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setOrderId(orders.getId());
            if (shoppingCart.getSetmealId() != null) {
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
            }
            if (shoppingCart.getDishId() != null) {
                orderDetail.setDishId(shoppingCart.getDishId());
            }

            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());

            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetails.add(orderDetail);
        }

        boolean saveBatch = orderDetailService.saveBatch(orderDetails);

        if (saveBatch) {

            LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ShoppingCart::getUserId, userId);
            shoppingCartService.remove(lqw);
        }

        return saveBatch;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean again(Long id) {
        Long userId = BaseContextUtil.getCurrentId();
        LambdaQueryWrapper<OrderDetail> qw = new LambdaQueryWrapper<>();
        qw.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> orderDetails = orderDetailService.list(qw);
        ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            Long dishId = orderDetail.getDishId();
            Long setmealId = orderDetail.getSetmealId();

            if (dishId != null) {
                Dish dish = dishService.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setUserId(userId);
                shoppingCart.setDishId(dishId);
                LambdaQueryWrapper<OrderDetail> lqw = new LambdaQueryWrapper<>();
                lqw.eq(OrderDetail::getDishId, dishId)
                        .eq(OrderDetail::getOrderId, orderDetail.getOrderId());
                OrderDetail orderDetail1 = orderDetailService.getOne(lqw);
                shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                shoppingCart.setNumber(orderDetail.getNumber());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());

            } else {
                Setmeal setmeal = setmealService.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setUserId(userId);
                shoppingCart.setSetmealId(setmealId);
                shoppingCart.setNumber(orderDetail.getNumber());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCarts.add(shoppingCart);
        }

        boolean result = shoppingCartService.saveBatch(shoppingCarts);


        return result;
    }

}
