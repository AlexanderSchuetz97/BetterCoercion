package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.ScoreHelper;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.Map;

public class NumberL2jCoercion implements LuaToJavaCoercion<Number>  {

    private static final Integer ZERO = 0;

    @Override
    public int score(LuaValue value) {
        if (value.isuserdata(Number.class)) {
            return COERCION_INSTANCE;
        }

        if (value.isnumber()) {
            if (value.type() == LuaValue.TSTRING) {
                return COERCION_CONVERSION;
            }
            return COERCION_INSTANCE;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        return ScoreHelper.chain(score(value), chain);
    }

    @Override
    public Number coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value.isuserdata(Number.class)) {
            return (Number) value.touserdata(Number.class);
        }

        if (value.isnil()) {
            return ZERO;
        }

        LuaValue number = value.tonumber();
        if (number.isnil()) {
            throw new LuaError("bad argument: number expected, got " + value.typename());
        }

        if (number.isint()) {
            return Integer.valueOf(number.toint());
        }

        if (number.islong()) {
            return Long.valueOf(number.tolong());
        }

        return Double.valueOf(number.todouble());
    }

    @Override
    public Class<Number> getCoercedClass() {
        return Number.class;
    }
}
