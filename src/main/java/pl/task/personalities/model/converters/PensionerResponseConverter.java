package pl.task.personalities.model.converters;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import pl.task.personalities.model.Pensioner;
import pl.task.personalities.model.dto.response.PensionerResponse;

public class PensionerResponseConverter implements Converter<Pensioner, PensionerResponse> {
    @Override
    public PensionerResponse convert(MappingContext<Pensioner, PensionerResponse> mappingContext) {
        Pensioner pensioner = mappingContext.getSource();
        return PensionerResponse.builder()
                .id(pensioner.getId())
                .createdAt(pensioner.getCreatedAt())
                .updatedAt(pensioner.getUpdatedAt())
                .version(pensioner.getVersion())
                .firstName(pensioner.getFirstName())
                .lastName(pensioner.getLastName())
                .gender(pensioner.getGender())
                .height(pensioner.getHeight())
                .weight(pensioner.getWeight())
                .emailAddress(pensioner.getEmailAddress())
                .build();
    }
}
