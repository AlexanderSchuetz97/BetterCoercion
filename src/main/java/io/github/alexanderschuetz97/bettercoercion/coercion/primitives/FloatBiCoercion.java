package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class FloatBiCoercion implements BiCoercion<Float> {

    private final boolean primitive;
    private final Class<Float> clazz;
    private final Float nullValue;

    public FloatBiCoercion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Float.valueOf(0) : null;
        clazz = isPrimitive ? float.class : Float.class;
    }

    @Override
    public LuaValue coerce2L(Float value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Float value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value instanceof LuaString && value.isnumber()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isnumber()) {
            return COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Float coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Float.valueOf((float) value.checkdouble());
    }

    @Override
    public Class<Float> getCoercedClass() {
        return clazz;
    }

}
