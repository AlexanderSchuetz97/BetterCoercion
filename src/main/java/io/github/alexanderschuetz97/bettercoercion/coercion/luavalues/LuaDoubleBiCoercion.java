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
package io.github.alexanderschuetz97.bettercoercion.coercion.luavalues;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

public class LuaDoubleBiCoercion implements BiCoercion<LuaDouble> {

    /**
     * By default LuaValue.valueOf checks if the passed double can be represented as an int without rounding.
     * If so it will return LuaInteger instead of LuaDouble. LuaDoubles constructor is private so we have to
     * use reflection to actually be guaranteed to get a instance of LuaDouble.
     */
    private static final Constructor<LuaDouble> DOUBLE_CONSTRUCTOR;

    static {
        Constructor<LuaDouble> constr;

        try {
            constr = LuaDouble.class.getDeclaredConstructor(double.class);
            constr.setAccessible(true);
        } catch (Exception e) {
            //Constructor not available, functinality will be very limited.
            constr = null;
        }

        DOUBLE_CONSTRUCTOR = constr;
    }

    @Override
    public LuaValue coerce2L(LuaDouble value, Map<Class<?>, Type[]> genericTypes) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value;

    }

    @Override
    public Varargs coerce2V(LuaDouble value, Map<Class<?>, Type[]> genericTypes) {
        if (value == null) {
            return LuaValue.NONE;
        }
        return value;
    }

    @Override
    public int score(LuaValue value) {
        if (value instanceof LuaDouble) {
            return COERCION_INSTANCE;
        }

        if (DOUBLE_CONSTRUCTOR != null) {
            if (value.isnumber()) {
                return COERCION_CONVERSION;
            }

            if (value.isuserdata(Number.class)) {
                return COERCION_CONVERSION;
            }
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public LuaDouble coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value instanceof LuaDouble)  {
            return (LuaDouble) value;
        }

        if (DOUBLE_CONSTRUCTOR != null) {
            if (value.isnumber()) {
                try {
                    return DOUBLE_CONSTRUCTOR.newInstance(value.checkdouble());
                } catch (Exception e) {
                    throw Util.wrapException(e);
                }
            }

            if (value.isuserdata(Number.class)) {
                try {
                    return DOUBLE_CONSTRUCTOR.newInstance(((Number)value.checkuserdata()).doubleValue());
                } catch (Exception e) {
                    throw Util.wrapException(e);
                }
            }
        }

        throw new LuaError("attempt to convert "+ value.typename() + " to LuaDouble");
    }

    @Override
    public Class<LuaDouble> getCoercedClass() {
        return LuaDouble.class;
    }
}
