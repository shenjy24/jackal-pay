package com.jonas.pay.repository.redis;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 支付序号的 Redis DAO
 *
 * @author 芋道源码
 */
@Repository
@RequiredArgsConstructor
public class PayNoRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 生成序号
     *
     * @param prefix 前缀
     * @return 序号
     */
    public String generate(String prefix) {
        // 递增序号
        String noPrefix = prefix + DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        String key = RedisKeyConstant.PAY_NO + noPrefix;
        Long no = stringRedisTemplate.opsForValue().increment(key);
        // 设置过期时间
        stringRedisTemplate.expire(key, Duration.ofMinutes(1L));
        return noPrefix + no;
    }

}
