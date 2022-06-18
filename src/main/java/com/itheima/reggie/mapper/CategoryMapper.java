package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 11:55
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
