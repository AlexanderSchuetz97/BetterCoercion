package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.LuaDoubleArray;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class DoubleArrayBiCoercion implements BiCoercion<double[]> {
    protected final LuaCoercion coercion;

    public DoubleArrayBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(double[] value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL :  new LuaDoubleArray(coercion, value);
    }

    @Override
    public Varargs coerce2V(double[] value, Map<Class<?>, Type[]> types) {
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
    public double[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(double[].class);
        if (o != null) {
            return (double[]) o;
        }

        LuaTable table = value.checktable();
        LuaToJavaCoercion<Long> child = coercion.getL2JCoercion(long.class);
        int len = table.length();
        long[] array = new long[len];
        for (int i = 0; i < len; i++) {
            array[i] = child.coerce2J(table.get(i+1), Util.NO_TYPE_MAP);
        }

        return Util.tableToArray(new double[len], coercion.getL2JCoercion(double.class), table);
    }

    @Override
    public Class<double[]> getCoercedClass() {
        return double[].class;
    }
}
