package com.hmdp.entity;
/**
 * 定义了一个名为 User 的 Java 实体类，主要用于表示系统中的用户信息。
 * 该类实现了 Serializable 接口，以支持对象的序列化和反序列化。
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
 * 类级别注解：
 *
 * @Data：来自 Lombok 库，自动生成该类的 getter、setter、toString、equals 和 hashCode 方法，
 * 减少样板代码。
 * @EqualsAndHashCode(callSuper = false)：同样来自 Lombok，生成 equals 和 hashCode 方法，
 * scallSuper = false 表示不调用父类的方法。
 * @Accessors(chain = true)：允许使用链式方法调用，即 setter 方法返回当前对象本身，方便连续设置属性。
 * @TableName("tb_user")：来自 MyBatis-Plus，指定该实体类对应的数据库表名为 tb_user。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段级别注解：
     *
     * @TableId(value = "id", type = IdType.AUTO)：指定 id 字段为主键，且主键生成策略为自动增长。
     */

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 类字段：
     *
     * id：用户的唯一标识符，对应数据库中的主键。
     *
     * phone：用户的手机号码。
     *
     * password：用户的密码，建议加密存储以确保安全性。
     *
     * nickName：用户的昵称，默认为随机字符。
     *
     * icon：用户的头像，默认为空字符串。
     *
     * createTime 和 updateTime：记录用户信息的创建和更新时间，类型为 LocalDateTime。
     */


    /**
     * 手机号码
     */
    private String phone;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 昵称，默认是随机字符
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String icon = "";

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
/**
 * 注意事项：
 * 安全性：对于 password 字段，需确保在存储前进行加密处理，以防止明文存储带来的安全风险。
 * 时间字段管理：createTime 和 updateTime 字段可以通过数据库的自动填充功能或在应用层进行管理，确保数据的准确性。
 * 通过使用 Lombok 和 MyBatis-Plus 的注解，简化了实体类的开发，提高了代码的可读性和维护性
 */