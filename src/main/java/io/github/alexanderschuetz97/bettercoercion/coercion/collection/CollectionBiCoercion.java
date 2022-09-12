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

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.collection.LuaCollectionUserdata;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class CollectionBiCoercion implements BiCoercion<Collection> {

    private final LuaCoercion coercion;

    public CollectionBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(Collection value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }


        return new LuaCollectionUserdata(coercion, value, Util.firstType(types.get(Iterable.class)));
    }

    @Override
    public Varargs coerce2V(Collection value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {

        if (value.isuserdata(Collection.class)) {
            return COERCION_INSTANCE;
        }

        if (value.isuserdata(Iterable.class)) {
            return COERCION_CONVERSION_N;
        }

        if (value.isuserdata(Map.class)) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
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
    public Collection coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isuserdata(Collection.class)) {
            return (Collection) value.checkuserdata(Collection.class);
        }

        if (value.isuserdata(Iterable.class)) {
            return Util.iterableToCollection((Iterable) value.checkuserdata(Iterable.class));
        }

        if (value.isuserdata(Map.class)) {
            return ((Map) value.checkuserdata(Map.class)).entrySet();
        }

        if (value.istable()) {
            return Util.tableToCollection(coercion.getL2JCoercion(Util.firstTypeClass(types.get(Iterable.class))), value.checktable());
        }

        if (value.isnil()) {
            return null;
        }

        throw new LuaError("cant coerce " + value.typename() + " to Collection");
    }

    @Override
    public Class<Collection> getCoercedClass() {
        return Collection.class;
    }
}
