package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.number.BigDecimalUserdata;
import io.github.alexanderschuetz97.bettercoercion.userdata.number.BigIntegerUserdata;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Map;

public class LongBiConversion implements BiCoercion<Long> {

    private final boolean primitive;
    private final Class<Long> clazz;
    private final Long nullValue;

    private final LuaCoercion coercion;

    public LongBiConversion(LuaCoercion coercion, boolean isPrimitive) {
        this.coercion = coercion;
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? Long.valueOf(0) : null;
        clazz = isPrimitive ? long.class : Long.class;
    }

    @Override
    public LuaValue coerce2L(Long value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : valueOf(value);
    }

    protected LuaValue valueOf(long l) {
        //TODO implement a 64 bit LuaInteger?
        //This here is at least better than nothing but if you fetch 9007199254740991L and add 1 it wont convert it to a BigInteger and you will end up loosing precision

        //Luaj squashes long to a double.
        //Double can represent every long up until 2^53 since 53 bits of the 64 bit double are used for the whole number part (10 for the exponent and 1 for sign).
        if (l > 9007199254740992L || l < -9007199254740992L) {
            return new BigIntegerUserdata(coercion, BigInteger.valueOf(l));
        }

        //Double or 32-bit integer is sufficient precision
        return LuaInteger.valueOf(l);
    }

    @Override
    public Varargs coerce2V(Long value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value instanceof LuaString) {
            LuaValue num = value.tonumber();
            if (num.isnil()) {
                return COERCION_IMPOSSIBLE;
            }

            return num.islong() ? COERCION_CONVERSION : COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value.isnumber()) {
            return value.islong() ? COERCION_INSTANCE : COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Long coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? nullValue : Long.valueOf(value.checklong());
    }

    @Override
    public Class<Long> getCoercedClass() {
        return clazz;
    }

}
