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
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class LuaTableBiCoercion implements BiCoercion<LuaTable> {
    @Override
    public LuaValue coerce2L(LuaTable value, Map<Class<?>, Type[]> genericTypes) {
        if (value == null) {
            return LuaValue.NIL;
        }

        return value;
    }

    @Override
    public Varargs coerce2V(LuaTable value, Map<Class<?>, Type[]> genericTypes) {
        if (value == null) {
            return LuaValue.NONE;
        }

        return value;
    }

    @Override
    public int score(LuaValue value) {
        return value.istable() ? COERCION_INSTANCE : COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public LuaTable coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        return value.checktable();
    }

    @Override
    public Class<LuaTable> getCoercedClass() {
        return LuaTable.class;
    }

}
