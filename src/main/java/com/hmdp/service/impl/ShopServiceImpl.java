package com.hmdp.service.impl;
/**
 * ShopServiceImpl 是一个实现了 IShopService 接口的服务类，
 * 继承自 ServiceImpl<ShopMapper, Shop>，
 * 用于处理与店铺相关的业务逻辑。该类的 queryById 方法主要负责根据店铺 ID 查询店铺信息，
 * 并在查询过程中应用缓存策略，以提升系统性能并防止缓存相关问题。
 */

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.hmdp.utils.RedisData;
import com.hmdp.utils.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    // 这里需要声明一个线程池，因为下面缓存击穿问题，我们需要新建一个线程来完成重构缓存
    /*
    1. 线程池声明：
    这里声明了一个固定大小为 10 的线程池 CACHE_REBUILD_EXECUTOR，用于在缓存重建时执行异步任务。
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    /*
    2. StringRedisTemplate 注入：
    通过 @Autowired 注解将 StringRedisTemplate 注入，方便在方法中与 Redis 进行交互。
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /*
    3. queryById 方法
     */
    @Override
    public Result queryById(Long id) {
        // 解决缓存穿透的代码逻辑
        Shop shop = querywithchuantou(id);
        // 利用互斥锁解决缓存击穿的代码逻辑
        // Shop shop = querywithjichuan_mutex(id);
        // Shop shop = queryWithLogicalExpire(id);
        if (shop == null) {
            return Result.fail("店铺不存在！！");
        }
        return Result.ok(shop);
    }

    /*
    querywithchuantou 方法旨在解决缓存穿透问题。缓存穿透是指对一个在缓存和数据库中都不存在的数据进行频繁查询，
    导致每次请求都直接访问数据库，增加了数据库的压力。
     */
    public Shop querywithchuantou(Long id) {
        /*
        1. 从 Redis 查询缓存：
        首先，通过 stringRedisTemplate 从 Redis 中获取键为 CACHE_SHOP_KEY + id 的缓存数据。
        这里，CACHE_SHOP_KEY 是缓存键的前缀，id 是店铺的唯一标识符。
         */
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        /*
        2. 判断缓存中是否存在数据：
        2.1 缓存命中的话，直接返回店铺数据
        如果 shopJson 不为空（字符串不是 null、""、全空格），说明缓存中有数据。
        则将其反序列化为 Shop 对象并返回，避免了对数据库的访问。

        StrUtil.isNotBlank(shopJson) 用于判断 shopJson 是否不为 null、不为空字符串，
        且不全是空白字符。
         */
        if (StrUtil.isNotBlank(shopJson)) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }

        /*
        2.2 缓存未命中
         */
        if (shopJson != null) {
            /*
            2.2.1 缓存中为空字符串：
            如果 shopJson 不为 null，但前面的 isNotBlank 判断未通过，
            说明 shopJson 是一个空字符串或仅包含空白字符。
            表明该店铺不存在。因此，直接返回 null，避免再次访问数据库。
             */
            return null;
        }

        /*
        2.2.2 当前数据是null,查询数据库：
        如果缓存中没有相关数据（即 shopJson 为 null），则查询数据库获取店铺信息。
         */
        Shop shop = getById(id);

        /*
        3. 判断数据库中是否存在该数据：
         */
        if (shop == null) {
            // 这里的常量值是2分钟
            /*
            3.1 数据库中也没，则缓存空对象
            如果数据库中也没有该店铺的信息，则将空字符串存入 Redis，并设置一个较短的过期时间（CACHE_NULL_TTL，例如 2 分钟）。
            这样可以在短时间内避免对同一不存在的数据频繁查询数据库，减轻数据库压力。
             */
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        /*
        3.2 数据库中存在，缓存数据库中存在的数据：
        如果数据库中存在该店铺的信息，则将其序列化为 JSON 字符串，存入 Redis，
        并设置一个较长的过期时间（CACHE_SHOP_TTL，例如 30 分钟）。这样可以在后续相同的查询中直接从缓存获取数据，提高查询效率。
         */
        String jsonStr = JSONUtil.toJsonStr(shop);
        // 并存入redis,并设置TTL，防止存了错的缓存
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, jsonStr, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // 4. 最终把查询到的商户信息返回给前端
        return shop;
    }

    /*
    二、缓存击穿的解决：法一，互斥锁
    引入“互斥锁”来确保只有一个线程去查询数据库和重建缓存，其他线程等待或重试。
     */
    public Shop querywithjichuan_mutex(Long id) {
        // 1. 先从Redis中查，这里的常量值是固定的前缀 + 店铺id
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        /*
        2. 判断缓存是否命中
        2.1 缓存命中了
        2.1.1 如果 shopJson 不为空（字符串不是 null、""、全空格），说明缓存中有数据。
        把 json 字符串转为 Shop 对象直接返回，不需要访问数据库。
         */
        if (StrUtil.isNotBlank(shopJson)) {
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }

        /* 2.1.2 如果查询到的是空字符串“”，则说明是我们缓存的空数据
        之前数据库查不到这条数据，我们会把一个空字符串放进缓存。
        这样后续请求查到这个空字符串就不会再去访问数据库，避免缓存穿透。
         */
        if (shopJson != null) {
            return null;
        }
        /*
        2.2 缓存未命中，开始加锁重建缓存
        实现在高并发的情况下缓存重建
        调用 tryLock() 尝试获取互斥锁（通常是 Redis 的 SETNX 实现）。
         */

        Shop shop = null;
        try {
            // 获取互斥锁
            boolean flag = tryLock(LOCK_SHOP_KEY + id);
            /* 2.2.1 加锁失败就休眠重试（自旋）
            如果拿不到锁，说明其他线程正在重建缓存。
            当前线程休眠 50 毫秒后递归调用自己重新尝试查询缓存。
            这个过程称为“自旋锁”或“延迟重试”。
             */
            while (!flag) {
                Thread.sleep(50);
                return querywithjichuan_mutex(id);
            }
            // 2.2.2 获取成功->读取数据库，重建缓存
            // 2.2.2.1 查不到，则将空值写入Redis
            shop = getById(id);
            if (shop == null) {
                stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            /*
            2.2.2.2 数据库中有数据，写入缓存
            查到了则转为json字符串
             */
            String jsonStr = JSONUtil.toJsonStr(shop);
            // 并存入redis，设置TTL
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, jsonStr, CACHE_SHOP_TTL, TimeUnit.MINUTES);
            // 最终把查询到的商户信息返回给前端
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 2.2.3 最后：释放锁
            unlock(LOCK_SHOP_KEY + id);
        }
        return shop;
    }

    // 逻辑过期解决缓存击穿
    public Shop queryWithLogicalExpire(Long id) {
        // 1. 从redis中查询商铺缓存
        String json = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        /*
        2. 判断是否在缓存中
        2.1 如果未命中，则返回空，直接失败
         */
        if (StrUtil.isBlank(json)) {
            return null;
        }
        // 2.2 命中，将json反序列化为对象,并判断是否过期
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        // 将data转为Shop对象
        JSONObject shopJson = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(shopJson, Shop.class);
        // 获取过期时间
        LocalDateTime expireTime = redisData.getExpireTime();
        // 3. 判断是否过期
        if (LocalDateTime.now().isBefore(expireTime)) {
            // 3.1 未过期，直接返回商铺信息
            return shop;
        }
        // 4 过期，尝试获取互斥锁
        boolean flag = tryLock(LOCK_SHOP_KEY + id);
        // 4.1 获取到了锁
        if (flag) {
            //  开启独立线程
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    this.saveShop2Redis(id, 20L);// 此处的expirSeconds应该为物品的活动时间,设置为20只为测试
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(LOCK_SHOP_KEY + id);
                }
            });
            //  直接返回商铺信息
            return shop;
        }
        // 4.2 未获取到锁，直接返回商铺信息
        return shop;
    }

    /*
    主要功能是更新店铺信息，并确保数据库与缓存中的数据一致。
    1. 方法签名与注解：
    @Override：表明该方法重写了父类或接口中的方法。
    @Transactional：这是Spring框架提供的声明式事务管理注解，
    表示该方法中的所有数据库操作要么全部成功提交，要么全部失败回滚，以确保数据的一致性和完整性。
     */
    @Override
    @Transactional
    public Result update(Shop shop) {
        //  首先先判一下空
        /*
        2. 参数检查：
        在执行更新操作前，首先检查传入的shop对象的id是否为空。
        如果为空，说明无法确定要更新的店铺，方法返回一个失败的结果，提示“店铺id不能为空！！”。
         */
        if (shop.getId() == null) {
            return Result.fail("店铺id不能为空！！");
        }

        /*
        3. 先修改数据库
        调用该方法根据shop对象的id在数据库中找到对应的记录，并更新其信息。
         */
        updateById(shop);

        /*
        4. 再删除缓存：
        使用StringRedisTemplate对象删除Redis中对应店铺的缓存数据。
        这里缓存的键是通过常量CACHE_SHOP_KEY与店铺id拼接而成的字符串。
        删除缓存的目的是在下次获取店铺信息时，避免使用过期的数据，从而确保数据的实时性和一致性。
         */
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        // 5. 如果上述操作全部成功，返回一个表示成功的结果。
        return Result.ok();
    }


    /*
    下面用来解决热点高并发访问中的缓存击穿问题
    实现了一个基于 Redis 的分布式锁机制，包含获取锁和释放锁的逻辑。
     */
    /*
    1. 获取锁的逻辑：
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        // 避免返回值为null，我们这里使用了BooleanUtil工具类
        return BooleanUtil.isTrue(flag);
    }

    // 2. 释放锁
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    //-------------------------------------------------------------
    // 逻辑过期实现缓存击穿问题->热点问题的数据预热
    public void saveShop2Redis(Long id, Long expirSeconds) throws InterruptedException {
        Shop shop = getById(id);
        Thread.sleep(200); // 模拟上面取数据的时间

        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expirSeconds));
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 1. 判断是否需要根据距离查询
        if (x == null || y == null) {
            // 根据类型分页查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }
//        以下是需要根据距离查询

        // 2. 计算分页查询参数
        int from = (current - 1) * SystemConstants.MAX_PAGE_SIZE;
        int end = current * SystemConstants.MAX_PAGE_SIZE;


        String key = SHOP_GEO_KEY + typeId;
        // 3. 查询redis、按照距离排序、分页; 结果：shopId、distance
        // GEOSEARCH key FROMLONLAT x y BYRADIUS 5000 m WITHDIST
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo().search(key,
                GeoReference.fromCoordinate(x, y),
                new Distance(5000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end));

        if (results == null) {
            return Result.ok(Collections.emptyList());
        }

        // 4. 解析出id
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();

        if (list.size() < from) {
            // 起始查询位置大于数据总量，则说明没数据了，返回空集合
            return Result.ok(Collections.emptyList());
        }

        ArrayList<Long> ids = new ArrayList<>(list.size());
        HashMap<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });


        // 5. 根据id查询shop
        String idsStr = StrUtil.join(",", ids);

        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD( id," + idsStr + ")").list();
        for (Shop shop : shops) {
            // 设置shop的举例属性，从distanceMap中根据shopId查询
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        // 6. 返回
        return Result.ok(shops);
    }
}
