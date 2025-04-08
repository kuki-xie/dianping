package com.hmdp.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.utils.SystemConstants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 类注解部分
 *
 * @RestController：标识这个类是一个 REST 风格的控制器，返回的是 JSON 数据。
 * @RequestMapping("/shop")：给当前控制器设置统一的请求前缀，所有接口路径都会以 /shop 开头。
 */
@RestController
@RequestMapping("/shop")
public class ShopController {
    /**
     * 成员变量注入
     *
     * @Resource：自动注入服务层的 IShopService 实现类，用来处理商铺相关的业务逻辑。
     */
    @Resource
    public IShopService shopService;

    /**
     * 1. 根据 ID 查询商铺
     * 请求路径：/shop/{id}
     * 方法说明：根据商铺 ID 查询商铺的详情。
     * 参数解释：
     *
     * @PathVariable("id")：从 URL 路径中提取 id 参数。
     */
    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        return shopService.queryById(id);
    }

    /**
     * 2. 新增商铺
     * 请求方式：POST
     * 功能：新增一个商铺。
     * 参数解释：
     *
     * @RequestBody Shop shop：前端提交的 JSON 数据会被转换为 Shop 对象。
     */
    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        // 写入数据库
        shopService.save(shop);
        // 返回店铺id
        return Result.ok(shop.getId());
    }

    /**
     * 3. 更新商铺信息
     * 请求方式：PUT
     * 功能：更新现有商铺信息。
     * 参数同上。
     */
    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        // 写入数据库
        return shopService.update(shop);
    }

    /**
     * 4. 根据商铺类型分页查询商铺信息
     *
     * @param typeId  商铺类型
     * @param current 页码
     * @return 商铺列表
     * 请求路径：/shop/of/type
     * 功能：分页查询某一类型的商铺，支持根据经纬度（x, y）进行附近推荐。
     * 参数解释：
     * typeId：商铺类型 ID。
     * current：当前页码。
     * x, y：可选，经纬度坐标。
     */
    @GetMapping("/of/type")
    public Result queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Double y
    ) {
        return shopService.queryShopByType(typeId, current, x, y);
    }

    /**
     * 5. 根据名称模糊查询商铺
     *
     * @param name    商铺名称关键字
     * @param current 页码
     * @return 商铺列表
     * <p>
     * 请求路径：/shop/of/name
     * 功能：通过关键词模糊匹配商铺名称并分页返回结果。
     * 说明：
     * 使用了 StrUtil.isNotBlank() 判断是否输入了查询关键词。
     * SystemConstants.MAX_PAGE_SIZE：控制单页最大条数。
     * 返回当前页的所有商铺数据。
     */
    @GetMapping("/of/name")
    public Result queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 返回数据
        return Result.ok(page.getRecords());
    }
}
/**
 * 这个控制器实现了完整的商铺模块的基本功能，包括：
 * 查询商铺详情
 * 添加商铺
 * 修改商铺
 * 根据类型或名称进行分页查询
 * 支持定位服务（通过经纬度筛选附近店铺）
 */
