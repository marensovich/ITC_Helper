package me.marensovich.itsKipfin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;

/**
 * Утилита для конвертации JSON в DTO
 * @author marensovich
 * @since 0.0.1
 * @version 0.0.1
 */
@Converter(autoApply = false)
public class JsonConverter implements AttributeConverter<RegisterITCButton.ProjectTeamHandler.UserProjectTeamApplicationData, String> {

    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(RegisterITCButton.ProjectTeamHandler.UserProjectTeamApplicationData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации JSON", e);
        }
    }

    @Override
    public RegisterITCButton.ProjectTeamHandler.UserProjectTeamApplicationData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, RegisterITCButton.ProjectTeamHandler.UserProjectTeamApplicationData.class);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка десериализации JSON", e);
        }
    }
}

