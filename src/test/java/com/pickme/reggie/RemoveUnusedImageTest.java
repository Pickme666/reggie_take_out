package com.pickme.reggie;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pickme.reggie.pojo.Dish;
import com.pickme.reggie.pojo.Setmeal;
import com.pickme.reggie.service.DishService;
import com.pickme.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class RemoveUnusedImageTest {
    @Value("${reggie.path}")
    private String path;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 清除开发环境中没有被使用的图片资源
     */
    @Test
    public void testRemoveUnusedImage() {
        File file = new File(path);
        String[] images = file.list();
        log.info("图片目录：{}", Arrays.toString(images));
        List<String> removes = new ArrayList<>();

        assert images != null;
        for (String img : images) {
            LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(Dish::getImage, img);
            LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
            setmealWrapper.eq(Setmeal::getImage, img);

            //根据查询结果判断图片是否有被使用
            Dish dish = dishService.getOne(dishWrapper);
            Setmeal setmeal = setmealService.getOne(setmealWrapper);
            if (dish == null && setmeal == null) {
                //删除图片
                boolean delete = new File(path + img).delete();
                if (delete) removes.add(img);
            }
        }

        if (removes.size() > 0) {
            removes.forEach((item) -> log.info("已清理未使用的图片：{}", item));
            log.info("图片目录清理后：{}",Arrays.toString(file.list()));
        } else {
            log.info("没有要清理的图片：{}",removes);
        }
    }
}
