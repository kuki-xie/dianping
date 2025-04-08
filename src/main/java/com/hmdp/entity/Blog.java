package com.hmdp.entity;
/**
 * 定义了一个名为 Blog 的 Java 实体类，主要用于表示博客信息。
 * 该类使用了多个 Lombok 和 MyBatis-Plus 的注解，以简化代码编写并实现与数据库表的映射。
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
 * @Data：这是 Lombok 提供的注解，自动生成类的 getter、setter、toString、equals 和 hashCode 方法，减少样板代码。
 * @EqualsAndHashCode(callSuper = false)：同样是 Lombok 的注解，用于生成 equals 和 hashCode 方法。
 * 这里的 callSuper = false 表示在生成这些方法时，不调用父类的方法。
 * @Accessors(chain = true)：这是 Lombok 的另一个注解，允许使用链式调用的方式设置属性值。
 * 例如，可以通过 blog.setTitle("Title").setContent("Content") 的方式连续设置多个属性。
 * @TableName("tb_blog")：这是 MyBatis-Plus 的注解，用于指定与该实体类对应的数据库表名为 tb_blog。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字段级别的注解：
     *
     * @TableId(value = "id", type = IdType.AUTO)：这是 MyBatis-Plus 的注解，
     * 指定主键字段为 id，并且主键生成策略为自动增长（AUTO）。
     */

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 商户id
     */
    private Long shopId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户图标
     */

    /**
     * @TableField(exist = false)：用于标注那些在数据库表中不存在的字段。
     * 比如，icon、name 和 isLike 字段并不直接对应数据库表中的列，但可能在业务逻辑中需要使用，因此使用该注解标明。
     */
    @TableField(exist = false)
    private String icon;
    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String name;
    /**
     * 是否点赞过了
     */
    @TableField(exist = false)
    private Boolean isLike;

    /**
     * 标题
     */
    private String title;

    /**
     * 探店的照片，最多9张，多张以","隔开
     */
    private String images;

    /**
     * 探店的文字描述
     */
    private String content;

    /**
     * 点赞数量
     */
    private Integer liked;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 字段说明：
     *
     * id：博客的唯一标识符，对应数据库的主键。
     *
     * shopId：关联的商户 ID，表示该博客与某个商户相关联。
     *
     * userId：发布该博客的用户 ID。
     *
     * icon 和 name：这两个字段使用了 @TableField(exist = false) 注解，
     * 表示它们不是数据库表中的字段，可能用于在前端展示用户的头像和姓名。
     *
     * isLike：同样不是数据库表中的字段，可能用于表示当前用户是否点赞了该博客。
     *
     * title：博客的标题。
     *
     * images：博客包含的图片，最多9张，图片路径之间用逗号分隔。
     *
     * content：博客的文字描述内容。
     *
     * liked：点赞数量。
     *
     * comments：评论数量。
     *
     * createTime 和 updateTime：分别表示博客的创建时间和更新时间。
     */
}
