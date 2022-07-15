package io.github.alexanderschuetz97.bettercoercion.coercion.luavalues;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class LuaTableBiCoercion implements BiCoercion<LuaTable> {
    @Override
    public LuaValue coerce2L(LuaTable value, Map<Class<?>, Type[]> genericTypes) {
        if (value == null) {
            return LuaValue.NIL;
        }

        return value;
    }

    @Override
    public Varargs coerce2V(LuaTable value, Map<Class<?>, Type[]> genericTypes) {
        if (value == null) {
            return LuaValue.NONE;
        }

        return value;
    }

    @Override
    public int score(LuaValue value) {
        return value.istable() ? COERCION_INSTANCE : COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public LuaTable coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        return value.checktable();
    }

    @Override
    public Class<LuaTable> getCoercedClass() {
        return LuaTable.class;
    }

}
