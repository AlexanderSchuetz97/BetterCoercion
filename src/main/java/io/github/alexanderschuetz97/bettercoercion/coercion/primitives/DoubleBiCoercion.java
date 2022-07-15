package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class DoubleBiCoercion implements BiCoercion<Double> {

    private final boolean primitive;
    private final Class<Double> clazz;
    private final Double nullValue;

    public DoubleBiCoercion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Double.valueOf(0) : null;
        clazz = isPrimitive ? double.class : Double.class;
    }

    @Override
    public LuaValue coerce2L(Double value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Double value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value instanceof LuaString && value.isnumber()) {
            return COERCION_CONVERSION;
        }

        if (value.isnumber()) {
            return COERCION_INSTANCE;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Double coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Double.valueOf(value.checkdouble());
    }

    @Override
    public Class<Double> getCoercedClass() {
        return clazz;
    }

}
