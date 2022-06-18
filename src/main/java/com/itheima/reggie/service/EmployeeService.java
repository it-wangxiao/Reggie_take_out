package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 11:56
 */
public interface EmployeeService extends IService<Employee> {
    boolean saveWithCheckUsername(Employee employee);
}
