package me.marensovich.itsKipfin;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@SpringBootApplication
public class ItcKipifinApplication {

    /**
     * Метод запуска приложения
     *
     * @param args the input arguments
     * @author marensovich
     * @since 0.0.1
     */
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        SpringApplication.run(ItcKipifinApplication.class, args);
    }

}
