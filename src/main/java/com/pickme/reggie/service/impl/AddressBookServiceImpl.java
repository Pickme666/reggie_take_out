package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.mapper.AddressBookMapper;
import com.pickme.reggie.pojo.AddressBook;
import com.pickme.reggie.service.inter.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
