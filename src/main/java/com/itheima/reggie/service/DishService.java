package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface DishService extends IService<Dish> {
    boolean saveDishWithFlavor(DishDto dishDto);


    Page<DishDto> findPage(Integer page, Integer pageSize, String name);

    DishDto getByIdWithFlavor(Long id);

    boolean updateByIdWithFlavor(DishDto dishDto);
    // 启售/禁售
    boolean switchStatus(Integer status, Long[] ids);


    boolean deleteByIds(Long[] ids);

    List<DishDto> listWithFlavor(Dish dish);
}