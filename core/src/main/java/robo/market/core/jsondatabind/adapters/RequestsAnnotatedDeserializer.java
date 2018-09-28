package robo.market.core.jsondatabind.adapters;

import com.google.gson.*;
import robo.market.core.jsondatabind.RobomarketRequest;
import robo.market.core.jsondatabind.annotations.JsonRequired;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Gets Gson to fail if Json would not contain required fields in models.
 *
 * @param <T> Model to parse to.
 */
public class RequestsAnnotatedDeserializer<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        T pojo = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX").create().
                fromJson(jsonElement, type);

        Field[] fields = pojo.getClass().getDeclaredFields();
        checkRequaredAnnotation(pojo, fields);
        //Checks all request except CancellationRequest
        if (pojo instanceof RobomarketRequest) {
            checkRequaredAnnotation(pojo, RobomarketRequest.class.getDeclaredFields());
        }
        return pojo;
    }

    private void checkSuperClasses(Object pojo) throws JsonParseException {
        Class<?> superClass = pojo.getClass();
        while ((superClass = superClass.getSuperclass()) != null) {
            checkRequaredAnnotation(pojo, superClass.getDeclaredFields());
        }
    }

    private void checkRequaredAnnotation(Object pojo, Field[] fields) throws JsonParseException {
        // Checking nested list items
        if (pojo instanceof List) {
            final List pojoList = (List) pojo;
            for (final Object pojoListPojo : pojoList) {
                checkRequaredAnnotation(pojoListPojo, pojoListPojo.getClass().getDeclaredFields());
                checkSuperClasses(pojoListPojo);
            }
        }
        for (Field field : fields) {
            // If some field has required annotation
            if (Objects.nonNull(field.getAnnotation(JsonRequired.class))) {
                try {
                    // Trying to read this field's value and check that it truly has value
                    field.setAccessible(true);
                    Object fieldObject = field.get(pojo);
                    if (Objects.isNull(fieldObject)) {
                        // Required value is null - throwing exception
                        throw new JsonParseException("Missing field in JSON: " + field.getName());
                    } else {
                        checkRequaredAnnotation(fieldObject, fieldObject.getClass().getDeclaredFields());
                        checkSuperClasses(fieldObject);
                    }
                    // Exceptions while reflection
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new JsonParseException(e);
                }
            }
        }
    }
}
