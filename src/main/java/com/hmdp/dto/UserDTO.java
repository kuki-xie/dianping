package com.hmdp.dto;

import lombok.Data;

/**
 * UserDTO 的数据传输对象（DTO，Data Transfer Object），用于在应用程序中传输用户相关的数据。
 * 该类包含以下三个字段：
 */

/*
为了简化代码，类上使用了 Lombok 提供的 @Data 注解。该注解会自动为类生成以下内容：

Getter 和 Setter 方法：为类的每个字段生成访问器和修改器方法。

toString 方法：生成一个包含类名和字段值的字符串表示。

equals 和 hashCode 方法：提供基于字段值的相等性比较和哈希码计算。

使用 @Data 注解可以减少样板代码，提高代码的可读性和维护性。
 */
@Data
public class UserDTO {
    /**
     * id：表示用户的唯一标识符，类型为 Long。
     * nickName：表示用户的昵称，类型为 String。
     * icon：表示用户的头像，类型为 String。
     */
    private Long id;
    private String nickName;
    private String icon;
}
/**
 * 使用场景：
 * UserDTO 类通常用于在不同层（如控制层、服务层、数据访问层）之间传输用户数据。
 * 通过使用 DTO，可以确保数据传输的安全性和一致性，同时避免直接暴露实体类的内部实现细节。
 */