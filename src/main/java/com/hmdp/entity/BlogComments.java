package com.hmdp.entity;
/**
 * 定义了一个名为 BlogComments 的 Java 实体类，用于表示博客的评论信息。
 * 该类与数据库中的 tb_blog_comments 表相对应，包含了评论的各项属性。
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
 * @Data：这是 Lombok 提供的注解，自动生成类的 getter、setter、toString、equals 和 hashCode 方法，
 * 减少样板代码的编写。
 * @EqualsAndHashCode(callSuper = false)：同样是 Lombok 的注解，用于生成 equals 和 hashCode 方法。
 * 这里的 callSuper = false 表示在生成这些方法时，不调用父类的方法。
 * @Accessors(chain = true)：这是 Lombok 的另一个注解，允许使用链式调用的方式设置属性值。
 * 例如，可以通过 blogComments.setContent("content").setLiked(10) 的方式连续设置多个属性。
 * @TableName("tb_blog_comments")：这是 MyBatis-Plus 的注解，
 * 用于指定与该实体类对应的数据库表名为 tb_blog_comments。
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog_comments")
public class BlogComments implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段级别的注解及属性说明：
     *
     * id：主键，唯一标识一条评论记录。使用 @TableId 注解，指定主键字段为 id，
     * 主键生成策略为自动增长（IdType.AUTO）。
     *
     * userId：用户 ID，表示发表评论的用户的唯一标识符。
     *
     * blogId：博客 ID，表示该评论所属的博客的唯一标识符。
     *
     * parentId：关联的一级评论 ID。如果该评论是一级评论，则值为 0；如果是对某条评论的回复，则值为被回复评论的 ID。
     *
     * answerId：回复的评论 ID，表示当前评论是对哪条评论的直接回复。
     *
     * content：评论的内容，存储用户的评论文本。
     *
     * liked：点赞数，表示该评论被点赞的次数。
     *
     * status：评论的状态，使用 Boolean 类型表示。0 表示正常，1 表示被举报，2 表示禁止查看。
     *
     * createTime 和 updateTime：分别表示评论的创建时间和更新时间，使用 LocalDateTime 类型记录时间戳。
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
     * 探店id
     */
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    private Long parentId;

    /**
     * 回复的评论id
     */
    private Long answerId;

    /**
     * 回复的内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer liked;

    /**
     * 状态，0：正常，1：被举报，2：禁止查看
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
