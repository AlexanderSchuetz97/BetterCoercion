package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class CharBiCoercion implements BiCoercion<Character> {

    private final boolean primitive;
    private final Class<Character> clazz;
    private final Character nullValue;

    public CharBiCoercion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Character.valueOf('\0') : null;
        clazz = isPrimitive ? char.class : Character.class;
    }

    @Override
    public LuaValue coerce2L(Character value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Character value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value instanceof LuaString) {
            LuaValue num = value.tonumber();
            if (num.isnil()) {
                return COERCION_IMPOSSIBLE;
            }

            int val = value.checkint();
            return val >= Character.MIN_VALUE && val <= Character.MAX_VALUE ? COERCION_CONVERSION : COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isnumber()) {
            int val = value.checkint();
            return val >= Character.MIN_VALUE && val <= Character.MAX_VALUE ? COERCION_INSTANCE : COERCION_CONVERSION_LOSS_OF_PRECISION;
        }



        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Character coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Character.valueOf((char) value.checkint());
    }

    @Override
    public Class<Character> getCoercedClass() {
        return clazz;
    }

}
