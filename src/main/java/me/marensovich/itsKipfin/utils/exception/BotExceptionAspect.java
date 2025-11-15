package me.marensovich.itsKipfin.utils.exception;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The type Bot exception aspect.
 */
@Aspect
@Component
public class BotExceptionAspect {

    private final GlobalExceptionHandler exceptionHandler;

    /**
     * Instantiates a new Bot exception aspect.
     *
     * @param exceptionHandler the exception handler
     */
    public BotExceptionAspect(GlobalExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Handle bot exception.
     *
     * @param e      the e
     * @param update the update
     */
    @AfterThrowing(
            pointcut = "execution(* me.marensovich.itsKipfin.bot..*(org.telegram.telegrambots.meta.api.objects.Update)) && args(update)",
            throwing = "e"
    )
    public void handleBotException(Exception e, Update update) {
        exceptionHandler.handle(e, update);
    }

}

