package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.mapper.EmployeeMapper;
import com.pickme.reggie.pojo.Employee;
import com.pickme.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements  EmployeeService {

    @Override
    public Employee employeeLogin(Employee employee) {
        //将页码提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //设置条件，查询数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = this.getOne(queryWrapper);

        //数据比对
        if (emp == null)                            throw new RuntimeException(MC.E_LOGIN_NO_NAME);
        if (!emp.getPassword().equals(password))    throw new RuntimeException(MC.E_LOGIN_PWD);
        if (emp.getStatus() == 0)                   throw new RuntimeException(MC.E_LOGIN_DISABLED);

        return emp;
    }

    @Override
    public boolean saveEmployee(Employee employee) {
        //查询账号是否已存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        if (this.getOne(queryWrapper) != null) throw new RuntimeException(MC.E_USERNAME_EXIST);

        //设置初始密码，并进行md5加密
        String pwd = DigestUtils.md5DigestAsHex(MC.DEFAULT_PWD.getBytes());
        employee.setPassword(pwd);

        return this.save(employee);
    }

    @Override
    public Page<Employee> pageEmployees(Integer page, Integer pageSize, String name) {
        Page<Employee> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //设置模糊查询条件（boolean condition 判断, R column 实体类属性, Object val 要查询的值）
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排除 admin 管理员账户
        wrapper.ne(Employee::getUsername,"admin");
        //设置排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        return this.page(p,wrapper);
    }
}
