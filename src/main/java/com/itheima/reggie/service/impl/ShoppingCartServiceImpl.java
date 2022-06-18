package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/14 9:15
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart saveShoppingCart(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                .eq(StringUtils.isNotBlank(shoppingCart.getDishFlavor()), ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());

        if (shoppingCart.getDishId() != null) {
            //添加到购物车的是菜品
            qw.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            //添加到购物车的是套餐
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = this.getOne(qw);

        if (cart == null) {

            cart = shoppingCart;
        } else {
            cart.setNumber(cart.getNumber() + 1);
        }

        boolean result = this.saveOrUpdate(cart);
        if (result) {
            return cart;
        }
        return null;
    }

    /**
     * 购物车删除菜品单个
     *
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart subShoppingCart(ShoppingCart shoppingCart) {

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        ShoppingCart cart = this.getOne(qw);
        if (cart != null) {
            if (cart.getNumber() > 1) {
                cart.setNumber(cart.getNumber() - 1);
                this.updateById(cart);
                return cart;
            }

            this.removeById(cart);

            return cart;
        }
        return null;
    }
}
