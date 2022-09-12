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
package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class StringBiCoercion implements BiCoercion<String> {
    @Override
    public LuaValue coerce2L(String value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaString.valueOf(value);
    }

    @Override
    public int score(LuaValue value) {
        if (value instanceof LuaString) {
            return COERCION_INSTANCE;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value instanceof LuaNumber) {
            return COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public String coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value.isnil() ? null : value.checkstring().tojstring();
    }

    @Override
    public Varargs coerce2V(String value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public Class<String> getCoercedClass() {
        return String.class;
    }
}
