package com.hmdp.entity;
/**
 * 定义了一个名为 ShopType 的 Java 实体类，用于表示商铺类型的信息，并与数据库中的 tb_shop_type 表进行映射。
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 类级别的注解：
 *
 * @Data：这是 Lombok 提供的注解，自动生成该类的 getter、setter、
 * toString、equals 和 hashCode 方法，减少样板代码的编写。
 * @EqualsAndHashCode(callSuper = false)：同样是 Lombok 的注解，用于生成 equals 和 hashCode 方法。
 * 这里的 callSuper = false 表示在生成这些方法时，不调用父类的方法。
 * @Accessors(chain = true)：这是 Lombok 的另一个注解，允许使用链式调用的方式设置属性值。
 * 例如，可以通过 shopType.setName("餐饮").setIcon("icon.png") 的方式连续设置多个属性。
 * @TableName("tb_shop_type")：这是 MyBatis-Plus 的注解，指定该实体类对应的数据库表名为 tb_shop_type。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop_type")
public class ShopType implements Serializable {
    // serialVersionUID：这是序列化版本号，用于确保在反序列化时，类的版本一致性。
    private static final long serialVersionUID = 1L;

    /**
     * 字段级别的注解及属性说明：
     *
     * id：主键，唯一标识一个商铺类型记录。使用 @TableId 注解，指定主键字段为 id，主键生成策略为自动增长（IdType.AUTO）。
     *
     * name：类型名称，表示商铺类型的名称，如“餐饮”、“娱乐”等。
     *
     * icon：图标，存储商铺类型的图标路径或名称，用于前端展示。
     *
     * sort：顺序，表示商铺类型的显示顺序，数值越小，排序越靠前。
     *
     * createTime：创建时间，记录该商铺类型记录的创建时间。
     * 使用 @JsonIgnore 注解，表示在进行 JSON 序列化时忽略该字段，避免将其暴露给前端。
     *
     * updateTime：更新时间，记录该商铺类型记录的最后修改时间。
     * 同样使用了 @JsonIgnore 注解，避免在 JSON 序列化时暴露。
     */

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonIgnore
    private LocalDateTime updateTime;


}
