package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 11:56
 */
public interface CategoryService extends IService<Category> {

    boolean addCategory(Category category);

    boolean deleteById(Long id);

    boolean addById(Category category);
}
