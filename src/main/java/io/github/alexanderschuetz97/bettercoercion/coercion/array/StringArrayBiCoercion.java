package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.LuaStringArray;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class StringArrayBiCoercion implements BiCoercion<String[]> {

    protected final LuaCoercion coercion;

    public StringArrayBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(String[] value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : new LuaStringArray(coercion, value);
    }

    @Override
    public Varargs coerce2V(String[] value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(String[].class)) {
            return COERCION_INSTANCE;
        }

        if (value.istable()) {
            return COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public String[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(String[].class);
        if (o != null) {
            return (String[]) o;
        }

        LuaTable table = value.checktable();
        return Util.tableToArray(new String[table.length()], coercion.getL2JCoercion(String.class), table);
    }

    @Override
    public Class<String[]> getCoercedClass() {
        return String[].class;
    }
}
