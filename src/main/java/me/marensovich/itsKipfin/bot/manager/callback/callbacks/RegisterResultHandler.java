package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import me.marensovich.itsKipfin.services.ApplicationService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The type Register result handler.
 */
@Component
public class RegisterResultHandler implements PrefixCallbackHandler {
    private final ApplicationService applicationService;

    public RegisterResultHandler(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    public String getPrefixCallbackData() {
        return RegisterITCButton.ITC_ADMIN_REG_DEFARAMENT_PREFIX;
    }

    @Override
    public void handle(Update update) {
        String[] parts = update.getCallbackQuery().getData().split(":");
        String department = parts[1];
        String result = parts[2];
        String id = parts[3];

        switch (department) {
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM -> {
                switch (result) {
                    case "YES" -> {
                        RegisterITCButton.ProjectTeamHandler handler = new RegisterITCButton.ProjectTeamHandler(applicationService);
                        handler.handleResultYes(id, update);
                    }
                    case "NO" -> {
                        RegisterITCButton.ProjectTeamHandler handler = new RegisterITCButton.ProjectTeamHandler(applicationService);
                        handler.handleResultNo(id, update);
                    }
                }
            }
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PR -> {

            }
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_DESIGNER -> {

            }
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT -> {

            }
        }
    }
}
