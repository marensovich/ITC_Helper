package me.marensovich.itsKipfin.config;

import me.marensovich.itsKipfin.bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Конфигурационный класс Telegram-бота.
 * <p>
 * Отвечает за инициализацию и регистрацию экземпляра {@link Bot} в {@link TelegramBotsApi}.
 * Настройки (токен и имя бота) загружаются из {@code application.properties}.
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Configuration
public class BotConfiguration {

    /**
     * Токен Telegram-бота, задаётся в {@code application.properties}.
     * @since 0.0.1
     */
    @Value("${telegram.bot.token}")
    private String botToken;

    /**
     * Имя пользователя (username) Telegram-бота.
     * @since 0.0.1
     */
    @Value("${telegram.bot.username}")
    private String botUsername;

    /**
     * Создаёт и регистрирует экземпляр TelegramBots API.
     * <p>
     * Метод создаёт новый экземпляр {@link TelegramBotsApi} с использованием {@link DefaultBotSession},
     * регистрирует указанный экземпляр бота и возвращает API-объект.
     *
     * @param bot экземпляр Telegram-бота
     * @return зарегистрированный экземпляр {@link TelegramBotsApi}
     * @throws TelegramApiException если регистрация бота завершилась с ошибкой
     * @author marensovich
     * @since 0.0.1
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(Bot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return botsApi;
    }

    /**
     * Создаёт и возвращает экземпляр Telegram-бота.
     * <p>
     * Использует параметры, указанные в конфигурационном файле:
     * {@code telegram.bot.token} и {@code telegram.bot.username}.
     *
     * @return экземпляр {@link Bot}, готовый к регистрации в API
     * @author marensovich
     * @since 0.0.1
     */
    @Bean
    public Bot itcBot() {
        return new Bot(botToken, botUsername);
    }

}
