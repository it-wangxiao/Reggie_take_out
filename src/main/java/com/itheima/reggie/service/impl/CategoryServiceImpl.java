package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.web.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 11:57
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @Override
    public boolean addCategory(Category category) {

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(category.getName()), Category::getName, category.getName());

        Category one = this.getOne(queryWrapper);
        if (one != null) {
            throw new BusinessException("名称：" + category.getName() + "已存在");
        }

        this.save(category);
        return true;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(Long id) {

        LambdaQueryWrapper<Dish> dlqw = new LambdaQueryWrapper<>();
        dlqw.eq(id != null, Dish::getCategoryId, id);

        List<Dish> dishes = dishService.list(dlqw);
        if (dishes.size() > 0 && dishes != null) {
            throw new BusinessException("该菜品分类正在使用，禁止删除");
        }


        LambdaQueryWrapper<Setmeal> slqw = new LambdaQueryWrapper<>();
        slqw.eq(id != null, Setmeal::getCategoryId, id);

        List<Setmeal> setmeals = setmealService.list(slqw);
        if (setmeals.size() > 0 && setmeals != null) {
            throw new BusinessException("该套餐分类正在使用，禁止删除");
        }

        boolean flag = this.removeById(id);
        return flag;
    }

    /**
     * 添加
     * @param category
     * @return
     */
    @Override
    public boolean addById(Category category) {

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(category.getName()), Category::getName, category.getName());

        Category one = this.getOne(queryWrapper);
        if (one != null) {
            throw new BusinessException("名称：" + category.getName() + "已存在");
        }

        this.updateById(category);
        return true;
    }
}
