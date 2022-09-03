package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.Employee;

public interface EmployeeService extends IService<Employee> {

    Employee employeeLogin(Employee employee);

    boolean saveEmployee(Employee employee);

    Page<Employee> pageEmployees(Integer page, Integer pageSize, String name);
}
