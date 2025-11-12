package me.marensovich.itsKipfin.utils.exception;

import java.time.LocalDateTime;

/**
 * API ошибка.
 *
 * @param username       username пользователя в тг
 * @param userId         id пользователя в тг
 * @param chatId         id чата в тг
 * @param userRole       должность пользователя (e.g. "Admin", "User")
 * @param userDepartment отдел пользователя (e.g. "IT", "HR")
 * @param activeCommand  текущая активная команда
 * @param activeButton   текущая активная кнопка
 * @param error          имя исключения
 * @param message        текст исключения
 * @param timestamp      время срабатывания исключения
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
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
) {
}
