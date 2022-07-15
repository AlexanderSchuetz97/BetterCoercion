package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class LongBiConversion implements BiCoercion<Long> {

    private final boolean primitive;
    private final Class<Long> clazz;
    private final Long nullValue;

    public LongBiConversion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Long.valueOf(0) : null;
        clazz = isPrimitive ? long.class : Long.class;
    }

    @Override
    public LuaValue coerce2L(Long value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Long value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value instanceof LuaString) {
            LuaValue num = value.tonumber();
            if (num.isnil()) {
                return COERCION_IMPOSSIBLE;
            }

            return num.islong() ? COERCION_CONVERSION : COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value.isnumber()) {
            return value.islong() ? COERCION_INSTANCE : COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Long coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Long.valueOf(value.checklong());
    }

    @Override
    public Class<Long> getCoercedClass() {
        return clazz;
    }

}
