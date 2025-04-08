package com.hmdp.service.impl;
/**
 * 使用 Spring 框架和 MyBatis-Plus 的服务实现类
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.MailUtils;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * @Slf4j 注解：
 * 这是来自 Lombok 库的注解，自动为类生成一个名为 log 的日志记录器对象，方便在类中进行日志记录。
 * @Service 注解：
 * 这是 Spring 的注解，标识该类是一个服务组件（Service），
 * 使其能够被 Spring 的组件扫描机制发现并注册为 Spring 容器的 Bean。
 */
@Slf4j
@Service
/**
 * 类声明：
 * UserServiceImpl 类继承自 MyBatis-Plus 提供的 ServiceImpl 类，并实现了自定义的 IUserService 接口。
 * ServiceImpl<UserMapper, User>：
 * UserMapper：这是 MyBatis-Plus 的 Mapper 接口，定义了针对 User 实体的数据库操作方法。
 * User：这是对应的实体类，通常与数据库中的用户表对应。
 * 通过继承 ServiceImpl，UserServiceImpl 类自动拥有了基本的 CRUD 操作，无需手动编写。
 */
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    /**
     * @Autowired 注解：
     * 将 Spring 容器中的 StringRedisTemplate Bean 自动注入到 stringRedisTemplate 字段中。
     * StringRedisTemplate：
     * 这是 Spring Data Redis 提供的一个模板类，专门用于操作 Redis 中的字符串数据。
     * 它是 RedisTemplate 的一个子类，默认使用 StringRedisSerializer 来序列化键和值，
     * 简化了对字符串类型数据的操作。
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 发短信
    @Override
    public Result sendCode(String phone, HttpSession session) throws MessagingException {
        /**
         * 1. 一级限制检查（5分钟限制）：
         * 目的：检查该手机号是否在一级限制（5分钟内禁止发送）中。
         * 实现：在 Redis 的集合（Set）中查询键为 ONE_LEVERLIMIT_KEY + phone 的集合中是否存在值 "1"。
         * 逻辑：如果存在，说明该手机号在5分钟的限制期内，返回失败信息。
         */
        Boolean oneLevelLimit = stringRedisTemplate.opsForSet().isMember(ONE_LEVERLIMIT_KEY + phone, "1");
        if (oneLevelLimit != null && oneLevelLimit) {
            // 在一级限制条件内，不能发送验证码
            return Result.fail("您需要等5分钟后再请求");
        }
        /**
         * 2. 二级限制检查（20分钟限制）：
         * 目的：检查该手机号是否在二级限制（20分钟内禁止发送）中。
         * 实现：同样在 Redis 的集合中查询键为 TWO_LEVERLIMIT_KEY + phone 的集合中是否存在值 "1"。
         * 逻辑：如果存在，说明该手机号在20分钟的限制期内，返回失败信息。
         */
        // 2. 判断是否在二级限制条件内
        Boolean twoLevelLimit = stringRedisTemplate.opsForSet().isMember(TWO_LEVERLIMIT_KEY + phone, "1");
        if (twoLevelLimit != null && twoLevelLimit) {
            // 在二级限制条件内，不能发送验证码
            return Result.fail("您需要等20分钟后再请求");
        }

        /**
         * 3. 一分钟内发送次数检查：
         * 目的：确保同一手机号在1分钟内最多发送一次验证码。
         * 实现：使用 Redis 的有序集合（ZSet），统计键为 SENDCODE_SENDTIME_KEY + phone 的集合中，
         * 分数在当前时间减去1分钟到当前时间之间的元素数量。
         * 逻辑：如果数量大于等于1，说明1分钟内已发送过验证码，返回失败信息。
         */
        // 3. 检查过去1分钟内发送验证码的次数
        /*
        1. 计算一分钟前的时间戳：
        System.currentTimeMillis()：获取当前时间的毫秒数。
        60 * 1000：表示60秒（即1分钟）的毫秒数。
        System.currentTimeMillis() - 60 * 1000：计算出1分钟前的时间戳。
        这行代码的作用是确定时间窗口的起点，即从当前时间回溯1分钟。
         */
        long oneMinuteAgo = System.currentTimeMillis() - 60 * 1000;
        /*
        2. 查询过去一分钟内的发送次数：
        stringRedisTemplate.opsForZSet()：获取与Redis的有序集合（Sorted Set）相关的操作对象。
        count(SENDCODE_SENDTIME_KEY + phone, oneMinuteAgo, System.currentTimeMillis())：
        统计在键为SENDCODE_SENDTIME_KEY + phone的有序集合中，分数（score）在oneMinuteAgo到当前时间之间的元素数量。
        这行代码的作用是统计在过去1分钟内，用户对应的有序集合中有多少条记录，即用户在这段时间内请求验证码的次数。
         */
        long count_oneminute = stringRedisTemplate.opsForZSet().count(SENDCODE_SENDTIME_KEY + phone, oneMinuteAgo, System.currentTimeMillis());
        if (count_oneminute >= 1) {
            // 过去1分钟内已经发送了1次，不能再发送验证码
            return Result.fail("距离上次发送时间不足1分钟，请1分钟后重试");
        }
        /**
         * 4. 五分钟内发送次数检查及限制升级：
         * 目的：在5分钟内发送次数达到特定阈值时，触发更高级别的限制。
         * 实现：
         * 统计5分钟内发送的验证码次数。
         * 如果发送次数达到特定值（如第8次、第11次等），将手机号加入二级限制集合，限制20分钟。
         * 如果5分钟内发送次数达到5次，加入一级限制集合，限制5分钟。
         * 逻辑：根据发送次数，动态调整限制级别，防止恶意频繁请求。
         */
        // 4. 检查发送验证码的次数
        long fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000;
        long count_fiveminute = stringRedisTemplate.opsForZSet().count(SENDCODE_SENDTIME_KEY + phone, fiveMinutesAgo, System.currentTimeMillis());
        if (count_fiveminute % 3 == 2 && count_fiveminute > 5) {
            // 发送了8, 11, 14, ...次，进入二级限制
            stringRedisTemplate.opsForSet().add(TWO_LEVERLIMIT_KEY + phone, "1");
            // expire():Set time to live for given key.
            stringRedisTemplate.expire(TWO_LEVERLIMIT_KEY + phone, 20, TimeUnit.MINUTES);
            return Result.fail("接下来如需再发送，请等20分钟后再请求");
        } else if (count_fiveminute == 5) {
            // 过去5分钟内已经发送了5次，进入一级限制
            stringRedisTemplate.opsForSet().add(ONE_LEVERLIMIT_KEY + phone, "1");
            stringRedisTemplate.expire(ONE_LEVERLIMIT_KEY + phone, 5, TimeUnit.MINUTES);
            return Result.fail("5分钟内已经发送了5次，接下来如需再发送请等待5分钟后重试");
        }

        /*
        生成并发送验证码：
        生成验证码：调用 MailUtils.achieveCode() 方法生成验证码。
        存储验证码：将生成的验证码存入 Redis，设置有效期为 LOGIN_CODE_TTL 分钟。
        日志记录：记录发送的验证码信息。
        发送验证码：调用 MailUtils.sendtoMail(phone, code) 方法，将验证码发送到指定手机号。
         */
        // 生成验证码
        String code = MailUtils.achieveCode();

        // 将生成的验证码保持到redis
        // Set the value and expiration timeout for key.
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        log.info("发送登录验证码：{}", code);
        // 发送验证码
        MailUtils.sendtoMail(phone, code);
        /*
        记录发送时间：
        记录每次发送验证码的时间，用于后续的
         */
        // 更新发送时间和次数
        // Add value to a sorted set at key, or update its score if it already exists.
        stringRedisTemplate.opsForZSet().add(SENDCODE_SENDTIME_KEY + phone, System.currentTimeMillis() + "", System.currentTimeMillis());

        return Result.ok();
    }

    /**
     * 实现了用户的登录和注册功能，主要流程包括：校验邮箱格式、验证验证码、查询或创建用户信息，
     * 以及生成并存储登录令牌（token）。
     *
     * @param loginForm
     * @param session
     * @return
     */
    // 登录注册
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        /*
        1. 获取登录信息：
        从 loginForm 对象中提取用户提交的邮箱（代码中变量名为 phone，但实际应为邮箱）和验证码。
         */
        String phone = loginForm.getPhone();
        String code = loginForm.getCode();
        /*
        校验邮箱格式：
        使用 RegexUtils.isEmailInvalid 方法检查邮箱格式是否有效。如果无效，返回错误信息。
         */
        // 检验手机号是否正确，不同的请求就应该再次去进行确认
        if (RegexUtils.isEmailInvalid(phone)) {
            // 如果无效，则直接返回
            return Result.fail("邮箱格式不正确！！");
        }
        /*
        3. 验证验证码：
        从Redis 中获取存储的验证码，并与用户提交的验证码进行比对。如果不匹配，返回错误信息。
         */
        String Cachecode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        // 1. 校验邮箱
        if (RegexUtils.isEmailInvalid(phone)) {
            return Result.fail("邮箱格式不正确！！");
        }
        // 2. 不符合格式则报错
        if (!code.equals(Cachecode)) {
            return Result.fail("无效的验证码");
        }
        // 如果上述都没有问题的话，就从数据库中查询该用户的信息

        /*
        4. 查询用户信息：
        在数据库中查询与该邮箱对应的用户信息。
        如果用户不存在，调用 createUser 方法创建新用户。
         */
        // select * from tb_user where phone = ?
        // 查询 = phone的单条记录
        User user = query().eq("phone", phone).one();

        // 判断用户是否存在
        if (user == null) {
            user = createuser(phone);
        }
        /*
        6. 生成并存储登录令牌（token）
        生成一个唯一的登录令牌（UUID）。
        将 User 对象转换为 UserDTO，然后存入 HashMap。
        将用户信息以哈希结构存储到 Redis，键为 LOGIN_USER_KEY + token。
        设置该令牌的有效期为 LOGIN_USER_TTL 分钟。
         */
        // 保存用户信息到Redis中
        String token = UUID.randomUUID().toString();

        // 7.2 将UserDto对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("id", String.valueOf(userDTO.getId()));
        userMap.put("nickName", userDTO.getNickName());
        userMap.put("icon", userDTO.getIcon());


        // 7.3 存储
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);

        // 7.4 设置token有效期为30分钟
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        /*
        7. 删除已使用的验证码：
        登录成功后，从 Redis 中删除已使用的验证码。
         */
        // 7.5 登陆成功则删除验证码信息
        stringRedisTemplate.delete(LOGIN_CODE_KEY + phone);

        /*
        8. 返回登录令牌：
        将生成的登录令牌返回给客户端，供后续请求使用。
         */
        return Result.ok(token);
    }

    /**
     * 定义了一个名为 createuser 的私有方法，接受一个字符串参数 phone，用于创建并返回一个新的 User 对象。
     *
     * @param phone
     * @return
     */
    private User createuser(String phone) {
        /*
        1. 创建用户对象：
        实例化了一个新的 User 对象，分配给变量 user。
         */
        User user = new User();
        /*
        2. 设置手机号：
        调用 user 对象的 setPhone 方法，将传入的 phone 参数赋值给用户的手机号属性。
         */
        user.setPhone(phone);
        /*
        3. 生成并设置随机昵称：
        这行代码为用户生成一个随机昵称，具体步骤如下：
        前缀：SystemConstants.USER_NICK_NAME_PREFIX 是一个常量，表示昵称的固定前缀。
        随机字符串：RandomUtil.randomString(10) 调用 RandomUtil 类的 randomString 方法生成一个长度为10的随机字符串。
        组合：将前缀和随机字符串拼接，形成最终的昵称，并通过 setNickName 方法设置给用户对象。
         */
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        // 保存用户 insert into tb_user(phone,nick_name) values(?,?)
        /*
        4. 保存用户：
        调用 save 方法将 user 对象保存到数据库中。这里假设 save 方法是当前类中的一个持久化方法，
        负责执行类似于 INSERT INTO tb_user(phone, nick_name) VALUES(?, ?) 的SQL语句。
         */
        save(user);
        return user;
    }

    @Override
    public Result sign() {
        // 1. 获取当前用户
        Long userId = UserHolder.getUser().getId();
        // 2. 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3. 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4. 获取今天是当月第几天(1~31)
        int dayOfMonth = now.getDayOfMonth();
        // 5. 写入Redis  BITSET key offset 1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        return Result.ok();
    }

    @Override
    public Result signCount() {
        // 1. 获取当前用户
        Long userId = UserHolder.getUser().getId();
        // 2. 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3. 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4. 获取今天是当月第几天(1~31)
        int dayOfMonth = now.getDayOfMonth();


        // 5. 获取截止至今日的签到记录  BITFIELD key GET uDay 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(key, BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        if (result == null || result.isEmpty()) {
            return Result.ok(0);
        }
        // 6. 循环遍历
        int count = 0;
        Long num = result.get(0);
        while (true) {
            if ((num & 1) == 0) {
                break;
            } else
                count++;
            // 数字右移，抛弃最后一位
            num = num >>> 1;
        }
        return Result.ok(count);
    }
}
