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
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class LuaStringBiCoercion implements BiCoercion<LuaString> {

    @Override
    public LuaValue coerce2L(LuaString value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value;

    }

    @Override
    public Varargs coerce2V(LuaString value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        switch (value.type()) {
            case LuaValue.TSTRING:
                return COERCION_INSTANCE;
            case LuaValue.TINT:
            case LuaValue.TNUMBER:
                return COERCION_CONVERSION;
            case LuaValue.TUSERDATA:
                if (value.isuserdata(byte[].class)) {
                    return COERCION_CONVERSION_N;
                }

                if (value.isuserdata(CharSequence.class)) {
                    return COERCION_CONVERSION_N;
                }

                if (value.isuserdata(Number.class)) {
                    return COERCION_CONVERSION_N;
                }

                return COERCION_IMPOSSIBLE;
            default:
                return COERCION_IMPOSSIBLE;
        }
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public LuaString coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        switch (value.type()) {
            case LuaValue.TINT:
            case LuaValue.TNUMBER:
            case LuaValue.TSTRING:
                return value.checkstring();
            case LuaValue.TUSERDATA:
                if (value.isuserdata(byte[].class)) {
                    byte[] array = (byte[]) value.checkuserdata(byte[].class);
                    return LuaString.valueUsing(Arrays.copyOf(array, array.length));
                }

                if (value.isuserdata(CharSequence.class)) {
                    return LuaString.valueOf(value.checkuserdata(CharSequence.class).toString());
                }

                if (value.isuserdata(Number.class)) {
                    return LuaString.valueOf(value.checkuserdata(Number.class).toString());
                }
        }
        throw new LuaError("cant coerce " + value.typename() + " to LuaString");
    }

    @Override
    public Class<LuaString> getCoercedClass() {
        return LuaString.class;
    }
}
