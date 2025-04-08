package com.hmdp.entity;
/**
 * 定义了一个名为 VoucherOrder 的 Java 实体类，主要用于表示代金券订单信息。
 * 该类实现了 Serializable 接口，以支持对象的序列化和反序列化，便于在网络传输或持久化存储时使用。
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
 * 类级别的注解：
 *
 * @Data：来自 Lombok 库，自动生成该类的 getter、setter、toString、equals 和 hashCode 方法，减少样板代码。
 * @EqualsAndHashCode(callSuper = false)：同样来自 Lombok，生成 equals 和 hashCode 方法，
 * callSuper = false 表示不调用父类的方法。
 * @Accessors(chain = true)：允许使用链式方法调用，即 setter 方法返回当前对象本身，方便连续设置属性。
 * @TableName("tb_voucher_order")：来自 MyBatis-Plus，指定该实体类对应的数据库表名为 tb_voucher_order。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher_order")
public class VoucherOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字段级别的注解：
     *
     * @TableId(value = "id", type = IdType.INPUT)：指定 id 字段为主键，
     * 且主键生成策略为 IdType.INPUT，即在插入数据时需要手动设置该字段的值，MyBatis-Plus 不会自动生成。
     */
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 下单的用户id
     */
    private Long userId;

    /**
     * 购买的代金券id
     */
    private Long voucherId;

    /**
     * 支付方式 1：余额支付；2：支付宝；3：微信
     */
    private Integer payType;

    /**
     * 订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款
     */
    private Integer status;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 核销时间
     */
    private LocalDateTime useTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
