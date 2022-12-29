package fml.plus.auth.common.exception;

public class LockAcquiredFailedException extends RuntimeException {

    public LockAcquiredFailedException(String message) {
        super(message);
    }
}
