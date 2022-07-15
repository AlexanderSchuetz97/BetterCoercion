package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class VoidBiCoercion implements BiCoercion<Object> {
    @Override
    public LuaValue coerce2L(Object value, Map<Class<?>, Type[]> types) {
        return LuaValue.NIL;
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_INSTANCE;
        }
        return COERCION_CONVERSION;

    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Object coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return null;
    }

    @Override
    public Varargs coerce2V(Object value, Map<Class<?>, Type[]> types) {
        return LuaValue.NONE;
    }

    @Override
    public Class getCoercedClass() {
        return Void.class;
    }
}
