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
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class VarargsBiCoercion implements BiCoercion<Varargs> {

    @Override
    public LuaValue coerce2L(Varargs value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }
        return value.arg1();
    }

    @Override
    public Varargs coerce2V(Varargs value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NONE;
        }
        return value;
    }

    @Override
    public int score(LuaValue value) {
        return COERCION_INSTANCE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Varargs coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return value;
    }

    @Override
    public Class<Varargs> getCoercedClass() {
        return Varargs.class;
    }
}
