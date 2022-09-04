package com.pickme.reggie.service.inter;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pickme.reggie.pojo.AddressBook;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface AddressBookService extends IService<AddressBook> {

    @Transactional
    AddressBook updateDefaultAddress(AddressBook addressBook);

    AddressBook getDefaultAddress();

    List<AddressBook> listAddress();
}
