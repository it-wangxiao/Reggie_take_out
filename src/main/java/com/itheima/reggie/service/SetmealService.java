package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SetmealService extends IService<Setmeal> {

    //套餐分页查询
    Page<SetmealDto> findPage(Integer page, Integer pageSize, String name);

    //添加套餐
    boolean saveSetmeal(SetmealDto setmealDto);

    SetmealDto getByIdWithFlavor(Long id);

    boolean updateWithSetmealDish(SetmealDto setmealDto);


    boolean switchStatus(Integer status, Long[] ids);

    boolean deleteByIds(Long[] ids);
}