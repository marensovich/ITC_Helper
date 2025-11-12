package me.marensovich.itsKipfin.settings.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO класс в документацией
 *
 * @since 0.0.1
 * @author marensovich
 * @version 0.0.1
 */
@Data
public class BotSettings {

    GeneralSettings generalSettings = new GeneralSettings();
    Applications applications = new Applications();
    Departaments departaments = new Departaments();

    @Data
    public static class GeneralSettings {
        private String botName = "ИТС Помощник";
        private String botDescription = "Помощник ИТС — твой наставник в Информационно-Техническом Сообществе: новости, заявки и полезные ресурсы.";
        private String botShortDescription = "Помощник ИТС — твой наставник в Информационно-Техническом Сообществе: новости, заявки и полезные ресурсы. Для начала работы используйте команду /start";

        private String adminChannelId = "-1003395394215";
        private String adminLogChannelThreadId = "11";
        private String adminBotErrorMessageThreadId = "13";

    }

    @Data
    public static class Departaments {
        ProjectTeam projectTeam = new ProjectTeam();
        Media media = new Media();
        PR pr = new PR();
        Designer designer = new Designer();
        Head head = new Head();

        @Data
        public static class Head {
            public List<String> presidentOfITC = new ArrayList<>();
            public List<String> curatorITC = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        @Data
        public static class ProjectTeam {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        @Data
        public static class Media {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        @Data
        public static class PR {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        @Data
        public static class Designer {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }
    }

    @Data
    public static class Applications {
        ProjectTeamApplication projectTeamApplication = new ProjectTeamApplication();
        MediaApplication mediaApplication = new MediaApplication();
        PRApplication prApplication = new PRApplication();
        DesignerApplication designerApplication = new DesignerApplication();

        private String newApplicationNotificationChannelId;

        @Data
        public static class ProjectTeamApplication {
            private String newApplicationNotificationThreadId;
        }

        @Data
        public static class MediaApplication {
            private String newApplicationNotificationThreadId;
        }

        @Data
        public static class PRApplication {
            private String newApplicationNotificationThreadId;
        }

        @Data
        public static class DesignerApplication {
            private String newApplicationNotificationThreadId;
        }
    }
}
