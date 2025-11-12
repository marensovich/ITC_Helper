package me.marensovich.itsKipfin.utils.exception;

import java.time.LocalDateTime;

/**
 * Represents a structured API error response.
 *
 * <p>Returned by {@link GlobalExceptionHandler}
 * when exceptions are thrown in the application.</p>
 *
 * @param error     error name (e.g. "Not Found", "Unauthorized")
 * @param message   error message
 * @param timestamp time of error occurrence
 * @author marensovich
 * @version v.0.1
 * @since v.0.1
 */
public record ApiError(
        String username,
        String userId,
        String chatId,
        String userRole,
        String userDepartment,
        String activeCommand,
        String activeButton,
        String error,
        String message,
        LocalDateTime timestamp
) { }
