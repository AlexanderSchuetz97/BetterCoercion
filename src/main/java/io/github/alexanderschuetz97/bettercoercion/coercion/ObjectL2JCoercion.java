package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.Map;

public class ObjectL2JCoercion implements LuaToJavaCoercion<Object> {

    private final LuaCoercion coercion;

    public ObjectL2JCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return LuaToJavaCoercion.COERCION_CONVERSION;
        }
        if (value.isuserdata()) {
            return LuaToJavaCoercion.COERCION_INSTANCE;
        }

        return LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Object coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return coercion.coerce(value);
    }

    @Override
    public Class<Object> getCoercedClass() {
        return Object.class;
    }
}
