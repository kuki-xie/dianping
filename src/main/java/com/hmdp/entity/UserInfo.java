package com.hmdp.entity;
/**
 * 定义了一个名为 UserInfo 的 Java 实体类，主要用于表示用户的详细信息。
 * 该类实现了 Serializable 接口，以支持对象的序列化和反序列化，方便在网络传输或持久化存储时使用。
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 类级别注解：
 *
 * @Data：来自 Lombok 库，自动生成该类的 getter、setter、toString、equals 和 hashCode 方法，减少样板代码。
 * @EqualsAndHashCode(callSuper = false)：同样来自 Lombok，生成 equals 和 hashCode 方法，
 * callSuper = false 表示不调用父类的方法。
 * @Accessors(chain = true)：允许使用链式方法调用，即 setter 方法返回当前对象本身，方便连续设置属性。
 * @TableName("tb_user_info")：来自 MyBatis-Plus，指定该实体类对应的数据库表名为 tb_user_info。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字段级别注解：
     *
     * @TableId(value = "user_id", type = IdType.AUTO)：
     * 指定 userId 字段为主键，且主键生成策略为自动增长。
     */

    /**
     * 主键，用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 类字段：
     * userId：用户的唯一标识符，对应数据库中的主键。
     * city：用户所在的城市名称。
     * introduce：个人介绍，限制不超过128个字符。
     * fans：粉丝数量。
     * followee：关注的人的数量。
     * gender：性别，使用布尔值表示，0：男，1：女。
     * birthday：生日，使用 LocalDate 类型。
     * credits：积分。
     * level：会员级别，0~9级，0代表未开通会员。
     * createTime 和 updateTime：记录用户信息的创建和更新时间，类型为 LocalDateTime。
     */

    /**
     * 城市名称
     */
    private String city;

    /**
     * 个人介绍，不要超过128个字符
     */
    private String introduce;

    /**
     * 粉丝数量
     */
    private Integer fans;

    /**
     * 关注的人的数量
     */
    private Integer followee;

    /**
     * 性别，0：男，1：女
     */
    private Boolean gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 积分
     */
    private Integer credits;

    /**
     * 会员级别，0~9级,0代表未开通会员
     */
    private Boolean level;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
