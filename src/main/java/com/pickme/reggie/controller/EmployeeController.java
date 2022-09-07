package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.Employee;
import com.pickme.reggie.service.EmployeeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 员工管理
 */
@Api(tags = "员工管理")
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param employee
     * @param request 用于处理服务端Session中的数据
     */
    @PostMapping("/login")
    public Res<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        Employee emp = employeeService.employeeLogin(employee);

        //登录成功，将员工id存入到服务端Session中
        request.getSession().setAttribute("employee",emp.getId());
        log.info("员工登录成功，用户名：{}",emp.getUsername());

        return Res.success(emp);
    }

    /**
     * 退出登录
     * @param request
     */
    @PostMapping("/logout")
    public Res<String> logout(HttpServletRequest request) {
        //删除Session中存储的员工id
        request.getSession().removeAttribute("employee");
        return Res.success("");
    }


    /**
     * 添加员工
     * @param employee
     */
    @PostMapping
    public Res<String> save(@RequestBody Employee employee) {
        employeeService.saveEmployee(employee);
        return Res.success(MC.S_INSERT);
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
    public Res<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        employeeService.updateById(employee);
        return Res.success("");
    }


    /**
     * 根据id查询员工信息
     * @param id
     */
    @GetMapping("/{id}")
    public Res<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Res.success(employee);
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     */
    @GetMapping("/page")
    public Res<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        Page<Employee> p = employeeService.pageEmployees(page, pageSize, name);
        return Res.success(p);
    }
}
