package com.hmdp.service.impl;
/**
 * 在Spring Boot应用中使用Redis作为缓存来查询店铺类型列表的实现。
 * 其主要目的是通过在缓存中存储店铺类型数据，减少对数据库的直接访问，提高查询效率。
 */

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;


@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryList() {
        // 先从Redis中查，这里的常量值是固定前缀 + 店铺id
        /*
        1. 从Redis缓存中获取店铺类型列表：
        这里使用了StringRedisTemplate的opsForList().range方法，
        从Redis的列表结构中获取所有存储的店铺类型数据。CACHE_SHOP_TYPE_KEY是缓存中存储店铺类型列表的键名。
        range方法的参数0和-1表示获取列表中的所有元素。
         */
        List<String> shopTypes =
                stringRedisTemplate.opsForList().range(CACHE_SHOP_TYPE_KEY, 0, -1);
        // 如果不为空（查询到了），则转为ShopType类型直接返回
        /*
        2. 判断缓存中是否存在店铺类型数据：
        如果从Redis中获取的shopTypes列表不为空，说明缓存中存在店铺类型数据。
        此时，将这些JSON格式的字符串转换为ShopType对象，并存入新的列表tmp中，最后返回包含店铺类型列表的结果。
         */
        if (!shopTypes.isEmpty()) {
            List<ShopType> tmp = new ArrayList<>();
            for (String types : shopTypes) {
                ShopType shopType = JSONUtil.toBean(types, ShopType.class);
                tmp.add(shopType);
            }
            return Result.ok(tmp);
        }
        // 否则去数据库中查
        /*
        3. 缓存中不存在数据时，从数据库查询：
        如果缓存中没有店铺类型数据，则通过数据库查询获取，并按照sort字段进行升序排序。
        若查询结果为空，返回错误信息。
         */
        List<ShopType> tmp = query().orderByAsc("sort").list();
        if (tmp == null) {
            return Result.fail("店铺类型不存在！！");
        }
        // 查到了转为json字符串，存入redis
        for (ShopType shopType : tmp) {
            String jsonStr = JSONUtil.toJsonStr(shopType);
            shopTypes.add(jsonStr);
        }
        stringRedisTemplate.opsForList().leftPushAll(CACHE_SHOP_TYPE_KEY, shopTypes);
        // 最终把查询到的商户分类信息返回给前端
        return Result.ok(tmp);
    }
}
