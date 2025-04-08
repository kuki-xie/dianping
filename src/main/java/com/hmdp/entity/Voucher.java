package com.hmdp.entity;
/**
 * 定义了一个名为 Voucher 的 Java 实体类，主要用于表示系统中的代金券信息。
 * 该类实现了 Serializable 接口，以支持对象的序列化和反序列化，方便在网络传输或持久化存储时使用。
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
 * 类级别注解：
 *
 * @Data：来自 Lombok 库，自动生成该类的 getter、setter、toString、equals 和 hashCode 方法，减少样板代码。
 * @EqualsAndHashCode(callSuper = false)：同样来自 Lombok，生成 equals 和 hashCode 方法，
 * callSuper = false 表示不调用父类的方法。
 * @Accessors(chain = true)：允许使用链式方法调用，即 setter 方法返回当前对象本身，方便连续设置属性。
 * @TableName("tb_voucher")：来自 MyBatis-Plus，指定该实体类对应的数据库表名为 tb_voucher。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_voucher")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字段级别注解：
     * @TableId(value = "id", type = IdType.AUTO)：指定 id 字段为主键，且主键生成策略为自动增长。
     * @TableField(exist = false)：用于标注那些在数据库表中不存在，但在业务逻辑中需要的字段，
     * 如 stock、beginTime 和 endTime。
     */

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 类字段：
     * id：代金券的唯一标识符，对应数据库中的主键。
     * shopId：关联的商铺 ID，表示该代金券适用于哪个商铺。
     * title：代金券的标题。
     * subTitle：代金券的副标题。
     * rules：使用规则，描述代金券的使用条件和限制。
     * payValue：支付金额，用户购买代金券需要支付的金额。
     * actualValue：抵扣金额，代金券可抵扣的金额。
     * type：代金券类型，使用整数表示不同的类型。
     * status：代金券状态，使用整数表示不同的状态。
     * stock：库存，表示代金券的可用数量。由于该字段使用了 @TableField(exist = false) 注解，
     * 说明它在数据库表中不存在，可能通过其他方式获取或计算得到。
     * beginTime 和 endTime：生效时间和失效时间，表示代金券的有效期。同样，这些字段在数据库表中不存在。
     * createTime 和 updateTime：记录代金券信息的创建和更新时间，类型为 LocalDateTime。
     */

    /**
     * 商铺id
     */
    private Long shopId;

    /**
     * 代金券标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 使用规则
     */
    private String rules;

    /**
     * 支付金额
     */
    private Long payValue;

    /**
     * 抵扣金额
     */
    private Long actualValue;

    /**
     * 优惠券类型
     */
    private Integer type;

    /**
     * 优惠券类型
     */
    private Integer status;
    /**
     * 库存
     */
    @TableField(exist = false)
    private Integer stock;

    /**
     * 生效时间
     */
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    @TableField(exist = false)
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
