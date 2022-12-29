package fml.plus.auth.service;

import com.fasterxml.uuid.Generators;
import com.google.common.base.Strings;
import com.google.gson.reflect.TypeToken;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.redisson.lock.LockService;
import fml.plus.auth.common.util.GsonUtils;
import fml.plus.auth.entity.ConfigEntity;
import fml.plus.auth.mapper.ConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fml.plus.auth.common.constants.RedisConstants.NULL_VAL;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ConfigService {

    @Autowired
    private StringRedisTemplate redis;
    @Autowired
    private LockService lockService;
    @Autowired
    private ConfigMapper configMapper;

    /**
     * 初始化配置
     */
    public void initConfig(String key, String value) {
        lockService.lock(RedisConstants.LockKeys.initConfig(key), 0, 10, () -> {
            var check = configMapper.findByKey(key);
            if(check == null) {
                var model = new ConfigEntity();
                model.setId(Generators.timeBasedGenerator().generate());
                model.setValues(value);
                model.setConfigKey(key);
                model.setCreateTime(LocalDateTime.now());
                configMapper.insert(model);
            } else {
                log.debug("Config [{}] is exist. continue...", key);
            }
        });
    }


    /**
     * 获取配置
     * @throws BusinessException 格式错误时
     */
    public <T> T getObject(String key, Class<T> type) {
        var str = getStr(key, null);
        if(str == null) return null;
        try {
            return GsonUtils.gson().fromJson(str, type);
        } catch (Exception e) {
            throw new BusinessException("gson format error");
        }
    }

    /**
     * 获取配置
     * @throws BusinessException 格式错误时
     */
    public <T> List<T> getList(String key) {
        var str = getStr(key, null);
        if(str == null) return new ArrayList<>();
        try {
            return GsonUtils.gson().fromJson(str, new TypeToken<List<T>>(){}.getType());
        } catch (Exception e) {
            throw new BusinessException("gson format error");
        }
    }

    /**
     * 获取配置
     */
    public boolean getBool(String key, boolean defaultVal) {
        return Boolean.parseBoolean(getStr(key, Boolean.toString(defaultVal)));
    }

    /**
     * 获取配置
     * @throws NullPointerException  数据库中没有Key的记录
     */
    public boolean getBool(String key) {
        var val = getStr(key, null);
        if(val == null) throw new NullPointerException();
        return Boolean.parseBoolean(val);
    }

    /**
     * 获取配置
     */
    public float getFloat(String key, float defaultVal) {
        return Float.parseFloat(getStr(key, Float.toString(defaultVal)));
    }

    /**
     * 获取配置
     * @throws NumberFormatException  数据库中没有Key的记录或格式错误
     */
    public float getFloat(String key) {
        return Float.parseFloat(getStr(key, ""));
    }

    /**
     * 获取配置
     */
    public double getDouble(String key, double defaultVal) {
        return Double.parseDouble(getStr(key, Double.toString(defaultVal)));
    }

    /**
     * 获取配置
     * @throws NumberFormatException  数据库中没有Key的记录或格式错误
     */
    public double getDouble(String key) {
        return Double.parseDouble(getStr(key, ""));
    }

    /**
     * 获取配置
     */
    public byte getByte(String key, byte defaultVal) {
        return Byte.parseByte(getStr(key, Byte.toString(defaultVal)));
    }

    /**
     * 获取配置
     * @throws NumberFormatException  数据库中没有Key的记录或格式错误
     */
    public byte getByte(String key) {
        return Byte.parseByte(getStr(key, ""));
    }

    /**
     * 获取配置
     * @throws NumberFormatException  数据库中没有Key的记录或格式错误
     */
    public long getLong(String key) {
        return Long.parseLong(getStr(key, ""));
    }

    /**
     * 获取配置
     */
    public long getLong(String key, long defaultVal) {
        return Long.parseLong(getStr(key, Long.toString(defaultVal)));
    }

    /**
     * 获取配置
     * @throws NumberFormatException  数据库中没有Key的记录或格式错误
     */
    public int getInt(String key) {
        return Integer.parseInt(getStr(key, ""));
    }

    /**
     * 获取配置
     */
    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(getStr(key, Integer.toString(defaultValue)));
    }

    /**
     * 获取配置
     */
    public String getStr(String key) {
        return getStr(key, "");
    }

    /**
     * 获取配置
     */
    public String getStr(String key, String defaultVal) {
        var ops = redis.boundValueOps(RedisConstants.ConfigKeys.getConfigKey(key));
        var val = ops.get();
        if(!Strings.isNullOrEmpty(val)) {
            ops.expire(1, TimeUnit.DAYS);
            if(val.equals(NULL_VAL)) return defaultVal;
            else return val;
        }

        var model = configMapper.findByKey(key);
        if(model == null) {
            ops.set(NULL_VAL, 1, TimeUnit.DAYS);
            return defaultVal;
        }
        ops.set(model.getValues(), 1, TimeUnit.DAYS);
        return model.getValues();
    }


}
