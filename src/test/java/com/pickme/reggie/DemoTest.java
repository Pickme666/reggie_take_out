package com.pickme.reggie;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pickme.reggie.entity.Dish;
import com.pickme.reggie.entity.Setmeal;
import com.pickme.reggie.service.impl.DishServiceImpl;
import com.pickme.reggie.service.impl.SetmealServiceImpl;
import com.pickme.reggie.service.inter.DishService;
import com.pickme.reggie.service.inter.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class DemoTest {

    @Test
    public void test() {
        String filename = "11222555.txt";
        //截取上传文件的后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖，并拼接上后缀名
        String newFilename = UUID.randomUUID() + suffix;
        System.out.println(newFilename);
    }

    @Test
    public void testFile() {
        /*boolean delete = new File(path + "aaaaa.jpg").delete();
        System.out.println(delete);*/
    }
}
