package fml.plus.auth.common.redisson.lock;

import fml.plus.auth.common.exception.LockAcquiredFailedException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Redisson锁相关Service
 * @author cicco
 */
@Slf4j
@AllArgsConstructor
public class LockService {
    private final RedissonClient redisson;

    /**
     * 尝试加锁(非公平)
     * @param lockKey 锁RedisKey
     * @param waitSeconds 最大等待时间(秒)
     * @param lockSeconds 最长持有锁时间(秒)
     * @return 是否获得锁 true:获取成功 false:获取失败
     */
    public boolean tryLock(String lockKey, int waitSeconds, int lockSeconds) {
        return tryLockOperation(redisson.getLock(lockKey), waitSeconds, lockSeconds);
    }


    /**
     * 释放锁
     * @param lockKey 锁RedisKey
     */
    public void releaseLock(String lockKey) {
        releaseLockOperation(redisson.getLock(lockKey));
    }

    /**
     * 使用Redis分布式锁(非公平锁)执行Task <br>
     * 若需要抛出异常: 请使用 {@link LockService#lockWithThrow(String, int, int, Runnable)}
     * @param lockKey 锁RedisKey
     * @param waitSeconds 最大等待时间(秒)
     * @param lockSeconds 最长持有锁时间(秒)
     * @param task 获取锁后执行的任务
     */
    public void lock(String lockKey, int waitSeconds, int lockSeconds, Runnable task) {
        lockOperation(
                LockParam.builder()
                        .lockKey(lockKey)
                        .waitSeconds(waitSeconds)
                        .lockSeconds(lockSeconds)
                        .task(task)
                        .build()
        );
    }

    /**
     * 使用Redis分布式锁(非公平锁)执行Task, 未获得锁时不抛出异常<br>
     * 默认等待时间30秒, 锁最长持有时间30秒<br>
     * 若需要抛出异常: 请使用 {@link LockService#lockWithThrow(String, int, int, Runnable)}
     * @param lockKey 锁RedisKey
     * @param task 获取锁后执行的任务
     */
    public void lock(String lockKey, Runnable task) {
        lock(lockKey, 30, 30, task);
    }

    /**
     * 使用Redis分布式锁(非公平锁)执行Task, 未获得锁时抛出异常<br>
     * 若不需要抛出异常: 请使用 {@link LockService#lock(String, int, int, Runnable)}
     * @param lockKey 锁RedisKey
     * @param waitSeconds 最大等待时间(秒)
     * @param lockSeconds 最长持有锁时间(秒)
     * @param task 获取锁后执行的任务
     * @throws LockAcquiredFailedException 未获得锁时, 抛出此异常
     */
    public void lockWithThrow(String lockKey, int waitSeconds, int lockSeconds, Runnable task) {
        lockOperation(
                LockParam.builder()
                        .lockKey(lockKey)
                        .waitSeconds(waitSeconds)
                        .lockSeconds(lockSeconds)
                        .failThrow(true)
                        .task(task)
                        .build()
        );
    }
    /**
     * 使用Redis分布式锁(非公平锁)执行Task, 未获得锁时抛出异常 <br>
     * 默认等待时间30秒, 锁最长持有时间30秒<br>
     * 若不需要抛出异常: 请使用 {@link LockService#lock(String, int, int, Runnable)}
     * @param lockKey 锁RedisKey
     * @param task 获取锁后执行的任务
     * @throws LockAcquiredFailedException 未获得锁时, 抛出此异常
     */
    public void lockWithThrow(String lockKey, Runnable task) {
        lockWithThrow(lockKey, 30, 30, task);
    }

    /**
     * 使用Redis分布式锁(公平锁)执行Task <br>
     * 若需要抛出异常: 请使用 {@link LockService#fairLockWithThrow(String, int, int, Runnable)}}
     * @param lockKey 锁RedisKey
     * @param waitSeconds 最大等待时间(秒)
     * @param lockSeconds 最长持有锁时间(秒)
     * @param task 获取锁后执行的任务
     * @throws LockAcquiredFailedException 未获得锁时, 抛出此异常
     */
    public void fairLock(String lockKey, int waitSeconds, int lockSeconds, Runnable task) {
        lockOperation(
                LockParam.builder()
                    .fairLock(true)
                    .lockKey(lockKey)
                    .waitSeconds(waitSeconds)
                    .lockSeconds(lockSeconds)
                    .task(task)
                .build()
        );
    }

