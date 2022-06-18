package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.web.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 11:57
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


    //    @Autowired
    //    HttpServletRequest request;

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @Override
    public boolean saveWithCheckUsername(Employee employee) {

        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(username), Employee::getUsername, username);
        Employee one = this.getOne(lqw);

        if (one != null) {
            throw new BusinessException("用户名" + username + "已存在");
        }
        //默认密码加密
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        //补全数据
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());*/
        //获取添加用户ID
//        Long id = (Long) request.getSession().getAttribute("id");

//        Long id = EmployeeController.threadLocal.get();

        /*Long id = BaseContextUtil.getCurrentId();

        System.out.println("————————————————————————————————————" + id);
        employee.setCreateUser(id);
        employee.setUpdateUser(id);*/

        this.save(employee);

        return false;
    }
}
