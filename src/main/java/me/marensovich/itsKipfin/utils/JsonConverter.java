package me.marensovich.itsKipfin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import me.marensovich.itsKipfin.bot.manager.button.buttons.RegisterITCButton;

/**
 * The type Json converter.
 */
@Converter(autoApply = false)
public class JsonConverter implements AttributeConverter<RegisterITCButton.ProjectTeamHandler.UserApplicationData, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(RegisterITCButton.ProjectTeamHandler.UserApplicationData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка сериализации JSON", e);
        }
    }

    @Override
    public RegisterITCButton.ProjectTeamHandler.UserApplicationData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, RegisterITCButton.ProjectTeamHandler.UserApplicationData.class);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка десериализации JSON", e);
        }
    }
}

