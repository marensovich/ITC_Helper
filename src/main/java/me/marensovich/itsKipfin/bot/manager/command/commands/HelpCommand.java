package me.marensovich.itsKipfin.bot.manager.command.commands;

import me.marensovich.itsKipfin.bot.Bot;
import me.marensovich.itsKipfin.bot.manager.command.interfaces.Command;
import me.marensovich.itsKipfin.utils.exception.exceptions.BotException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class HelpCommand implements Command {
    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Помощь по боту";
    }

    @Override
    public boolean isAdminRequired() {
        return false;
    }

    @Override
    public void execute(Update update) {
        Bot.getInstance().getCommandManager().setActiveCommand(update.getMessage().getFrom().getId(), this);
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText("Если вам нужна помощь, пожалуйста, свяжитесь с нашим отделом поддержки по адресу");

        try {
            Bot.getInstance().showBotAction(update.getMessage().getFrom().getId(), ActionType.TYPING);
            Bot.getInstance().execute(message);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getMessage().getFrom().getId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            throw new BotException("Ошибка при отправке сообщения: " + e.getMessage(), update);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getFrom().getId());
        }
    }

    @Override
    public BotCommandScope getScope() {
        return Command.super.getScope();
    }
}
