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
     * @return the application
     */
    public Application createApplication(Application.Departament departament, Object dto, Long userId) {
        Application application = new Application();
        application.setDepartament(departament);
        application.setStatus(Application.Status.PENDING);
        application.setDataObject(dto);
        application.setId(userId);
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
