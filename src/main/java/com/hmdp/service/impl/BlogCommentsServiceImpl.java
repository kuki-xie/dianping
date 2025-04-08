package com.hmdp.service.impl;
/**
 * 定义了一个名为 BlogCommentsServiceImpl 的服务实现类，主要用于处理与 BlogComments 实体相关的业务逻辑。
 * 它继承了 MyBatis-Plus 提供的 ServiceImpl 类，并实现了自定义的 IBlogCommentsService 接口。
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.entity.BlogComments;
import com.hmdp.mapper.BlogCommentsMapper;
import com.hmdp.service.IBlogCommentsService;
import org.springframework.stereotype.Service;

/**
 * 继承关系:
 * ServiceImpl<BlogCommentsMapper, BlogComments>: 这是 MyBatis-Plus 提供的一个通用服务实现类。
 * 通过继承它，BlogCommentsServiceImpl 获得了对 BlogComments 实体的基本 CRUD（创建、读取、更新、删除）操作方法。
 * BlogCommentsMapper: 这是对应的 Mapper 接口，负责与数据库进行交互。
 * BlogComments: 这是对应的实体类，映射到数据库中的表结构。
 * implements IBlogCommentsService: 实现自定义的服务接口，
 * 通常用于定义特定于 BlogComments 实体的业务方法。
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
/**
 * 主要功能
 * 通过继承 ServiceImpl，BlogCommentsServiceImpl 类自动具备了以下功能：
 * 基本的 CRUD 操作: 无需额外编写代码，即可使用如 save、removeById、updateById、
 * getById 等方法对 BlogComments 实体进行操作。
 * 条件查询: 可以使用 lambdaQuery、lambdaUpdate 等方法构建复杂的查询条件。
 * 分页查询: 结合 MyBatis-Plus 的分页插件，实现对 BlogComments 的分页查询。
 */
