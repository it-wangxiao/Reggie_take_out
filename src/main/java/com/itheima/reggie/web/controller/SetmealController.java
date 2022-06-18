package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.web.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/12 19:05
 * 套餐
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page<SetmealDto>> findPage(Integer page, Integer pageSize, String name) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        Page<SetmealDto> dtoPage = setmealService.findPage(page, pageSize, name);

        return R.success("查询成功", dtoPage);
    }

    /**
     * 添加套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R saveSetmeal(@RequestBody SetmealDto setmealDto) {

        boolean ssResult = setmealService.saveSetmeal(setmealDto);

        if (ssResult) {
            return R.success("添加成功");
        }

        return R.error("添加失败");
    }

    /**
     * 根据ID查询套餐
     * 进行修改数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> findById(@PathVariable Long id) {

        if (id != null) {
            SetmealDto setmealDto = setmealService.getByIdWithFlavor(id);

            if (setmealDto != null) {
                return R.success("查询成功", setmealDto);
            }
            return R.error("查询失败");
        }
        return R.error("参数有误");
    }


    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R update(@RequestBody SetmealDto setmealDto) {

        boolean updateResult = setmealService.updateWithSetmealDish(setmealDto);

        if (updateResult) {

            return R.success("修改成功");
        }
        return R.error("修改失败");

    }

    /**
     * 套餐启售/禁售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R switchStatus(@PathVariable Integer status, Long[] ids) {

        if (status != null && (status == 1 || status == 0)) {

            boolean ssResult = setmealService.switchStatus(status, ids);

            if (ssResult) {
                return R.success(status == 1 ? "启售成功" : "禁售成功");
            }
            return R.error(status == 1 ? "启售失败" : "禁售失败");
        }
        return R.error("参数有误");
    }


    /**
     * 套餐删除（逻辑删除）
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R deleteByIds(Long[] ids) {

        if (ids.length > 0) {

            boolean ssResult = setmealService.deleteByIds(ids);
            if (ssResult) {
                return R.success("删除成功");
            }
            return R.error("删除失败");
        }
        return R.error("参数有误");
    }


    /**
     * 根据分类id查询套餐
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
                .like(StringUtils.isNotBlank(setmeal.getName()), Setmeal::getName, setmeal.getName())
                .like(StringUtils.isNotBlank(setmeal.getDescription()), Setmeal::getDescription, setmeal.getDescription())
                .like(StringUtils.isNotBlank(setmeal.getCode()), Setmeal::getCode, setmeal.getCode());
        List<Setmeal> setmeals = setmealService.list(lqw);


        if (setmeal!=null) {
            return R.success("查询成功", setmeals);
        }
        return R.error("查询失败");
    }


    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> findDish(@PathVariable Long id) {


        List<SetmealDish> dishList = setmealDishService.listWithDish(id);

        return R.success("",dishList);
    }

}
