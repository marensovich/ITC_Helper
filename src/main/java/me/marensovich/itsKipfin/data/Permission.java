package me.marensovich.itsKipfin.data;


/**
 * Перечисление доступных разрешений на взаимодействие с данными бота ИТС.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
public enum Permission {
    /**
     * Полные права на редактирование данных бота.
     */
    EDIT_ALL,
    /**
     * Права на редактирование данных конкретного отдела.
     */
    EDIT_DEPARTMENT,
    /**
     * Права на просмотр всех данных бота.
     */
    VIEW_ALL,
    /**
     * Права на просмотр данных конкретного отдела.
     */
    VIEW_DEPARTMENT

}
