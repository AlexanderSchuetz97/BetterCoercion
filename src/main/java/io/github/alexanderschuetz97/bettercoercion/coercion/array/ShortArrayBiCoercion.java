package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.LuaShortArray;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class ShortArrayBiCoercion implements BiCoercion<short[]> {
    protected final LuaCoercion coercion;

    public ShortArrayBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(short[] value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL :  new LuaShortArray(coercion, value);
    }

    @Override
    public Varargs coerce2V(short[] value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return LuaToJavaCoercion.COERCION_CONVERSION;
        }

        if (value.isuserdata(long[].class)) {
            return LuaToJavaCoercion.COERCION_INSTANCE;
        }

        if (value.istable()) {
            return LuaToJavaCoercion.COERCION_CONVERSION;
        }

        return LuaToJavaCoercion.COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public short[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(short[].class);
        if (o != null) {
            return (short[]) o;
        }

        LuaTable table = value.checktable();
        LuaToJavaCoercion<Long> child = coercion.getL2JCoercion(long.class);
        int len = table.length();
        long[] array = new long[len];
        for (int i = 0; i < len; i++) {
            array[i] = child.coerce2J(table.get(i+1), Util.NO_TYPE_MAP);
        }

        return Util.tableToArray(new short[len], coercion.getL2JCoercion(short.class), table);
    }

    @Override
    public Class<short[]> getCoercedClass() {
        return short[].class;
    }
}
