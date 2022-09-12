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
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class LuaIntegerBiCoercion implements BiCoercion<LuaInteger> {
    @Override
    public LuaValue coerce2L(LuaInteger value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value;

    }

    @Override
    public Varargs coerce2V(LuaInteger value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        switch (value.type()) {
            case LuaValue.TSTRING:
                LuaValue num = value.tonumber();
                if (num.isnil()) {
                    return COERCION_IMPOSSIBLE;
                }

                if (num.isint()) {
                    return COERCION_CONVERSION;
                }

                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            case LuaValue.TINT:
                return COERCION_INSTANCE;
            case LuaValue.TNUMBER:
                return value.isint() ? COERCION_CONVERSION : COERCION_CONVERSION_LOSS_OF_PRECISION;
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
    public LuaInteger coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        switch (value.type()) {
            case LuaValue.TUSERDATA:
                return LuaValue.valueOf(((Number)value.checkuserdata(Number.class)).intValue());
            default:
                return value.checkinteger();
        }
    }

    @Override
    public Class<LuaInteger> getCoercedClass() {
        return LuaInteger.class;
    }
}
