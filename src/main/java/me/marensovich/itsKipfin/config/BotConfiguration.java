package me.marensovich.itsKipfin.config;

import me.marensovich.itsKipfin.bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * The type Bot configuration.
 */
@Configuration
public class BotConfiguration {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    /**
     * Telegram bots api telegram bots api.
     *
     * @param bot the bot
     * @return the telegram bots api
     * @throws TelegramApiException the telegram api exception
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(Bot bot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(bot);
        return botsApi;
    }

    /**
     * Vpn bot bot.
     *
     * @return the bot
     */
    @Bean
    public Bot vpnBot() {
        return new Bot(botToken, botUsername);
    }

}
