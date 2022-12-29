package fml.plus.auth.common.transaction;

public interface IAfterCommitExecutor {

    /**
     * 提交任务
     */
    void run(Runnable runnable);
}
