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

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.Map;

public class ObjectL2JCoercion implements LuaToJavaCoercion<Object> {

    private final LuaCoercion coercion;

    public ObjectL2JCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return LuaToJavaCoercion.COERCION_CONVERSION;
        }
        if (value.isuserdata()) {
            return LuaToJavaCoercion.COERCION_INSTANCE;
        }

        return LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Object coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        return coercion.coerce(value);
    }

    @Override
    public Class<Object> getCoercedClass() {
        return Object.class;
    }
}
