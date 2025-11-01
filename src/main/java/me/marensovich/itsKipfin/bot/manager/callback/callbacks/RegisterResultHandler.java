package me.marensovich.itsKipfin.bot.manager.callback.callbacks;

import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;
import me.marensovich.itsKipfin.bot.manager.callback.interfaces.PrefixCallbackHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The type Register result handler.
 */
@Component
public class RegisterResultHandler implements PrefixCallbackHandler {
    @Override
    public String getPrefixCallbackData() {
        return RegisterITCButton.ITC_ADMIN_REG_DEFARAMENT_PREFIX;
    }

    @Override
    public void handle(Update update) {
        String[] parts = update.getCallbackQuery().getData().split(":");
        String departament = parts[1];
        String result = parts[2];
        String id = parts[3];

        //TODO: 2025-11-02T00:49:06.988+03:00 ERROR 148 --- [ITC_KIPIFIN] [legram Executor] o.t.t.u.DefaultBotSession                : Index 3 out of bounds for length 3
        //
        //java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3
        //	at me.marensovich.itsKipfin.bot.manager.callback.callbacks.RegisterResultHandler.handle(RegisterResultHandler.java:20) ~[main/:na]

        switch (departament) {
            case RegisterITCButton.ITC_REGISTRATION_DEPARTAMENT_PROJECT_TEAM -> {
                switch (result) {
                    case "YES" -> {
                        RegisterITCButton.ProjectTeamHandler.handleResultYes(id);
                    }
                    case "NO" -> {
                        RegisterITCButton.ProjectTeamHandler.handleResultNo(id);
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
