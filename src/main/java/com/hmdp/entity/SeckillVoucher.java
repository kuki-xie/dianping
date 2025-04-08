package com.hmdp.entity;
/**
 * 定义了一个名为 SeckillVoucher 的 Java 实体类，用于表示秒杀优惠券的信息。
 * 该类与数据库中的 tb_seckill_voucher 表相对应，包含了秒杀优惠券的各项属性。
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 秒杀优惠券表，与优惠券是一对一关系
 */

/**
 * 类级别的注解：
 *
 * @Data：这是 Lombok 提供的注解，自动生成类的 getter、setter、toString、equals 和 hashCode 方法，
 * 减少样板代码的编写。
 * @EqualsAndHashCode(callSuper = false)：同样是 Lombok 的注解，用于生成 equals 和 hashCode 方法。
 * 这里的 callSuper = false 表示在生成这些方法时，不调用父类的方法。
 * @Accessors(chain = true)：这是 Lombok 的另一个注解，允许使用链式调用的方式设置属性值。
 * 例如，可以通过 seckillVoucher.setStock(100).setBeginTime(LocalDateTime.now()) 的方式连续设置多个属性。
 * @TableName("tb_seckill_voucher")：这是 MyBatis-Plus 的注解，
 * 用于指定与该实体类对应的数据库表名为 tb_seckill_voucher。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_seckill_voucher")
public class SeckillVoucher implements Serializable {
    // serialVersionUID：这是序列化版本号，用于确保在反序列化时，类的版本一致性。
    private static final long serialVersionUID = 1L;
    /**
     * 字段级别的注解及属性说明：
     *
     * voucherId：关联的优惠券 ID，作为主键，唯一标识一条秒杀优惠券记录。使用 @TableId 注解，
     * 指定主键字段为 voucher_id，主键生成策略为手动输入（IdType.INPUT）。
     *
     * stock：库存，表示该秒杀优惠券的可用数量。
     *
     * createTime：创建时间，记录秒杀优惠券的创建时间，使用 LocalDateTime 类型。
     *
     * beginTime：生效时间，表示秒杀优惠券开始生效的时间。
     *
     * endTime：失效时间，表示秒杀优惠券失效的时间。
     *
     * updateTime：更新时间，记录秒杀优惠券信息的最后修改时间。
     */

    /**
     * 关联的优惠券的id
     */
    @TableId(value = "voucher_id", type = IdType.INPUT)
    private Long voucherId;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
// 通过上述注解和字段的配合，这个 SeckillVoucher 类既能方便地与数据库表进行映射，
// 又能在业务逻辑中清晰地表示秒杀优惠券的各项属性。