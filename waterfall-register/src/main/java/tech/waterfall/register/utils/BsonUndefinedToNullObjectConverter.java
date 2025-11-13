package tech.waterfall.register.utils;

import org.bson.BsonUndefined;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class BsonUndefinedToNullObjectConverter implements ConverterFactory<BsonUndefined, Object> {
    public <T> Converter<BsonUndefined, T> getConverter(Class<T> targetType) {
        return (o) -> null;
    }
}
