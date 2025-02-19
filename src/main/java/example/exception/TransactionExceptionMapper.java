package example.exception;

import org.hibernate.PessimisticLockException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.http.HttpStatus;

import com.yahoo.elide.ElideErrorResponse;
import com.yahoo.elide.ElideErrors;
import com.yahoo.elide.core.exceptions.ErrorContext;
import com.yahoo.elide.core.exceptions.ExceptionMapper;
import com.yahoo.elide.core.exceptions.TransactionException;

/**
 * {@link ExceptionMapper} for {@TransactionException} to handle database
 * constraint violations.
 */
public class TransactionExceptionMapper implements ExceptionMapper<TransactionException, ElideErrors> {
    @Override
    public ElideErrorResponse<? extends ElideErrors> toErrorResponse(TransactionException exception,
            ErrorContext errorContext) {
        Throwable cause = exception.getCause();
        if (cause instanceof ConstraintViolationException) {
            return ElideErrorResponse.status(HttpStatus.CONFLICT.value())
                    .errors(errors -> errors.error(error -> error.message("Unique or key constraint violation")
                            .attribute("type", "ConstraintViolation")));
        } else if (cause instanceof LockAcquisitionException) {
            return ElideErrorResponse.status(HttpStatus.CONFLICT.value())
                    .errors(errors -> errors
                            .error(error -> error.message("Cannot acquire lock").attribute("type", "LockAcquisition")));
        } else if (cause instanceof PessimisticLockException) {
            return ElideErrorResponse.status(HttpStatus.CONFLICT.value())
                    .errors(errors -> errors.error(error -> error.message("Cannot acquire pessimistic lock")
                            .attribute("type", "PessimisticLockAcquisition")));
        }
        return null; // Fallback on default handling for TransactionException
    }
}