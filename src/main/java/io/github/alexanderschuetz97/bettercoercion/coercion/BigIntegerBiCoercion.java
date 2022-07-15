package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.number.BigIntegerUserdata;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Map;

public class BigIntegerBiCoercion implements BiCoercion<BigInteger> {

    private final LuaCoercion coercion;

    public BigIntegerBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(BigInteger value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return new BigIntegerUserdata(coercion, value);
    }

    @Override
    public Varargs coerce2V(BigInteger value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(BigDecimal.class)) {
            return COERCION_INSTANCE;
        }

        if (value.isuserdata(Number.class)) {
            return COERCION_CONVERSION;
        }

        return value.isnumber() ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public BigInteger coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        if (value.isuserdata(BigDecimal.class)) {
            return (BigInteger) value.touserdata();
        }

        if (value.isuserdata(Number.class)) {
            return toBigInteger((Number) value.touserdata());
        }

        if (value.isinttype()) {
            return BigInteger.valueOf(value.checkint());
        }

        LuaValue num = value.tonumber();
        if (num.isnil()) {
            try {
                return new BigInteger(num.checkjstring());
            } catch (NumberFormatException exc) {
                throw new LuaError(exc);
            }
        }

        if (num.islong()) {
            return BigInteger.valueOf(num.checklong());
        }

        return toBigInteger(num.checkdouble());
    }



    @Override
    public Class<BigInteger> getCoercedClass() {
        return BigInteger.class;
    }

    protected BigInteger toBigInteger(double number) {
        try {
            return new BigDecimal(number).setScale(0, RoundingMode.HALF_EVEN).toBigInteger();
        } catch (NumberFormatException exc) {
            if (Double.isInfinite(number)) {
                throw new LuaError("attempt to convert infinity to big integer");
            }

            if (Double.isNaN(number)) {
                throw new LuaError("attempt to convert NaN to big integer");
            }

            throw new LuaError(exc);
        }
    }

    protected BigDecimal toBigInteger(int number) {
        return new BigDecimal(number);
    }

    protected BigInteger toBigInteger(Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        }

        if (number instanceof BigInteger) {
            return (BigInteger) number;
        }

        if (number instanceof Integer || number instanceof Long) {
            return new BigInteger(number.toString());
        }

        return toBigInteger(number.doubleValue());
    }
}
