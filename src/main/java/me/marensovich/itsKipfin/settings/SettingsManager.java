package me.marensovich.itsKipfin.settings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import me.marensovich.itsKipfin.settings.dto.BotSettings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


/**
 * The type Settings manager.
 */
@Component
public class SettingsManager {

    private static final String FILE_NAME = "bot_settings.json";
    private static final String BACKUP_SUFFIX = ".bak";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @Getter
    private static BotSettings settings;

    /**
     * Instantiates a new Settings manager.
     *
     */
    public SettingsManager() {
        loadOrCreateSettings();
    }

    /**
     * Сохранение настроек в файл
     */
    public void saveSettings() {
        try {
            // Основное место сохранения — ./data/
            File dataDir = new File("./data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            File outputFile = new File(dataDir, FILE_NAME);
            objectMapper.writeValue(outputFile, settings);
            System.out.println("[SettingsManager] ✅ Настройки сохранены в: " + outputFile.getAbsolutePath());

            // В режиме разработки также сохраняем в ресурсы
            if (!isRunningFromJar()) {
                saveToResourcesForDevelopment();
            }

        } catch (IOException e) {
            System.err.println("[SettingsManager] ❌ Ошибка при сохранении настроек: " + e.getMessage());
        }
    }

    /**
     * Загружает или создаёт настройки
     */
    private void loadOrCreateSettings() {
        BotSettings defaultSettings = createDefaultSettings();

        // 1️⃣ — Сначала пробуем из ./data/
        File externalFile = new File("./data/" + FILE_NAME);
        if (externalFile.exists()) {
            try {
                settings = objectMapper.readValue(externalFile, BotSettings.class);
                System.out.println("[SettingsManager] ✅ Настройки загружены из ./data/");
            } catch (IOException e) {
                createBackup(externalFile);
                System.err.println("[SettingsManager] ⚠ Повреждённый JSON, создаю новый...");
                settings = defaultSettings;
            }
        } else {
            // 2️⃣ — Если нет, пробуем из classpath
            try {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource resource = resolver.getResource("classpath:" + FILE_NAME);

                if (resource.exists()) {
                    try (InputStream is = resource.getInputStream()) {
                        settings = objectMapper.readValue(is, BotSettings.class);
                        System.out.println("[SettingsManager] ✅ Настройки загружены из ресурсов");
                    }
                } else {
                    System.out.println("[SettingsManager] ⚠ Файл настроек не найден, создаю новый...");
                    settings = defaultSettings;
                }
            } catch (Exception e) {
                System.err.println("[SettingsManager] ❌ Ошибка при загрузке настроек: " + e.getMessage());
                settings = defaultSettings;
            }
        }
        saveSettings();
    }

    /**
     * Сохраняет копию настроек в src/main/resources (режим разработки)
     */
    private void saveToResourcesForDevelopment() {
        try {
            File resourcesDir = new File("src/main/resources");
            if (resourcesDir.exists() && resourcesDir.isDirectory()) {
                File outputFile = new File(resourcesDir, FILE_NAME);
                objectMapper.writeValue(outputFile, settings);
                System.out.println("[SettingsManager] 💾 Также сохранено в ресурсы: " + outputFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("[SettingsManager] ⚠ Не удалось сохранить в ресурсы (нормально в JAR): " + e.getMessage());
        }
    }

    /**
     * Проверяет, запущено ли приложение из JAR
     */
    private boolean isRunningFromJar() {
        try {
            String protocol = this.getClass().getResource("").getProtocol();
            return "jar".equals(protocol);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Создание резервной копии битого файла
     */
    private void createBackup(File file) {
        File backup = new File(file.getAbsolutePath() + BACKUP_SUFFIX);
        try {
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[SettingsManager] 🧩 Создан бэкап: " + backup.getName());
        } catch (IOException ex) {
            System.err.println("[SettingsManager] ❌ Не удалось создать бэкап: " + ex.getMessage());
        }
    }

    /**
     * Рекурсивно объединяет defaultNode и targetNode.
     * Если поле отсутствует или имеет некорректный тип — подставляет дефолтное значение.
     */
    private JsonNode mergeAndFixJson(JsonNode defaultNode, JsonNode targetNode) {
        if (defaultNode instanceof ObjectNode defaultObj && targetNode instanceof ObjectNode targetObj) {
            defaultObj.fieldNames().forEachRemaining(field -> {
                JsonNode defaultValue = defaultObj.get(field);
                JsonNode targetValue = targetObj.get(field);

                if (targetValue == null || targetValue.isNull()) {
                    System.out.println("[SettingsManager] Добавляю дефолтное поле: " + field);
                    targetObj.set(field, defaultValue);
                } else if (defaultValue.isObject()) {
                    targetObj.set(field, mergeAndFixJson(defaultValue, targetValue)); // <── исправление
                } else if (!defaultValue.getNodeType().equals(targetValue.getNodeType())) {
                    System.out.println("[SettingsManager] Поле '" + field + "' имело неверный тип. Ставлю дефолтное значение.");
                    targetObj.set(field, defaultValue);
                }
            });
        }
        return targetNode;
    }

    /**
     * Создаёт объект с дефолтными настройками
     */
    private BotSettings createDefaultSettings() {
        return new BotSettings();
    }

}
