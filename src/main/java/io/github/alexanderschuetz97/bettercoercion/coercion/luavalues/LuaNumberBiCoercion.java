package io.github.alexanderschuetz97.bettercoercion.coercion.luavalues;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class LuaNumberBiCoercion implements BiCoercion<LuaNumber> {
    @Override
    public LuaValue coerce2L(LuaNumber value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value;

    }

    @Override
    public Varargs coerce2V(LuaNumber value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        switch (value.type()) {
            case LuaValue.TINT:
            case LuaValue.TNUMBER:
                return COERCION_INSTANCE;
            case LuaValue.TUSERDATA:
                if (value.isuserdata(Number.class)) {
                    return COERCION_CONVERSION;
                }
            default:
                return value.isnumber() ? COERCION_CONVERSION : COERCION_IMPOSSIBLE;
        }
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public LuaNumber coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        switch (value.type()) {
            case LuaValue.TUSERDATA:
                if (value.isuserdata(Number.class)) {
                    return LuaValue.valueOf(((Number) value.checkuserdata(Number.class)).doubleValue());
                }
            default:
                return value.checknumber();
        }
    }

    @Override
    public Class<LuaNumber> getCoercedClass() {
        return LuaNumber.class;
    }

}
