package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.Blog;

/**
 * BlogMapper 接口： 这是针对 Blog 实体类的 Mapper 接口。通过继承 BaseMapper<Blog>，
 * BlogMapper 获得了对 Blog 表的基本 CRUD 操作能力。
 * BaseMapper<Blog>： BaseMapper 是 MyBatis-Plus 提供的通用 Mapper 接口，泛型参数指定了操作的实体类类型。
 * 它包含了如 insert、deleteById、updateById、selectById 等常用方法。
 * 继承 BaseMapper 后，BlogMapper 即可使用这些方法，而无需编写具体的 SQL 语句
 */
public interface BlogMapper extends BaseMapper<Blog> {

}
/**
 * 在 MyBatis-Plus 中，BaseMapper 是一个通用 Mapper 接口，
 * 提供了对数据库的基本 CRUD（创建、读取、更新、删除）操作方法。
 * 通过让实体类对应的 Mapper 接口继承 BaseMapper，可以无需编写 XML 映射文件，即可直接使用这些通用方法进行数据库操作。
 */