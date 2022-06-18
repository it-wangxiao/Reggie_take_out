package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.SetmealDish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SetmealDishService extends IService<SetmealDish> {

    List<SetmealDish> listWithDish(Long id);



}