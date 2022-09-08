package com.pickme.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pickme.reggie.common.exception.BusinessException;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.mapper.AddressBookMapper;
import com.pickme.reggie.pojo.AddressBook;
import com.pickme.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public AddressBook updateDefaultAddress(AddressBook addressBook) {
        Long userId = LocalContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        wrapper.set(AddressBook::getIsDefault, 0);  //设置字段所有值都为 0

        //SQL:update address_book set is_default = 0 where user_id = ?
        this.update(wrapper);

        //设置为默认地址
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        this.updateById(addressBook);
        return addressBook;
    }

    @Override
    public AddressBook getDefaultAddress() {
        Long userId = LocalContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        wrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = this.getOne(wrapper);
        if (null == addressBook) throw new BusinessException("没有找到该对象");

        return addressBook;
    }

    @Override
    public List<AddressBook> listAddress() {
        Long userId = LocalContext.getCurrentId();

        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, AddressBook::getUserId, userId);
        wrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return this.list(wrapper);
    }
}
