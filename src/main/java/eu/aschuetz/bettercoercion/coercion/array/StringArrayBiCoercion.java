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
package eu.aschuetz.bettercoercion.coercion.array;

import eu.aschuetz.bettercoercion.api.BiCoercion;
import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.userdata.array.LuaStringArray;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class StringArrayBiCoercion implements BiCoercion<String[]> {

    protected final LuaCoercion coercion;

    public StringArrayBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(String[] value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : new LuaStringArray(coercion, value);
    }

    @Override
    public Varargs coerce2V(String[] value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(String[].class)) {
            return COERCION_INSTANCE;
        }

        if (value.istable()) {
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
    public String[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(String[].class);
        if (o != null) {
            return (String[]) o;
        }

        LuaTable table = value.checktable();
        return Util.tableToArray(new String[table.length()], coercion.getL2JCoercion(String.class), table);
    }

    @Override
    public Class<String[]> getCoercedClass() {
        return String[].class;
    }
}
