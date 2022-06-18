package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<SetmealDto> findPage(Integer page, Integer pageSize, String name) {

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(name), Setmeal::getName, name).orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPage, qw);

        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");

        List<Setmeal> records = setmealPage.getRecords();

        List<Category> categories = categoryService.list();

        ArrayList<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(record, setmealDto);

            for (Category category : categories) {
                if (record.getCategoryId().equals(category.getId())) {
                    setmealDto.setCategoryName(category.getName());
                    setmealDtos.add(setmealDto);
                }
            }
        }
        dtoPage.setRecords(setmealDtos);
        return dtoPage;
    }

    /**
     * 添加套餐
     *
     * @param setmealDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveSetmeal(SetmealDto setmealDto) {

        String name = setmealDto.getName();

        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(Setmeal::getName, name);

        Setmeal setmeal = this.getOne(qw);
        if (setmeal != null) {
            throw new BusinessException("套餐名" + name + "已存在");
        }


        boolean setmealResult = this.save(setmealDto);
        if (!setmealResult) {
            return setmealResult;
        }

        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }

        boolean saveBatch = setmealDishService.saveBatch(setmealDishes);

        if (!saveBatch) {
            return false;
        }
        return true;

    }

    @Override
    public SetmealDto getByIdWithFlavor(Long id) {

        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();

        qw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(qw);

        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWithSetmealDish(SetmealDto setmealDto) {

        boolean updateResult = this.updateById(setmealDto);
        if (!updateResult){
            return updateResult;
        }

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        Long id = setmealDto.getId();
        qw.eq(SetmealDish::getSetmealId, id);
        boolean removeResult = setmealDishService.remove(qw);

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        boolean saveResult = setmealDishService.saveBatch(setmealDishes);

        if (!saveResult) {
            return false;
        }
        return true;
    }


    @Override
    public boolean switchStatus(Integer status, Long[] ids) {

        setmealMapper.switchStatus(status,ids);
        return true;
    }

    /**
     * 逻辑删除
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByIds(Long[] ids) {


        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int onCount = this.count(qw);


        if (onCount>0) {
            throw new BusinessException("禁止删除启售套餐，请先停售");
        }


        boolean removeResult = this.removeByIds(Arrays.asList(ids));

        if (!removeResult) {
            return false;
        }

        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);
        return setmealDishService.remove(lqw);
    }


}