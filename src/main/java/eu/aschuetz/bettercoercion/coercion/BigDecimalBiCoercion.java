//
// Copyright Alexander Schütz, 2022
//
// This file is part of BetterCoercion.
//
// BetterCoercion is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BetterCoercion is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// A copy of the GNU Lesser General Public License should be provided
// in the COPYING & COPYING.LESSER files in top level directory of BetterCoercion.
// If not, see <https://www.gnu.org/licenses/>.
//
package eu.aschuetz.bettercoercion.coercion;

import eu.aschuetz.bettercoercion.api.BiCoercion;
import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.userdata.number.BigDecimalUserdata;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class BigDecimalBiCoercion implements BiCoercion<BigDecimal> {

    private final LuaCoercion coercion;

    public BigDecimalBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(BigDecimal value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return new BigDecimalUserdata(coercion, value);
    }

    @Override
    public Varargs coerce2V(BigDecimal value, Map<Class<?>, Type[]> types) {
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
    public BigDecimal coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        if (value.isuserdata(BigDecimal.class)) {
            return (BigDecimal) value.touserdata();
        }

        if (value.isuserdata(Number.class)) {
            return toBigDecimal((Number) value.touserdata());
        }

        if (value.isinttype()) {
            return toBigDecimal(value.checkint());
        }

        LuaValue num = value.tonumber();
        if (num.isnil()) {
            try {
                return new BigDecimal(num.checkjstring());
            } catch (NumberFormatException exc) {
                throw new LuaError(exc);
            }
        }

        if (num.islong()) {
            return new BigDecimal(num.checklong());
        }

        return toBigDecimal(num.checkdouble());
    }



    @Override
    public Class<BigDecimal> getCoercedClass() {
        return BigDecimal.class;
    }

    protected BigDecimal toBigDecimal(double number) {
        try {
            return new BigDecimal(String.valueOf(number));
        } catch (NumberFormatException exc) {
            if (Double.isInfinite(number)) {
                throw new LuaError("attempt to convert infinity to big decimal");
            }

            if (Double.isNaN(number)) {
                throw new LuaError("attempt to convert NaN to big decimal");
            }

            throw new LuaError(exc);
        }
    }

    protected BigDecimal toBigDecimal(int number) {
        return new BigDecimal(number);
    }

    protected BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }

        if (number instanceof BigInteger) {
            return new BigDecimal((BigInteger)number);
        }

        if (number instanceof Integer) {
            return toBigDecimal(number);
        }


        return toBigDecimal(number.doubleValue());
    }

}
