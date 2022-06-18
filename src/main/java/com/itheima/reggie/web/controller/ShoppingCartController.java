package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.utils.BaseContextUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/14 9:13
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车...");
        Long userId = BaseContextUtil.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(qw);

        return R.success("查找成功", shoppingCarts);
    }


    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart) {

        Long userId = BaseContextUtil.getCurrentId();

        shoppingCart.setUserId(userId);

        ShoppingCart shoppingCart1 = shoppingCartService.saveShoppingCart(shoppingCart);

        if (shoppingCart1 != null) {
            return R.success("添加成功", shoppingCart1);
        }
        return R.error("添加失败");
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R clean() {

        Long userId = BaseContextUtil.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, userId);

        boolean remove = shoppingCartService.remove(qw);

        if (remove) {
            return R.success("清空购物车成功");
        }
        return R.error("清空购物车失败");
    }

    /**
     * 购物车删除菜品单个
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R sub(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContextUtil.getCurrentId();

        shoppingCart.setUserId(userId);
        ShoppingCart cart = shoppingCartService.subShoppingCart(shoppingCart);
        if (cart != null) {
            return R.success("成功", cart);
        }
        return R.error("失败");
    }

}
