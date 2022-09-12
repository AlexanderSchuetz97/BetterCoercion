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

package io.github.alexanderschuetz97.bettercoercion.coercion.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.GenericArray;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Map;

public class GenericArrayBiCoercion<T> implements BiCoercion<T> {

    protected final Class<T> target;

    protected final LuaCoercion coercion;

    public GenericArrayBiCoercion(LuaCoercion coercion, Class<T> aClass) {
        this.coercion = coercion;
        this.target = aClass;
    }

    @Override
    public LuaValue coerce2L(T value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : new GenericArray(coercion, value);
    }

    @Override
    public Varargs coerce2V(T value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }


    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(target)) {
            return COERCION_INSTANCE;
        }

        if (value.istable()) {
            return COERCION_CONVERSION_N;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }


    @Override
    public T coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(target);
        if (o != null) {
            return (T) o;
        }

        LuaTable table = value.checktable();
        return (T) Util.tableToArray(Array.newInstance(target.getComponentType(), table.length()), coercion.getL2JCoercion(target.getComponentType()), table);
    }

    @Override
    public Class<T> getCoercedClass() {
        return target;
    }
}
