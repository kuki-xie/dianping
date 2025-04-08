package com.hmdp.dto;
/**
 * 名为 LoginFormDTO 的 Java 类，主要用于封装用户登录时提交的数据，包括手机号、验证码和密码。
 */

import lombok.Data;

/**
 * Lombok 的 @Data 注解
 *
 * @Data 是 Lombok 提供的一个注解，作用是自动为类生成以下常用方法：
 * Getter 方法：为所有字段生成公有的 getter 方法。
 * Setter 方法：为所有非 final 字段生成公有的 setter 方法。
 * toString() 方法：生成包含所有字段信息的 toString() 方法。
 * equals() 和 hashCode() 方法：基于类的所有字段生成 equals() 和 hashCode() 方法。
 * RequiredArgsConstructor 构造器：为所有被标记为 final 或带有 @NonNull 注解的字段生成一个包含这些参数的构造器。
 * <p>
 * 通过使用 @Data 注解，可以减少大量样板代码的编写，提高开发效率和代码可读性。
 */
@Data
/**
 * 数据传输对象（DTO）
 * LoginFormDTO 类是一个典型的数据传输对象（Data Transfer Object，DTO）。
 * DTO 的主要作用是封装数据，并在应用程序的不同层之间传递，特别是在客户端和服务器之间。
 * 它通常只包含字段及其访问方法，而不包含任何业务逻辑。使用 DTO 的好处包括：
 *
 * 1. 减少方法调用次数：通过一次性传递多个数据，降低远程调用的开销。
 * 2. 提高安全性：控制暴露的数据，避免直接暴露内部数据结构。
 * 3. 解耦：将数据表示与业务逻辑分离，增强系统的可维护性和扩展性。
 */
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
/**
 * 在本例中，LoginFormDTO 用于在用户登录时，将用户输入的手机号、验证码和密码封装为一个对象，
 * 便于在系统各层之间传递和处理。
 */