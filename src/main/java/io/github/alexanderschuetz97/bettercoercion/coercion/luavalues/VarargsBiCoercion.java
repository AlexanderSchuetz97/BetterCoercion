package io.github.alexanderschuetz97.bettercoercion.coercion.luavalues;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class VarargsBiCoercion implements BiCoercion<Varargs> {

    @Override
    public LuaValue coerce2L(Varargs value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value.arg1();
    }

    @Override
    public Varargs coerce2V(Varargs value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NONE;
        }
        return value;
    }

    @Override
    public int score(LuaValue value) {
        return COERCION_INSTANCE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Varargs coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value;
    }

    @Override
    public Class<Varargs> getCoercedClass() {
        return Varargs.class;
    }
}
