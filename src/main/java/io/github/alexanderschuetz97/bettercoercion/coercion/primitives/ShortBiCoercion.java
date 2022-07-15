package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class ShortBiCoercion implements BiCoercion<Short> {

    private final boolean primitive;
    private final Class<Short> clazz;
    private final Short nullValue;

    public ShortBiCoercion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Short.valueOf((short)0) : null;
        clazz = isPrimitive ? short.class : Short.class;
    }

    @Override
    public LuaValue coerce2L(Short value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Short value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value instanceof LuaInteger) {
            int l = value.checkint();
            if (l < Short.MIN_VALUE || l > Short.MAX_VALUE) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            return COERCION_INSTANCE;
        }

        if (value instanceof LuaString) {
            LuaValue num = value.tonumber();
            if (num.isnil()) {
                return COERCION_IMPOSSIBLE;
            }

            long i = num.tolong();

            if (num.isint() && i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
                return COERCION_CONVERSION;
            }

            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isnumber()) {
            if (!value.isint()) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            long l = value.checklong();
            if (l < Short.MIN_VALUE || l > Short.MAX_VALUE) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            return  COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Short coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Short.valueOf(value.checknumber().toshort());
    }

    @Override
    public Class<Short> getCoercedClass() {
        return clazz;
    }
}
