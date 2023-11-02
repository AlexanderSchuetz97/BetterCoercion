package eu.aschuetz.bettercoercion.coercion;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EnumBiCoercion<T extends Enum<T>> extends UserdataBiCoercion<T> {

    private final Map<String, T> values;
    private final boolean caseInsensitive;

    public EnumBiCoercion(LuaCoercion coercion, Class<T> target) {
        super(coercion, target);
        boolean isCaseInsensitive = true;
        Map<String, T> realCase = new HashMap<>();
        Map<String, T> lowerCase = new HashMap<>();
        for (T e : target.getEnumConstants()) {
            String realName = e.name();
            realCase.put(realName, e);
            if (!isCaseInsensitive) {
                continue;
            }
            String lowerName = realName.toLowerCase();
            if (lowerCase.containsKey(lowerName)) {
                isCaseInsensitive = false;
                continue;
            }
            lowerCase.put(lowerName, e);
        }
        caseInsensitive = isCaseInsensitive;
        values = caseInsensitive ? lowerCase : realCase;
    }

    @Override
    public int score(LuaValue value) {
        if (!value.isstring()) {
            return super.score(value);
        }

        String jstring = value.tojstring();

        if (caseInsensitive) {
            jstring = jstring.toLowerCase();
        }

        if (values.containsKey(jstring)) {
            return COERCION_CONVERSION;
        }

        return super.score(value);
    }

    @Override
    public T coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (!value.isstring()) {
            return super.coerce2J(value, types);
        }

        String jstring = value.tojstring();

        if (caseInsensitive) {
            jstring = jstring.toLowerCase();
        }

        T element = values.get(jstring);
        if (element != null) {
            return element;
        }

        return super.coerce2J(value, types);
    }
}
