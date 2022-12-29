package fml.plus.auth.common.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Configuration
public class TransactionConfiguration {

    @Bean
    public IAfterCommitExecutor executor() {
        log.debug("Configuration TransactionExecutor..");
        return new AfterCommitExecutorImpl();
    }

    @Slf4j
    private static class AfterCommitExecutorImpl implements IAfterCommitExecutor {

        @Override
        public void run(Runnable runnable) {
            log.debug("Submitting new runnable {} to run after commit", runnable);
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                log.debug("Transaction synchronization is NOT ACTIVE. Executing right now runnable {}", runnable);
                runnable.run();
                return;
            }
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

                @Override
                public void afterCommit() {
                    log.debug("AfterCommit executing runnable {}", runnable);
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        log.error("AfterCommit failed to execute runnable " + runnable, e);
                    }
                }

                @Override
                public void afterCompletion(int status) {
                    log.debug("Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");
                }


            });
        }
    }

}
