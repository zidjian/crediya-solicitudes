package co.com.crediya.shared.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface DataMapper {
    @Named("toLocalDate")
    default LocalDate toLocalDate(String iso) { return LocalDate.parse(iso); }

    @Named("fromLocalDate")
    default String fromLocalDate(LocalDate d) { return d == null ? null : d.toString(); }
}

