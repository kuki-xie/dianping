package com.hmdp.entity;

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
 * @Data：这是 Lombok 提供的注解，自动生成类的 getter、setter、toString、equals 和 hashCode 方法，
 * 减少样板代码的编写。
 * @EqualsAndHashCode(callSuper = false)：同样是 Lombok 的注解，用于生成 equals 和 hashCode 方法。
 * 这里的 callSuper = false 表示在生成这些方法时，不调用父类的方法。
 * @Accessors(chain = true)：这是 Lombok 的另一个注解，允许使用链式调用的方式设置属性值。
 * 例如，可以通过 follow.setUserId(1L).setFollowUserId(2L) 的方式连续设置多个属性。
 * @TableName("tb_follow")：这是 MyBatis-Plus 的注解，用于指定与该实体类对应的数据库表名为 tb_follow。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_follow")
public class Follow implements Serializable {

    // serialVersionUID：这是序列化版本号，用于确保在反序列化时，类的版本一致性。
    private static final long serialVersionUID = 1L;
    /**
     * 字段级别的注解及属性说明：
     *
     * id：主键，唯一标识一条关注记录。使用 @TableId 注解，指定主键字段为 id，主键生成策略为自动增长（IdType.AUTO）。
     *
     * userId：用户 ID，表示发起关注操作的用户的唯一标识符。
     *
     * followUserId：被关注用户的 ID，表示被关注者的唯一标识符。
     *
     * createTime：创建时间，记录关注关系建立的时间，使用 LocalDateTime 类型。
     */

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 关联的用户id
     */
    private Long followUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
/**
 * 通过上述注解和字段的配合，这个 Follow 类既能方便地与数据库表进行映射，
 * 又能在业务逻辑中清晰地表示用户之间的关注关系。
 */
