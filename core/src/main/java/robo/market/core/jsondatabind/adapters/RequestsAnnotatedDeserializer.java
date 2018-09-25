package robo.market.core.jsondatabind.adapters;

import com.google.gson.*;
import robo.market.core.jsondatabind.RobomarketRequest;
import robo.market.core.jsondatabind.annotations.JsonRequired;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class RequestsAnnotatedDeserializer<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        T pojo = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX").create().
                fromJson(jsonElement, type);

        Field[] fields = pojo.getClass().getDeclaredFields();
        checkRequaredAnnotation(pojo, fields);
        if (pojo instanceof RobomarketRequest) {
            Field[] fieldsParent = pojo.getClass().getSuperclass().getDeclaredFields();
            checkRequaredAnnotation(pojo, fieldsParent);
        }
        //checkSuperClasses(pojo);
        return pojo;
    }

    private void checkSuperClasses(Object pojo) throws JsonParseException {
        Class<?> superclass = pojo.getClass();
        while ((superclass = superclass.getSuperclass()) != null) {
            checkRequaredAnnotation(pojo, superclass.getDeclaredFields());
        }
    }

    private void checkRequaredAnnotation(Object pojo, Field[] fields) throws JsonParseException {
        if (pojo instanceof List) {
            final List pojoList = (List) pojo;
            for (final Object pojoListPojo : pojoList) {
                checkRequaredAnnotation(pojoListPojo, pojoListPojo.getClass().getDeclaredFields());
                checkSuperClasses(pojoListPojo);
            }
        }
        for (Field field : fields) {
            if (Objects.nonNull(field.getAnnotation(JsonRequired.class))) {
                try {
                    field.setAccessible(true);
                    Object fieldObject = field.get(pojo);
                    if (fieldObject == null) {
                        throw new JsonParseException("Missing field in JSON: " + field.getName());
                    } else {
                        checkRequaredAnnotation(fieldObject, fieldObject.getClass().getDeclaredFields());
                        checkSuperClasses(fieldObject);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new JsonParseException(e);
                }
            }
        }
    }
}
