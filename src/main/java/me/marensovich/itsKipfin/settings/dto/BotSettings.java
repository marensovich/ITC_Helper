package me.marensovich.itsKipfin.settings.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO класс в документацией
 *
 * @author marensovich
 * @version 0.0.1
 * @since 0.0.1
 */
@Data
public class BotSettings {

    /**
     * The General settings.
     */
    GeneralSettings generalSettings = new GeneralSettings();
    /**
     * The Applications.
     */
    Applications applications = new Applications();
    /**
     * The Departaments.
     */
    Departaments departaments = new Departaments();

    /**
     * The type General settings.
     */
    @Data
    public static class GeneralSettings {
        private String botName = "ИТС Помощник";
        private String botDescription = "Помощник ИТС — твой наставник в Информационно-Техническом Сообществе: новости, заявки и полезные ресурсы.";
        private String botShortDescription = "Помощник ИТС — твой наставник в Информационно-Техническом Сообществе: новости, заявки и полезные ресурсы. Для начала работы используйте команду /start";

        private String adminChannelId = "-1003395394215";
        private String adminLogChannelThreadId = "11";
        private String adminBotErrorMessageThreadId = "13";

    }

    /**
     * The type Departaments.
     */
    @Data
    public static class Departaments {
        /**
         * The Project team.
         */
        ProjectTeam projectTeam = new ProjectTeam();
        /**
         * The Media.
         */
        Media media = new Media();
        /**
         * The Pr.
         */
        PR pr = new PR();
        /**
         * The Designer.
         */
        Designer designer = new Designer();
        /**
         * The Head.
         */
        Head head = new Head();

        /**
         * The type Head.
         */
        @Data
        public static class Head {
            /**
             * The President of itc.
             */
            public List<String> presidentOfITC = new ArrayList<>();
            /**
             * The Curator itc.
             */
            public List<String> curatorITC = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        /**
         * The type Project team.
         */
        @Data
        public static class ProjectTeam {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        /**
         * The type Media.
         */
        @Data
        public static class Media {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        /**
         * The type Pr.
         */
        @Data
        public static class PR {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }

        /**
         * The type Designer.
         */
        @Data
        public static class Designer {
            private List<String> headsOfDepartment = new ArrayList<>();
            private List<String> deputyHeadsOfDepartment = new ArrayList<>();
            private String mainChannelId;
            private String mainChannelInviteLink;
        }
    }

    /**
     * The type Applications.
     */
    @Data
    public static class Applications {
        /**
         * The Project team application.
         */
        ProjectTeamApplication projectTeamApplication = new ProjectTeamApplication();
        /**
         * The Media application.
         */
        MediaApplication mediaApplication = new MediaApplication();
        /**
         * The Pr application.
         */
        PRApplication prApplication = new PRApplication();
        /**
         * The Designer application.
         */
        DesignerApplication designerApplication = new DesignerApplication();

        private String newApplicationNotificationChannelId;

        /**
         * The type Project team application.
         */
        @Data
        public static class ProjectTeamApplication {
            private String newApplicationNotificationThreadId;
        }

        /**
         * The type Media application.
         */
        @Data
        public static class MediaApplication {
            private String newApplicationNotificationThreadId;
        }

        /**
         * The type Pr application.
         */
        @Data
        public static class PRApplication {
            private String newApplicationNotificationThreadId;
        }

        /**
         * The type Designer application.
         */
        @Data
        public static class DesignerApplication {
            private String newApplicationNotificationThreadId;
        }
    }
}
