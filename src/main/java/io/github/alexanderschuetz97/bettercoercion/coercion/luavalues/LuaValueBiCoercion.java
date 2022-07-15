package io.github.alexanderschuetz97.bettercoercion.coercion.luavalues;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class LuaValueBiCoercion<T extends LuaValue> implements BiCoercion<T> {

    protected final Class<T> target;

    public LuaValueBiCoercion(Class<T> target) {
        this.target = target;
    }


    @Override
    public LuaValue coerce2L(T value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value;
    }

    @Override
    public Varargs coerce2V(T value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (target.isInstance(value)) {
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
    public T coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (!target.isInstance(value)) {
            throw new LuaError("expected lua type " + target.getName() + " got " + value.typename());
        }
        return target.cast(value);
    }



    @Override
    public Class<T> getCoercedClass() {
        return target;
    }
}
