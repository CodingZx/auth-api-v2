package fml.plus.auth.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.google.common.base.Strings;
import fml.plus.auth.common.constants.RedisConstants;
import fml.plus.auth.common.exception.BusinessException;
import fml.plus.auth.common.exception.LockAcquiredFailedException;
import fml.plus.auth.common.model.Page;
import fml.plus.auth.common.model.Pager;
import fml.plus.auth.common.redisson.lock.LockService;
import fml.plus.auth.common.transaction.IAfterCommitExecutor;
import fml.plus.auth.dto.resp.ExceptionLogResp;
import fml.plus.auth.entity.ExceptionLogBackupEntity;
import fml.plus.auth.entity.ExceptionLogEntity;
import fml.plus.auth.mapper.ExceptionLogBackupMapper;
import fml.plus.auth.mapper.ExceptionLogMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(rollbackFor = Exception.class)
public class ExceptionLogService {
    private ExceptionLogMapper exceptionLogMapper;
    private ExceptionLogBackupMapper exceptionLogBackupMapper;
    private StringRedisTemplate redis;
    private LockService lockService;
    private IAfterCommitExecutor afterCommitExecutor;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Exception e) {
        log.warn(e.getMessage(), e);
        var str = ExceptionUtil.stacktraceToString(e, Integer.MAX_VALUE);
        save(e.getMessage(), str);
    }

    private void save(String message, String messageDetail) {
        var digestHex = ExceptionLogEntity.digestHex(message, messageDetail);
        try {
            lockService.lockWithThrow(RedisConstants.ExceptionKeys.getExceptionKey(digestHex), 0, 60, () -> {
                var ex = new ExceptionLogEntity(message, messageDetail);
                saveException(digestHex, ex);
            });
        } catch (LockAcquiredFailedException e){
            // 保存到backup
            exceptionLogBackupMapper.insert(new ExceptionLogBackupEntity(message, messageDetail));
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void saveException(String digestHex, ExceptionLogEntity ex) {
        var id = loadExceptionByMD5(digestHex);
        if(id == null) {
            // 新的错误信息
            exceptionLogMapper.insert(ex);
        } else {
            // 已经存在的错误信息
            exceptionLogMapper.updateCounter(id, ex.getLastReportTime());
        }
    }

    private UUID loadExceptionByMD5(String digestHex) {
        var ops = redis.boundValueOps(RedisConstants.ExceptionKeys.getExceptionIdKey(digestHex));
        var val = ops.get();
        if(Strings.isNullOrEmpty(val)) {
            var check = exceptionLogMapper.wrapper().eq(ExceptionLogEntity::getMsgMD5, digestHex).one();
            if(check == null) {
                return null;
            }
            ops.set(check.getId().toString(), 1, TimeUnit.HOURS);
            return check.getId();
        }
        ops.expire(1, TimeUnit.HOURS);
        return UUID.fromString(val);
    }


    @Scheduled(fixedDelay = 60, initialDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void scheduleExceptionBackup() {
        try {
            lockService.lock(RedisConstants.LockKeys.backupLockKey(), 0, 60, () -> {
                AtomicBoolean status = new AtomicBoolean();
                do {
                    var list = exceptionLogBackupMapper.wrapper().orderByAsc(ExceptionLogBackupEntity::getCreateTime).top(1000);
                    for(var backup : list) {
                        var ex = new ExceptionLogEntity(backup);

                        try {
                            lockService.lockWithThrow(RedisConstants.ExceptionKeys.getExceptionKey(ex.getMsgMD5()), 10, 10, () -> {
                                saveException(ex.getMsgMD5(), ex);
                                // 转移后 删除
                                exceptionLogBackupMapper.deleteById(backup.getId());
                            });
                        } catch (LockAcquiredFailedException ignore) {
                        } catch (Exception e) {
                            log.warn(e.getMessage(), e);
                        }
                    }
                    status.set(list.size() > 0);
                } while (status.get());
            });
        } catch (Exception e) {
            save(e);
        }
    }

    public Pager<ExceptionLogResp> list(Page page, int status) {
        var query = exceptionLogMapper.wrapper().page(page.toRow());
        if(status != -99) query.eq(ExceptionLogEntity::getStatus, ExceptionLogEntity.ExceptionLogStatus.of(status));
        query.orderByDesc(ExceptionLogEntity::getCreateTime);

        var list = query.list();
        var count = query.count();

        return Pager.of(list.stream().map(f -> new ExceptionLogResp(f, true)).collect(Collectors.toList()), count);
    }

    public ExceptionLogResp findById(UUID id) {
        var model = exceptionLogMapper.findById(id);
        if(model == null) throw new BusinessException("ID错误");
        return new ExceptionLogResp(model, false);
    }

    public void updateStatus(UUID id, int status) {
        var updateStatue = ExceptionLogEntity.ExceptionLogStatus.of(status);

        var update = new ExceptionLogEntity();
        update.setStatus(updateStatue);
        update.setId(id);
        exceptionLogMapper.updateSelectiveById(update);
    }

    public void delete(UUID id) {
        var msg = exceptionLogMapper.findById(id);
        if(msg == null) return;

        exceptionLogMapper.deleteById(id);
        afterCommitExecutor.run(() -> redis.delete(RedisConstants.ExceptionKeys.getExceptionIdKey(msg.getMsgMD5())));
    }
}
