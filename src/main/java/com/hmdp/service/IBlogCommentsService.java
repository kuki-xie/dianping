package com.hmdp.service;
/**
 * 定义了一个名为 IBlogCommentsService 的接口，
 * 它继承自 MyBatis-Plus 提供的 IService<BlogComments> 接口。
 * 通过继承 IService，IBlogCommentsService 接口
 * 获得了对 BlogComments 实体的通用 CRUD（创建、读取、更新、删除）操作方法。
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.entity.BlogComments;


public interface IBlogCommentsService extends IService<BlogComments> {

}
/**
 * MyBatis-Plus 中的 IService 接口：
 * IService 是 MyBatis-Plus 提供的一个通用服务层接口，封装了常见的数据库操作方法，如：
 * save：保存实体对象。
 * remove：根据条件删除记录。
 * list：查询所有记录。
 * update：根据条件更新记录。
 * 通过继承 IService，开发者无需手动编写上述方法的实现，从而提高开发效率。
 */
// 通过继承 MyBatis-Plus 的 IService 接口，
// IBlogCommentsService 接口获得了对 BlogComments 实体的通用数据库操作方法