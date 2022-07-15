package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.GenericArray;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

public class GenericArrayBiCoercion<T> implements BiCoercion<T> {

    protected final Class<T> target;

    protected final LuaCoercion coercion;

    public GenericArrayBiCoercion(LuaCoercion coercion, Class<T> aClass) {
        this.coercion = coercion;
        this.target = aClass;
    }

    @Override
    public LuaValue coerce2L(T value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : new GenericArray(coercion, value);
    }

    @Override
    public Varargs coerce2V(T value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }


    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(target)) {
            return COERCION_INSTANCE;
        }

        if (value.istable()) {
            return COERCION_CONVERSION_N;
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
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(target);
        if (o != null) {
            return (T) o;
        }

        LuaTable table = value.checktable();
        return (T) Util.tableToArray(Array.newInstance(target.getComponentType(), table.length()), coercion.getL2JCoercion(target.getComponentType()), table);
    }

    @Override
    public Class<T> getCoercedClass() {
        return target;
    }
}
