package io.github.alexanderschuetz97.bettercoercion.coercion.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.collection.LuaListUserdata;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ListBiCoercion implements BiCoercion<List> {

    private final LuaCoercion coercion;

    public ListBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(List value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }


        return new LuaListUserdata(coercion, value, Util.firstType(types.get(Iterable.class)));
    }

    @Override
    public Varargs coerce2V(List value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {

        if (value.isuserdata(List.class)) {
            return COERCION_INSTANCE;
        }

        if (value.isuserdata(Iterable.class)) {
            return COERCION_CONVERSION_N;
        }

        if (value.istable()) {
            return COERCION_CONVERSION_N;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score + chain;
    }

    @Override
    public List coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isuserdata(List.class)) {
            return (List) value.checkuserdata(List.class);
        }

        if (value.isuserdata(Iterable.class)) {
            return Util.iterableToList((Iterable) value.checkuserdata(Iterable.class));
        }

        if (value.istable()) {
            return Util.tableToCollection(coercion.getL2JCoercion(Util.firstTypeClass(types.get(Iterable.class))), value.checktable());
        }

        if (value.isnil()) {
            return null;
        }

        throw new LuaError("cant coerce " + value.typename() + " to Collection");
    }

    @Override
    public Class<List> getCoercedClass() {
        return List.class;
    }


}
