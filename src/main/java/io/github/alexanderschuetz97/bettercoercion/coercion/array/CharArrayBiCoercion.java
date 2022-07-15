package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;


public class CharArrayBiCoercion implements BiCoercion<char[]> {

    @Override
    public LuaValue coerce2L(char[] value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }

        return LuaString.valueOf(value);
    }

    @Override
    public Varargs coerce2V(char[] value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value instanceof LuaString) {
            return COERCION_INSTANCE;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(char[].class)) {
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
    public char[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isuserdata(char[].class)) {
            return (char[]) value.checkuserdata(char[].class);
        }

        if (value.isnil()) {
            return null;
        }

        return value.checkjstring().toCharArray();
    }

    @Override
    public Class<char[]> getCoercedClass() {
        return char[].class;
    }
}
