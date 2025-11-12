package me.marensovich.itsKipfin.utils.exception;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Aspect
@Component
public class BotExceptionAspect {

    private final GlobalExceptionHandler exceptionHandler;

    public BotExceptionAspect(GlobalExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @AfterThrowing(
            pointcut = "execution(* me.marensovich.itsKipfin.bot..*(org.telegram.telegrambots.meta.api.objects.Update)) && args(update)",
            throwing = "e"
    )
    public void handleBotException(Exception e, Update update) {
        exceptionHandler.handle(e, update);
    }

}