    /**
     * 使用Redis分布式锁(公平锁)执行Task <br>
     * 默认等待时间30秒, 锁最长持有时间30秒<br>
     * 若需要抛出异常: 请使用 {@link LockService#fairLockWithThrow(String, int, int, Runnable)}}
     * @param lockKey 锁RedisKey
     * @param task 获取锁后执行的任务
     */
    public void fairLock(String lockKey, Runnable task) {
        fairLock(lockKey, 30, 30, task);
    }

    /**
     * 使用Redis分布式锁(公平锁)执行Task, 未获得锁时抛出异常<br>
     * 若不需要抛出异常: 请使用 {@link LockService#fairLock(String, int, int, Runnable)}
     * @param lockKey 锁RedisKey
     * @param waitSeconds 最大等待时间(秒)
     * @param lockSeconds 最长持有锁时间(秒)
     * @param task 获取锁后执行的任务
     * @throws LockAcquiredFailedException 未获得锁时, 抛出此异常
     */
    public void fairLockWithThrow(String lockKey, int waitSeconds, int lockSeconds, Runnable task) {
        lockOperation(
                LockParam.builder()
                        .fairLock(true)
                        .failThrow(true)
                        .lockKey(lockKey)
                        .waitSeconds(waitSeconds)
                        .lockSeconds(lockSeconds)
                        .task(task)
                        .build()
        );
    }
    /**
     * 使用Redis分布式锁(公平锁)执行Task, 未获得锁时抛出异常<br>
     * 默认等待时间30秒, 锁最长持有时间30秒<br>
     * 若不需要抛出异常: 请使用 {@link LockService#fairLock(String, int, int, Runnable)}
     * @param lockKey 锁RedisKey
     * @param task 获取锁后执行的任务
     * @throws LockAcquiredFailedException 未获得锁时, 抛出此异常
     */
    public void fairLockWithThrow(String lockKey, Runnable task) {
        fairLockWithThrow(lockKey, 30, 30, task);
    }

    private boolean tryLockOperation(RLock lock, int waitSeconds, int lockSeconds) {
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitSeconds, lockSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("尝试获得锁时发生InterruptedException,key:{}", lock.getName(), e);
            Thread.currentThread().interrupt();
        }
        return lockResult;
    }

    private void releaseLockOperation(RLock lock) {
        try {
            log.debug("尝试释放锁[{}]", lock.getName());
            lock.unlock();
            log.debug("释放锁[{}]成功", lock.getName());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not locked by current thread")) {
                log.debug("锁{}已被提前释放，无需解锁", lock.getName());
            } else {
                throw e;
            }
        }
    }

    private void lockOperation(LockParam param) {
        // 校验参数
        param.verify();

        String lockKey = param.lockKey;

        RLock lock = param.fairLock ? redisson.getFairLock(lockKey) : redisson.getLock(lockKey);
        log.debug("尝试获得锁，key:{},waitSeconds:{},lockSeconds:{}", lockKey, param.waitSeconds, param.lockSeconds);

        if (tryLockOperation(lock, param.waitSeconds, param.lockSeconds)) {
            log.debug("获得锁[{}]成功", lockKey);
            try {
                param.task.run();
            } finally {
                releaseLockOperation(lock);
            }
        } else {
            if(param.failThrow) throw new LockAcquiredFailedException("未能获得锁["+lockKey+"]");
            else log.debug("获得锁[{}]失败", lockKey);
        }
    }

    @Builder
    @AllArgsConstructor
    @Data
    private static class LockParam {
        private String lockKey; // Redis锁的Key
        private int waitSeconds; // 等待秒数
        private int lockSeconds; // 持有锁最长时间
        private boolean failThrow; // 获取锁失败时是否抛出{LockAcquiredFailedException}异常
        private Runnable task; // 执行的任务
        private boolean fairLock; // 是否使用公平锁

        void verify() {
            if(lockKey == null || lockKey.isBlank()) {
                throw new IllegalArgumentException("Redisson LockKey不能为空");
            }
            if(waitSeconds < 0) {
                throw new IllegalArgumentException("Lock waitSeconds(最大等待锁的时间)不能小于0");
            }
            if(lockSeconds < 0) {
                throw new IllegalArgumentException("Lock lockSeconds(最大持有锁的时间)不能小于0");
            }
            if(task == null) {
                throw new IllegalArgumentException("提交的Task不能为空");
            }
        }
    }
}
