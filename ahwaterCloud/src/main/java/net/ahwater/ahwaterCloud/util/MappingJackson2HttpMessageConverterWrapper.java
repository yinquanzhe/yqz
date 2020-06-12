package net.ahwater.ahwaterCloud.util;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.sun.istack.internal.Nullable;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Reeye on 2017/11/7.
 * Nothing is true but improving yourself.
 * 使action返回的json封装一层 {@link RespResult}
 */
public class MappingJackson2HttpMessageConverterWrapper extends MappingJackson2HttpMessageConverter {

    private PrettyPrinter ssePrettyPrinter;

    public MappingJackson2HttpMessageConverterWrapper() {
        super();
    }

    public MappingJackson2HttpMessageConverterWrapper(ObjectMapper objectMapper) {
        super(objectMapper);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);
        JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        if (!(object instanceof RespResult)) {
            object = RespResult.correct(object);
        }
        try {
            writePrefix(generator, object);

            Class<?> serializationView = null;
            FilterProvider filters = null;
            Object value = object;
            JavaType javaType = null;
            if (object instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue) object;
                value = container.getValue();

                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }
            if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
                javaType = getJavaType(type, null);
            }
            ObjectWriter objectWriter;
            if (serializationView != null) {
                objectWriter = this.objectMapper.writerWithView(serializationView);
            }
            else if (filters != null) {
                objectWriter = this.objectMapper.writer(filters);
            }
            else {
                objectWriter = this.objectMapper.writer();
            }
            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }
            SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
                    config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }
            objectWriter.writeValue(generator, value);

            writeSuffix(generator, object);
            generator.flush();

        }
        catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
        }
    }

}
