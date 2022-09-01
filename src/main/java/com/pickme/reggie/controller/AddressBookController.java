package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.common.util.LocalContext;
import com.pickme.reggie.pojo.AddressBook;
import com.pickme.reggie.service.inter.AddressBookService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址管理
 */
@Api(tags = "地址管理")
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public Res<AddressBook> save(@RequestBody AddressBook addressBook) {
        Long userId = LocalContext.getCurrentId();
        log.info("addressBook: {}", addressBook);
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return Res.success(addressBook);
    }

    /**
     * 删除地址
     * @return
     */
    @DeleteMapping()
    public Res<String> remove(Long ids) {
        addressBookService.removeById(ids);
        return Res.success("");
    }

    /**
     * 修改地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public Res<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Res.success("");
    }

    /**
     * 修改默认地址
     */
    @PutMapping("default")
    public Res<AddressBook> updateDefault(@RequestBody AddressBook addressBook) {
        Long userId = LocalContext.getCurrentId();

        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, userId);
        wrapper.set(AddressBook::getIsDefault, 0);  //设置字段所有值都为0

        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return Res.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public Res get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) {
            return Res.error("没有找到该对象");
        }
        return Res.success(addressBook);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public Res<AddressBook> getDefault() {
        Long userId = LocalContext.getCurrentId();

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (null == addressBook) {
            return Res.error("没有找到该对象");
        }
        return Res.success(addressBook);
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public Res<List<AddressBook>> list() {
        Long userId = LocalContext.getCurrentId();
        log.info("登录用户id：{}", userId);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, AddressBook::getUserId, userId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return Res.success(list);
    }
}
