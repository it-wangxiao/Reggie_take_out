package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/9 15:49
 * 分类控制器，包含了菜品分类和套餐分类
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 菜品套餐分类分页查询
     * **排序条件：
     * ****主要：sort
     * ****次要：updateTime
     *
     * @param page     //当前页
     * @param pageSize //每页显示数据数
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> findCategory(Integer page, Integer pageSize) {

        log.info("菜品套餐分类分页查询");
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        Page<Category> categoryPage = new Page<>();

        categoryPage.setCurrent(page);
        categoryPage.setSize(pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        categoryService.page(categoryPage, lqw);

        return R.success("查询成功", categoryPage);
    }

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R addCategory(@RequestBody Category category) {
        log.info("添加分类，信息{}", category);
        categoryService.addCategory(category);
        return R.success("添加成功");
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public R update(@RequestBody Category category) {
        log.info("修改分类{}", category);
        if (category.getId() != null) {
            boolean flag = categoryService.addById(category);
            if (flag) {
                return R.success("修改成功");
            }
        }
        return R.error("修改失败");
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R delete(Long id) {
        log.info("根据id删除，id为：{}", id);
        if (id != null && id > 0) {
            boolean flag = categoryService.deleteById(id);
            if (flag) {
                return R.success("删除成功");
            }
        }
        return R.error("删除失败");
    }


    /**
     * 根据type查询分类
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> selectByType(Category category) {
//        if (type != null) {

        String name = category.getName();
        Integer type = category.getType();
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(type != null, Category::getType, type)
                //.eq(categoryId!=null,Category::getId,categoryId)
                .like(StringUtils.isNotBlank(name),Category::getName,name)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lqw);
        if (list != null && list.size() > 0) {
            return R.success("查询成功", list);
        }
        return R.error("查无信息");
//        }
//        return R.error("参数有误");
    }
}
