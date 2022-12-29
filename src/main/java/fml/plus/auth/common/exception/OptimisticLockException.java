package fml.plus.auth.common.exception;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException() {
        super();
    }

    public OptimisticLockException(String msg) {
        super(msg);
    }

    public OptimisticLockException(Throwable e) {
        super(e);
    }
}
