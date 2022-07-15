package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class IntBiCoercion implements BiCoercion<Integer> {

    private final boolean primitive;
    private final Class<Integer> clazz;
    private final Integer nullValue;

    public IntBiCoercion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Integer.valueOf(0) : null;
        clazz = isPrimitive ? int.class : Integer.class;
    }

    @Override
    public LuaValue coerce2L(Integer value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Integer value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value instanceof LuaInteger) {
            return COERCION_INSTANCE;
        }

        if (value instanceof LuaString) {
            LuaValue num = value.tonumber();
            if (num.isnil()) {
                return COERCION_IMPOSSIBLE;
            }

            if (num.isint()) {
                return COERCION_CONVERSION;
            }

            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isnumber()) {
            if (value.isint()) {
                return COERCION_CONVERSION;
            }

            long l = value.checklong();
            if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
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
    public Integer coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Integer.valueOf(value.checkint());
    }

    @Override
    public Class<Integer> getCoercedClass() {
        return clazz;
    }

}
