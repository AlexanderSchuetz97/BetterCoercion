package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class ByteArrayBiCoercion implements BiCoercion<byte[]> {



    @Override
    public LuaValue coerce2L(byte[] value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaString.valueUsing(Arrays.copyOf(value, value.length));
    }

    @Override
    public Varargs coerce2V(byte[] value, Map<Class<?>, Type[]> types) {
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

        if (value.isuserdata(byte[].class)) {
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
    public byte[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object arr = value.touserdata(byte[].class);
        if (arr != null) {
            return (byte[]) arr;
        }

        LuaString str = value.checkstring();
        byte[] copy = new byte[str.m_length];
        System.arraycopy(str.m_bytes, str.m_offset, copy, 0, str.m_length);
        return copy;
    }

    @Override
    public Class<byte[]> getCoercedClass() {
        return byte[].class;
    }
}
