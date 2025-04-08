package com.hmdp.controller;
/**
 * 定义了一个名为 UploadController 的 Spring Boot 控制器，主要用于处理博客图片的上传和删除操作。
 */

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.Result;
import com.hmdp.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 类级别注解
 *
 * @Slf4j：来自 Lombok 库，自动生成日志记录器，方便在类中使用 log 对象进行日志记录。
 * @RestController：标识该类为 RESTful 控制器，返回的结果直接作为 HTTP 响应体。
 * @RequestMapping("upload")：为该控制器指定基础请求路径，即所有以 /upload 开头的请求都会由此控制器处理。
 */
@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {
    /**
     * 方法解析
     * 1. 上传博客图片
     *
     * @param image
     * @return
     * @PostMapping("blog")：处理 /upload/blog 的 POST 请求，用于上传博客图片。
     * uploadImage 方法：
     * 参数：@RequestParam("file") MultipartFile image，接收上传的文件。
     * 步骤：
     * 1. 获取上传文件的原始名称。
     * 2. 调用 createNewFileName 方法生成新的文件名。
     * 3. 使用 transferTo 方法将文件保存到指定目录。
     * 4. 记录日志并返回成功结果。
     */
    @PostMapping("blog")
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            // 生成新文件名
            String fileName = createNewFileName(originalFilename);
            // 保存文件
            image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName));
            // 返回结果
            log.debug("文件上传成功，{}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 2. 删除博客图片
     *
     * @param filename
     * @return
     * @GetMapping("/blog/delete")：处理 /upload/blog/delete 的 GET 请求，用于删除指定名称的博客图片。
     * deleteBlogImg 方法：
     * 参数：@RequestParam("name") String filename，指定要删除的文件名。
     * 步骤：
     * 1. 根据提供的文件名创建文件对象。
     * 2. 检查该文件是否为目录，如果是，则返回错误信息。
     * 3. 使用 FileUtil.del 方法删除文件。
     * 4. 返回成功结果。
     */
    @GetMapping("/blog/delete")
    public Result deleteBlogImg(@RequestParam("name") String filename) {
        File file = new File(SystemConstants.IMAGE_UPLOAD_DIR, filename);
        if (file.isDirectory()) {
            return Result.fail("错误的文件名称");
        }
        FileUtil.del(file);
        return Result.ok();
    }

    /**
     * 3. 生成新文件名
     *
     * @param originalFilename
     * @return createNewFileName 方法：
     * 参数：originalFilename，原始文件名。
     * 步骤：
     * 1. 获取文件后缀名。
     * 2. 生成唯一的 UUID 作为新的文件名。
     * 3. 通过哈希值计算两个子目录，用于分散存储文件，避免单个目录下文件过多。
     * 4. 检查并创建对应的目录。
     * 5. 返回新的文件路径，包括子目录和新的文件名。
     */
    private String createNewFileName(String originalFilename) {
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("/blogs/{}/{}", d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format("/blogs/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
/**
 * 该控制器提供了博客图片的上传和删除功能。上传时，会根据原始文件名生成新的唯一文件名，
 * 并将文件存储在按照哈希值划分的子目录中，
 * 以优化文件存储结构。删除功能则根据提供的文件名删除对应的文件。
 */
