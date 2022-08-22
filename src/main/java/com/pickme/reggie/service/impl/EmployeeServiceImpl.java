package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.entity.Employee;
import com.pickme.reggie.mapper.EmployeeMapper;
import com.pickme.reggie.service.inter.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements  EmployeeService {

}
