package com.hmdp.entity;
/**
 * 定义了一个名为 Shop 的 Java 实体类，用于表示商铺的信息。
 * 该类与数据库中的 tb_shop 表相对应，包含了商铺的各项属性。
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 类级别的注解：
 *
 * @Data：这是 Lombok 提供的注解，自动生成类的 getter、setter、toString、equals 和 hashCode 方法，
 * 减少样板代码的编写。
 * @EqualsAndHashCode(callSuper = false)：同样是 Lombok 的注解，用于生成 equals 和 hashCode 方法。
 * 这里的 callSuper = false 表示在生成这些方法时，不调用父类的方法。
 * @Accessors(chain = true)：这是 Lombok 的另一个注解，允许使用链式调用的方式设置属性值。
 * 例如，可以通过 shop.setName("商铺名称").setAddress("商铺地址") 的方式连续设置多个属性。
 * @TableName("tb_shop")：这是 MyBatis-Plus 的注解，用于指定与该实体类对应的数据库表名为 tb_shop。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_shop")
public class Shop implements Serializable {

    // serialVersionUID：这是序列化版本号，用于确保在反序列化时，类的版本一致性。
    private static final long serialVersionUID = 1L;
    /**
     * 字段级别的注解及属性说明：
     *
     * id：主键，唯一标识一条商铺记录。使用 @TableId 注解，指定主键字段为 id，主键生成策略为自动增长（IdType.AUTO）。
     *
     * name：商铺名称，表示商铺的名称信息。
     *
     * typeId：商铺类型的 ID，表示商铺所属类型的唯一标识符。
     *
     * images：商铺图片，存储商铺的图片路径，多个图片以逗号（,）隔开。
     *
     * area：商圈，例如陆家嘴，表示商铺所在的商圈名称。
     *
     * address：地址，表示商铺的具体地址信息。
     *
     * x：经度，表示商铺所在位置的经度坐标。
     *
     * y：纬度，表示商铺所在位置的纬度坐标。
     *
     * avgPrice：均价，取整数，表示商铺的平均消费价格。
     *
     * sold：销量，表示商铺的销售数量。
     *
     * comments：评论数量，表示商铺收到的评论数量。
     *
     * score：评分，1~5 分，乘以 10 保存，避免小数，表示商铺的评分。
     *
     * openHours：营业时间，例如 10:00-22:00，表示商铺的营业时间段。
     *
     * createTime：创建时间，记录商铺信息的创建时间，使用 LocalDateTime 类型。
     *
     * updateTime：更新时间，记录商铺信息的最后修改时间。
     *
     * distance：距离，表示用户与商铺之间的距离。使用 @TableField(exist = false) 注解，
     * 表示该字段在数据库表中不存在，仅用于业务逻辑计算。
     */


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商铺名称
     */
    private String name;

    /**
     * 商铺类型的id
     */
    private Long typeId;

    /**
     * 商铺图片，多个图片以','隔开
     */
    private String images;

    /**
     * 商圈，例如陆家嘴
     */
    private String area;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private Double x;

    /**
     * 维度
     */
    private Double y;

    /**
     * 均价，取整数
     */
    private Long avgPrice;

    /**
     * 销量
     */
    private Integer sold;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    private String openHours;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    @TableField(exist = false)
    private Double distance;
}
