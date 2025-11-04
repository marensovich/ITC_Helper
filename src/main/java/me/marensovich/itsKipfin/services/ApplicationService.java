package me.marensovich.itsKipfin.services;

import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.database.repositories.ApplicationRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с заявками пользователей.
 * <p>
 * Предоставляет методы для создания, обновления и получения заявок из базы данных.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    /**
     * Конструктор сервиса заявок.
     *
     * @param applicationRepository репозиторий для работы с сущностями {@link Application}
     * @since 0.0.1
     */
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Создаёт новую заявку.
     *
     * @param departament отдел, в который подаётся заявка
     * @param dto         объект данных заявки (DTO)
     * @param userId      ID пользователя, отправившего заявку
     * @param messageId   ID сообщения в Telegram, связанного с заявкой
     * @return созданный объект {@link Application}
     * @author marensovich
     * @since 0.0.1
     */
    public Application createApplication(Application.Departament departament, Object dto, Long userId, Long messageId) {
        Application application = new Application();
        application.setDepartament(departament);
        application.setStatus(Application.Status.PENDING);
        application.setDataObject(dto);
        application.setUserId(userId);
        application.setMessageId(messageId);
        return applicationRepository.save(application);
    }

    /**
     * Обновляет статус заявки.
     *
     * @param id     ID заявки
     * @param status новый статус {@link Application.Status}
     * @author marensovich
     * @since 0.0.1
     */
    public void updateApplicationStatus(Long id, Application.Status status) {
        Application application = applicationRepository.findById(id).orElseThrow();
        application.setStatus(status);
        applicationRepository.save(application);
    }

    /**
     * Возвращает заявку по ID.
     *
     * @param id ID заявки
     * @return объект {@link Application}
     * @author marensovich
     * @since 0.0.1
     */
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id).orElseThrow();
    }

    /**
     * Обновляет ID сообщения, связанного с заявкой.
     *
     * @param id        ID заявки
     * @param messageId новый ID сообщения
     * @return обновлённый объект {@link Application}
     * @author marensovich
     * @since 0.0.1
     */
    public Application updateApplicationMessageId(Long id, Long messageId) {
        Application application = applicationRepository.findById(id).orElseThrow();
        application.setMessageId(messageId);
        return applicationRepository.save(application);
    }

    /**
     * Проверяет, существует ли активная (ожидающая) заявка у пользователя.
     *
     * @param userId ID пользователя
     * @return {@code true}, если активная заявка существует, иначе {@code false}
     * @author marensovich
     * @since 0.0.1
     */
    public boolean isActiveApplicationExists(Long userId) {
        return applicationRepository.existsApplicationByUserIdAndStatus(userId, Application.Status.PENDING);
    }

}
