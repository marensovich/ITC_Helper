package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.DesignerHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.MediaHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.PRHandler;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITC.utils.handlers.ProjectTeamHandler;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import me.marensovich.itsKipfin.services.ApplicationService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик результата регистрации ИТС.
 * <p>
 * Используется администраторами для подтверждения или отклонения заявок.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class RegisterResultHandler implements PrefixCallbackHandler {

    private final ApplicationService applicationService;

    /**
     * Конструктор обработчика.
     *
     * @param applicationService сервис для работы с заявками
     * @author marensovich
     * @since 0.0.1
     */
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
                ProjectTeamHandler handler = new ProjectTeamHandler(applicationService);
                switch (result) {
                    case "YES" -> handler.handleResultYes(id, update);
                    case "NO" -> handler.handleResultNo(id, update);
                }
            }
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PR -> {
                PRHandler handler = new PRHandler(applicationService);
                switch (result) {
                    case "YES" -> handler.handleResultYes(id, update);
                    case "NO" -> handler.handleResultNo(id, update);
                }
            }
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_DESIGNER -> {
                DesignerHandler handler = new DesignerHandler(applicationService);
                switch (result) {
                    case "YES" -> handler.handleResultYes(id, update);
                    case "NO" -> handler.handleResultNo(id, update);
                }
            }
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_VIDEO_CONTENT -> {
                MediaHandler handler = new MediaHandler(applicationService);
                switch (result) {
                    case "YES" -> handler.handleResultYes(id, update);
                    case "NO" -> handler.handleResultNo(id, update);
                }
            }
        }
    }
}
