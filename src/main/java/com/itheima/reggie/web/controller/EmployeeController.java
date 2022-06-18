package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.dto.LoginDto;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 12:04
 * 员工控制器类
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private HttpServletRequest request;


    /**
     * 登录
     *
     * @param loginDto
     * @return
     */

    @PostMapping("/login")
    public R login(@RequestBody LoginDto loginDto) {
        log.info("开始登录{}", loginDto);
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        if (StringUtils.isNotEmpty(password)) {
            //1. 加密提交的`password`，得到密文
            password = DigestUtils.md5DigestAsHex(password.getBytes());

            LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
            lqw.eq(username != null, Employee::getUsername, username);

            //2. 判断员工是否存在
            Employee employee = employeeService.getOne(lqw);
            if (employee == null) {
                return R.error("登录失败");
            }

            //3. 判断密码是否正确
            if (!password.equals(employee.getPassword())) {
                return R.error("登录失败");
            }

            // 4. 判断员工是否被禁用
            if (employee.getStatus() != 1) {
                return R.error("用户被禁用");
            }

            //5. 登陆成功，把员工`ID`存入`session`
            request.getSession().setAttribute("employeeId", employee.getId());


            //6. 响应页面登陆成功
            return R.success("登录成功", employee);
        }
        return R.error("登录失败");
    }


    /**
     * 登出
     *
     * @return
     */
    @PostMapping("/logout")
    public R logout() {
        log.info("登出");
        request.getSession().removeAttribute("employeeId");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R addEmployee(@RequestBody Employee employee) {
        Long id = (Long) request.getSession().getAttribute("id");
        employeeService.saveWithCheckUsername(employee);
        return R.success("添加成功");
    }


    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> findEmployee(Integer page, Integer pageSize, String name) {
        log.info("员工信息分页查询");
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 5;
        }

        Page<Employee> employeePage = new Page<>();
        employeePage.setCurrent(page);
        employeePage.setSize(pageSize);

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        lqw.like(StringUtils.isNotBlank(name), Employee::getName, name);
        lqw.orderByDesc(Employee::getUpdateTime);
        employeeService.page(employeePage, lqw);
        List<Employee> employees = employeePage.getRecords();
        for (int i = 0; i < employees.size(); i++) {
            employees.get(i).setPassword(null);
        }
        return R.success("查询成功", employeePage);
    }

    /**
     * 修改员工。默认按需修改
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R update(@RequestBody Employee employee) {
        Long id = employee.getId();
        Integer status = employee.getStatus();
        if (id != null) {
            if (status != 0 && status != 1) {
                return R.error("参数有误");
            }
//            (Long) request.getSession().getAttribute("id")

//            Long updateId = BaseContextUtil.getCurrentId();
//            employee.setUpdateUser(updateId);
//            employee.setUpdateTime(LocalDateTime.now());
            boolean flag = employeeService.updateById(employee);
            if (flag) {
                return R.success("修改成功");
            }
            return R.error("修改失败");
        }
        return R.error("参数有误");
    }

    /**
     * 查询员工信息，进行编辑回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> findById(@PathVariable Long id) {
        log.info("员工{}信息编辑回显", id);
        if (id != null) {
            Employee employee = employeeService.getById(id);
            if (employee != null) {
                employee.setPassword(null);
                return R.success("查询成功", employee);
            }
            return R.error("查询失败");
        }
        return R.error("参数有误");
    }
}
