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
package eu.aschuetz.bettercoercion.coercion.luavalues;

import eu.aschuetz.bettercoercion.api.BiCoercion;
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
