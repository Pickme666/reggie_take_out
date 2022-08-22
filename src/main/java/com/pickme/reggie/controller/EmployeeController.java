package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.R;
import com.pickme.reggie.entity.Employee;
import com.pickme.reggie.service.inter.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 员工管理
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     * @param employee
     * @param request 用于处理服务端Session中的数据
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //将页码提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //设置条件，查询数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //数据比对
        if (emp == null)                            return R.error(MC.E_LOGIN_NO_NAME);
        if (!emp.getPassword().equals(password))    return R.error(MC.E_LOGIN_PWD);
        if (emp.getStatus() == 0)                   return R.error(MC.E_LOGIN_DISABLED);

        //登录成功，将员工id存入到服务端Session中
        request.getSession().setAttribute("employee",emp.getId());

        log.info(MC.S_LOGIN + emp.getUsername());
        return R.success(emp);
    }

    /**
     * 退出登录
     * @param request
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //删除Session中存储的员工id
        request.getSession().removeAttribute("employee");
        return R.success("");
    }


    /**
     * 添加员工
     * @param employee
     * @param request
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request) {
        //查询账号是否已存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        if (employeeService.getOne(queryWrapper) != null) return R.error(MC.E_USERNAME_EXIST);

        //设置初始密码为123456，并进行md5加密
        String pwd = DigestUtils.md5DigestAsHex(MC.DEFAULT_PWD.getBytes());
        employee.setPassword(pwd);

        employeeService.save(employee);
        return R.success(MC.S_INSERT);
    }


    /**
     * 根据id修改员工信息
     * @出现问题：
     * 前端发送过来的员工id与数据库中不一致，是因为javaScript只能处理最多16位的长整型数据，
     * 超出这个长度就会损失精度，而数据库中的id是通过雪花算法生成的19位长。
     * @解决方案：
     * 把19位长整形的数据转换成字符串类型在返回给前端处理，通过对SpringMVC框架的消息转换器的功能进行扩展，
     * 提供对象转换器JacksonObjectMapper，使其能够把java对象转换为json格式的字符串数据。
     * @param employee
     * @param request
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request) {
        employeeService.updateById(employee);
        return R.success("");
    }


    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page,Integer pageSize,String name) {
        Page<Employee> p = new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //设置模糊查询条件（boolean condition 判断, R column 实体类属性, Object val 要查询的值）
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //设置排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(p,queryWrapper);
        return R.success(p);
    }
}
