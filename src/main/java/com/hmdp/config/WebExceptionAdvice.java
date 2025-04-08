package com.hmdp.config;
/**
 * 在 Spring Boot 应用中用于全局异常处理的类，旨在捕获并处理运行时异常（RuntimeException），以统一返回错误信息。
 */

import com.hmdp.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 1. @Slf4j 注解
 * 作用：这是来自 Lombok 库的注解，自动为类生成一个名为 log 的日志记录器对象，简化了日志记录的代码编写。
 * 使用该注解后，无需手动创建日志对象，即可直接使用 log 进行日志记录。
 */
@Slf4j
/**
 * 2. @RestControllerAdvice 注解
 * 作用：这是 Spring 提供的一个注解，标识该类为全局异常处理器，专门用于处理控制器（@RestController）中抛出的异常。
 * 与 @ControllerAdvice 类似，但 @RestControllerAdvice 默认会将返回值作为 JSON 格式输出，适用于 RESTful 风格的应用。
 */
@RestControllerAdvice
public class WebExceptionAdvice {
    /**
     * 4. @ExceptionHandler(RuntimeException.class) 注解
     * 作用：指定该方法用于处理 RuntimeException 类型的异常。当控制器中抛出此类异常时，会由该方法进行处理。
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    /**
     * 3. handleRuntimeException 方法
     * 参数：RuntimeException e，表示捕获到的运行时异常对象。
     * 返回值：Result 类型的对象，封装了错误信息，通常用于向客户端返回统一的响应结构。
     *
     * 方法逻辑：
     * 使用 log.error(e.toString(), e); 记录错误日志，便于开发人员排查问题。
     * 返回一个表示失败的 Result 对象，包含错误提示信息 "服务器异常"。
     */
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Result.fail("服务器异常");
    }
}
/**
 * 通过上述配置，当应用中的控制器方法抛出 RuntimeException 时，会被 handleRuntimeException 方法捕获并处理，
 * 返回统一的错误信息给客户端，同时记录详细的错误日志，方便维护和调试。
 */