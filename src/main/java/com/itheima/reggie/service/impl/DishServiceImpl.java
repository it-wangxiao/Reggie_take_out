package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveDishWithFlavor(DishDto dishDto) {
        String name = dishDto.getName();
        LambdaQueryWrapper<Dish> dlqw = new LambdaQueryWrapper<>();
        dlqw.eq(Dish::getName, name);
        Dish dish = this.getOne(dlqw);
        if (dish != null) {
            throw new BusinessException("菜品名" + name + "已存在");
        }
        //保存菜品
        this.save(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            Long id = dishDto.getId();
            flavors = flavors.stream().map((flavor) -> {
                flavor.setDishId(id);
                return flavor;
            }).collect(Collectors.toList());
            dishFlavorService.saveBatch(flavors);
        }
        return true;
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<DishDto> findPage(Integer page, Integer pageSize, String name) {


        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        Page<Dish> Pages = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(name), Dish::getName, name).orderByDesc(Dish::getUpdateTime);
        Page<Dish> dishPage = this.page(Pages, lqw);

        Page<DishDto> dtoPage = new Page<>();

        BeanUtils.copyProperties(dishPage, dtoPage, "records");

        List<Dish> records = dishPage.getRecords();

        List<Category> categories = categoryService.list();

        List<DishDto> dishes = records.stream().map((dish) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(dish, dishDto);

            Long id = dish.getCategoryId();

            /*Category categoryServiceById = categoryService.getById(id);

            if (categoryServiceById!=null){
                dishDto.setCategoryName(categoryServiceById.getName());

            }*/

            for (Category category : categories) {
                if (id.equals(category.getId())) {
                    dishDto.setCategoryName(category.getName());
                }
            }
            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dishes);

        return dtoPage;
    }

    /**
     * 根据id查询菜品
     * 数据回显
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //拷贝数据
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(lqw);

        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    /**
     * 修改
     *
     * @param dishDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateByIdWithFlavor(DishDto dishDto) {

        Long dishId = dishDto.getId();

        Dish dish = this.getById(dishId);
        if (!dish.getName().equals(dishDto.getName())) {
            LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
            String name = dishDto.getName();
            qw.eq(Dish::getName, name);
            Dish dish1 = this.getOne(qw);

            if (dish1 != null) {
                throw new BusinessException("菜名" + name + "已存在");
            }
        }


        boolean updateResult = this.updateById(dishDto);

        if (!updateResult) {
            return updateResult;
        }


        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        boolean removeResult = dishFlavorService.remove(lqw);

        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        boolean saveResult = dishFlavorService.saveBatch(flavors);

        if (!saveResult) {
            return false;
        }
        return true;
    }

    /**
     * 启售/禁售
     *
     * @param status
     * @param ids
     * @return
     */
    @Override
    public boolean switchStatus(Integer status, Long[] ids) {
        int switchStatus = dishMapper.switchStatus(status, ids);

        if (switchStatus == ids.length) {
            return true;
        }


        return false;
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @Override
    public boolean deleteByIds(Long[] ids) {
        int count = dishMapper.deleteByIds(ids);

        return true;
    }

    @Override
    public List<DishDto> listWithFlavor(Dish dish) {


        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus())
                .like(StringUtils.isNotBlank(dish.getName()),Dish::getName,dish.getName());
        List<Dish> dishes = this.list(qw);

        ArrayList<DishDto> dishDtos = new ArrayList<>();

//        BeanUtils.copyProperties(dishes,dishDtos);

        for (Dish dish1 : dishes) {
            DishDto dishDto = new DishDto();
            Long id = dish1.getId();
            BeanUtils.copyProperties(dish1,dishDto);
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();

            lqw.eq(DishFlavor::getDishId,id);

            List<DishFlavor> dishFlavors = dishFlavorService.list(lqw);

            dishDto.setFlavors(dishFlavors);
            dishDtos.add(dishDto);
        }

        return dishDtos;
    }

}