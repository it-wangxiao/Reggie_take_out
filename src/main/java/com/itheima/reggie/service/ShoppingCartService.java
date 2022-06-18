package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.ShoppingCart;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/14 9:14
 */

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart saveShoppingCart(ShoppingCart shoppingCart);

    ShoppingCart subShoppingCart(ShoppingCart shoppingCart);
}
