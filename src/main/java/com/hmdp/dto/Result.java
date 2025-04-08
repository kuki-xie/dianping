package com.hmdp.dto;
/**
 * 定义了一个名为 Result 的 Java 类，用于统一表示操作结果，特别是在处理成功或失败的场景中。
 * 该类利用了 Lombok 提供的注解，简化了代码的编写。
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Lombok 注解的作用
 *
 * @Data： 该注解是 Lombok 的快捷方式，集合了 @Getter、@Setter、@ToString、@EqualsAndHashCode
 * 和 @RequiredArgsConstructor 等功能。
 * 使用 @Data 后，编译器会自动为类的所有字段生成 getter 和 setter 方法，
 * 以及 toString、equals 和 hashCode 方法。
 * @NoArgsConstructor：生成一个无参构造函数。对于需要无参构造函数的场景（如某些框架要求），该注解非常有用。
 * @AllArgsConstructor：生成一个包含类中所有字段的构造函数。这样可以方便地通过构造器初始化对象的所有属性。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Result {
    /**
     * 类的字段及方法
     * 字段：
     * success：表示操作是否成功的标志。
     * errorMsg：当操作失败时，存储错误信息。
     * data：存储操作结果的数据。
     * total：通常用于分页场景，表示数据总数。
     */
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    /**
     * 静态方法：
     *
     * @return
     */
    // ok()：创建一个表示成功且无数据的 Result 对象。
    public static Result ok() {
        return new Result(true, null, null, null);
    }

    // ok(Object data)：创建一个表示成功并包含数据的 Result 对象。
    public static Result ok(Object data) {
        return new Result(true, null, data, null);
    }

    // ok(List<?> data, Long total)：创建一个表示成功、包含数据列表和总数的 Result 对象，常用于分页查询。
    public static Result ok(List<?> data, Long total) {
        return new Result(true, null, data, total);
    }

    // fail(String errorMsg)：创建一个表示失败并包含错误信息的 Result 对象。
    public static Result fail(String errorMsg) {
        return new Result(false, errorMsg, null, null);
    }
}
