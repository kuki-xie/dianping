package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.entity.BlogComments;

/**
 * 这段代码定义了一个名为 BlogCommentsMapper 的接口，并继承了 MyBatis-Plus 提供的 BaseMapper 接口。
 * 通过继承 BaseMapper<BlogComments>，
 * BlogCommentsMapper 接口自动具备了对 BlogComments 实体类的基本 CRUD（创建、读取、更新、删除）操作方法，
 * 无需手动编写 SQL 语句。
 */
public interface BlogCommentsMapper extends BaseMapper<BlogComments> {

}
/**
 * 具体来说，BaseMapper 接口提供了以下常用方法：
 * insert(T entity): 插入一条记录。
 * deleteById(Serializable id): 根据主键 ID 删除一条记录。
 * updateById(T entity): 根据主键 ID 更新一条记录。
 * selectById(Serializable id): 根据主键 ID 查询一条记录。
 * selectList(Wrapper<T> queryWrapper): 根据条件查询记录列表。
 * 通过继承 BaseMapper，开发者可以直接使用这些方法对 BlogComments 实体进行数据库操作，
 * 极大地简化了数据访问层的开发工作，提高了开发效率。
 */