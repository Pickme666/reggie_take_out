package com.pickme.reggie.controller;

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
     * @param addressBook
     */
    @PostMapping
    public Res<AddressBook> save(@RequestBody AddressBook addressBook) {
        Long userId = LocalContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return Res.success(addressBook);
    }

    /**
     * 删除地址
     * @param ids
     */
    @DeleteMapping()
    public Res<String> remove(Long ids) {
        addressBookService.removeById(ids);
        return Res.success("");
    }

    /**
     * 修改地址信息
     * @param addressBook
     */
    @PutMapping
    public Res<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return Res.success("");
    }

    /**
     * 修改默认地址
     * @param addressBook
     */
    @PutMapping("default")
    public Res<AddressBook> updateIsDefault(@RequestBody AddressBook addressBook) {
        AddressBook isDefaultAddress = addressBookService.updateDefaultAddress(addressBook);
        return Res.success(isDefaultAddress);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public Res<AddressBook> getDefault() {
        AddressBook defaultAddress = addressBookService.getDefaultAddress();
        return Res.success(defaultAddress);
    }

    /**
     * 根据id查询地址
     * @param id
     */
    @GetMapping("/{id}")
    public Res<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) return Res.error("没有找到该对象");
        return Res.success(addressBook);
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public Res<List<AddressBook>> list() {
        List<AddressBook> allAddress = addressBookService.listAddress();
        return Res.success(allAddress);
    }
}
