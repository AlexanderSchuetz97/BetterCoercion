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
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class VoidBiCoercion implements BiCoercion<Object> {
    @Override
    public LuaValue coerce2L(Object value, Map<Class<?>, Type[]> types) {
        return LuaValue.NIL;
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_INSTANCE;
        }
        return COERCION_CONVERSION;

    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Object coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return null;
    }

    @Override
    public Varargs coerce2V(Object value, Map<Class<?>, Type[]> types) {
        return LuaValue.NONE;
    }

    @Override
    public Class getCoercedClass() {
        return Void.class;
    }
}
