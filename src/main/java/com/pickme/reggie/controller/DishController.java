package com.pickme.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.pojo.dto.DishDto;
import com.pickme.reggie.pojo.Dish;
import com.pickme.reggie.service.DishService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 菜品管理
 */
@Api(tags = "菜品管理")
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /*@Autowired
    private RedisTemplate redisTemplate;
    private final String DISH_LIST = "dish_list_"; // key 前缀名*/

    /**
     * 添加菜品，为了确保数据的一致性，在添加完成后清除缓存
     * @param dishDto
     */
    @PostMapping
    public Res<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);

        /*//设置当前分类id的key
        String key = DISH_LIST + dishDto.getCategoryId();
        //清除当前分类的菜品缓存数据
        redisTemplate.delete(key);*/

        log.info("添加菜品：{}",dishDto.toString());
        return Res.success("");
    }


    /**
     * 删除和批量删除菜品
     * @param ids
     */
    @DeleteMapping
    public Res<String> delete(@RequestParam List<Long> ids) {
        dishService.removeWithFlavor(ids);

        /*//获取所有指定匹配内容的key，* 代表匹配任意字符
        Set keys = redisTemplate.keys(DISH_LIST + "*");
        //清理此所有缓存
        redisTemplate.delete(keys);*/

        log.info("删除菜品：{}",ids);
        return Res.success("");
    }


    /**
     * 修改菜品信息，修改完成后需要清除所有分类菜品缓存数据，因为如果修改的是菜品的分类信息，可能会导致数据错误
     * @param dishDto
     */
    @PutMapping
    public Res<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);

        /*Set keys = redisTemplate.keys(DISH_LIST + "*");
        redisTemplate.delete(keys);*/

        return Res.success("");
    }

    /**
     * 修改和批量修改菜品状态，启售或停售
     * @param s
     * @param ids
     */
    @PostMapping("/status/{sta}")
    public Res<String> status(@PathVariable Integer sta, Long[] ids) {
        dishService.updateStatus(sta,ids);

        /*Set keys = redisTemplate.keys(DISH_LIST + "*");
        redisTemplate.delete(keys);*/

        return Res.success("");
    }


    /**
     * 根据id查询，回显菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Res<DishDto> getObject(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Res.success(dishDto);
    }

    /**
     * 分页查询菜品信息，除了查询菜品的基本信息外，还要根据菜品分类id来查询分类信息。
     * @param page
     * @param pageSize
     * @param name
     * @return 泛型为 DishDto 的 Page，因为 DishDto 类带有 categoryName 属性来封装分类名称
     */
    @GetMapping("/page")
    public Res<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        Page<DishDto> dishDtoPage = dishService.pageWithCategory(page, pageSize, name);
        return Res.success(dishDtoPage);
    }

    /**
     * 移动端套餐和菜品查询，根据分类id或名称查询菜品
     * @优化: 使用redis缓存不同分类的菜品信息
     * @注意: 在使用缓存过程中，要注意保证数据库中的数据和缓存中的数据一致，如果数据库中的数据发生变化，需要及时清理缓存数据。
     *        否则就会造成缓存数据与数据库数据不一致的情况。
     * @param dish
     */
    @GetMapping("/list")
    public Res<List<DishDto>> list(Dish dish) {

        /*//动态设置key
        String key = DISH_LIST + dish.getCategoryId();
        //先从redis中获取缓存的菜品数据
        List<DishDto> dtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果有缓存数据，直接return
        if (dtoList != null) {
            return Res.success(dtoList);
        } else {
            //如果没有缓存菜品数据，查询菜品数据
            dtoList = dishService.listByCategoryId(dish);
            //将查询的菜品数据缓存在redis中，设置60分钟有效时间
            redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);
        }*/

        List<DishDto> DishDtoList = dishService.listByCategoryId(dish);
        return Res.success(DishDtoList);
    }
}
