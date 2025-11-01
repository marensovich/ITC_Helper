package me.marensovich.itsKipfin.services;

import me.marensovich.itsKipfin.database.models.Application;
import me.marensovich.itsKipfin.database.repositories.ApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application createApplication(Application.Departament departament, Object dto) {
        Application application = new Application();
        application.setDepartament(departament);
        application.setStatus(Application.Status.PENDING);
        application.setDataObject(dto);
        return applicationRepository.save(application);
    }

    public boolean isActiveApplicationExists(Long userId) {
        return applicationRepository.existsByIdAndStatus(userId, Application.Status.PENDING);
    }

}
