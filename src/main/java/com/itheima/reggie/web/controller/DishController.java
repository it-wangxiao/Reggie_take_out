package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.web.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/11 17:43
 * 菜品控制器
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R addDish(@RequestBody DishDto dishDto) {

        boolean saveResult = dishService.saveDishWithFlavor(dishDto);

        if (saveResult) {
            return R.success("添加菜品成功");
        }
        return R.error("添加菜品失败");
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> findByPageWithName(Integer page, Integer pageSize, String name) {

        Page<DishDto> dishDtoPage = dishService.findPage(page, pageSize, name);

        return R.success("查询成功", dishDtoPage);
    }


    /**
     * 根据id查询菜品
     * 数据回显
     *
     * @param id
     * @return
     */
/*    @GetMapping("/{id}")
    public R<Dish> findById(@PathVariable Long id) {

        if (id != null) {
            Dish dish = dishService.getById(id);

            if (dish != null) {
                return R.success("查询成功", dish);
            }
            return R.error("查询失败");
        }
        return R.error("参数有误");
    }*/

    /**
     * 根据id查询菜品
     * 数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> findById(@PathVariable Long id) {

        if (id != null) {
            DishDto dishDto = dishService.getByIdWithFlavor(id);

            if (dishDto != null) {
                return R.success("查询成功", dishDto);
            }
            return R.error("查询失败");
        }
        return R.error("参数有误");
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R update(@RequestBody DishDto dishDto) {
        Long id = dishDto.getId();
        if (id != null) {
            boolean updateResult = dishService.updateByIdWithFlavor(dishDto);
            if (updateResult) {
                return R.success("修改成功");
            }
            return R.error("修改失败");
        }
        return R.error("参数有误");
    }

    /**
     * 启售/禁售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R switchStatus(@PathVariable Integer status, Long[] ids) {
        if (status != null && (status == 1 || status == 0)) {

            boolean ssResult = dishService.switchStatus(status, ids);

            if (ssResult) {
                return R.success(status == 1 ? "启售成功" : "禁售成功");
            }
            return R.error(status == 1 ? "启售失败" : "禁售失败");
        }
        return R.error("参数有误");
    }

    /**
     * 删除菜品
     * **逻辑删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R deleteByIds(Long[] ids) {
        if (ids != null) {
            boolean deleteResult = dishService.deleteByIds(ids);
            if (deleteResult) {
                return R.success("删除成功");
            }
            return R.error("删除失败");
        }
        return R.error("参数有误");
    }

    /**
     * 根据菜品分类查询菜品
     *
     * @param categoryId
     * @return
     */
/*    @GetMapping("/list")
    public R<List<Dish>> list(Long categoryId,Integer status) {
        if (categoryId != null) {
            LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Dish::getCategoryId, categoryId)
            .eq(status!=null,Dish::getStatus,status);
            List<Dish> dishes = dishService.list(lqw);
            return R.success("查询成功", dishes);
        }
        return R.error("参数有误");
    }*/

    /**
     * 根据菜品分类查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        if (dish != null) {
            List<DishDto> dishDtos = dishService.listWithFlavor(dish);

            /*LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Dish::getCategoryId, categoryId)
                    .eq(status != null, Dish::getStatus, status);
            List<Dish> dishes = dishService.list(lqw);*/
            return R.success("查询成功", dishDtos);
        }
        return R.error("参数有误");
    }
}
