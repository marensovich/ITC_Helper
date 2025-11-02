package me.marensovich.itsKipfin.services;

import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.database.repositories.ApplicationRepository;
import org.springframework.stereotype.Service;

/**
 * The type Application service.
 */
@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    /**
     * Instantiates a new Application service.
     *
     * @param applicationRepository the application repository
     */
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    /**
     * Create application application.
     *
     * @param departament the departament
     * @param dto         the dto
     * @param userId      the user id
     * @param messageId   the message id
     * @return the application
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
     * Update application status.
     *
     * @param id     the id
     * @param status the status
     */
    public void updateApplicationStatus(Long id, Application.Status status) {
        Application application = applicationRepository.findById(id).orElseThrow();
        application.setStatus(status);
        applicationRepository.save(application);
    }

    /**
     * Gets application by id.
     *
     * @param id the id
     * @return the application by id
     */
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id).orElseThrow();
    }

    /**
     * Update application message id application.
     *
     * @param id        the id
     * @param messageId the message id
     * @return the application
     */
    public Application updateApplicationMessageId(Long id, Long messageId) {
        Application application = applicationRepository.findById(id).orElseThrow();
        application.setMessageId(messageId);
        return applicationRepository.save(application);
    }

    /**
     * Is active application exists boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean isActiveApplicationExists(Long userId) {
        return applicationRepository.existsByIdAndStatus(userId, Application.Status.PENDING);
    }

}
