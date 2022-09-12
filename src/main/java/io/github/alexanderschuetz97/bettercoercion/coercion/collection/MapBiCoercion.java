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
package io.github.alexanderschuetz97.bettercoercion.coercion.collection;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.collection.LuaMapUserdata;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class MapBiCoercion implements BiCoercion<Map> {

    private final LuaCoercion coercion;

    public MapBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(Map value, Map<Class<?>, Type[]> genericTypes) {
        Type[] types = genericTypes.get(Map.class);
        if (types == null || types.length != 2) {
            return new LuaMapUserdata(coercion, value, Object.class, Object.class);
        }

        return new LuaMapUserdata(coercion, value, types[0], types[1]);
    }

    @Override
    public Varargs coerce2V(Map value, Map<Class<?>, Type[]> genericTypes) {
        return coerce2L(value, genericTypes);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isuserdata(Map.class)) {
            return COERCION_INSTANCE;
        }

        if (value.istable()) {
            return COERCION_CONVERSION_N;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }
    @Override
    public Map coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value.isuserdata(Map.class)) {
            return (Map) value.touserdata(Map.class);
        }

        if (value.istable()) {
            LuaTable table = value.checktable();
            Class keyClass = Object.class;
            Class valueClass = Object.class;
            Type[] keyGenerics = Util.NO_TYPES;
            Type[] valueGenerics = Util.NO_TYPES;
            Type[] types = genericTypes.get(Map.class);

            if (types.length == 2) {
                keyClass = Util.getTypeClass(types[0]);
                keyGenerics = Util.getTypeGenerics(types[0]);
                valueClass = Util.getTypeClass(types[1]);
                valueGenerics = Util.getTypeGenerics(types[1]);
            }

            LuaToJavaCoercion keyCoercion = coercion.getL2JCoercion(keyClass);
            LuaToJavaCoercion valueCoercion = coercion.getL2JCoercion(valueClass);

            return Util.tableToMap(keyCoercion, Util.getGenericHierarchy(keyClass, keyGenerics), valueCoercion, Util.getGenericHierarchy(valueClass, valueGenerics), table);
        }

        return null;
    }

    @Override
    public Class<Map> getCoercedClass() {
        return Map.class;
    }
}
