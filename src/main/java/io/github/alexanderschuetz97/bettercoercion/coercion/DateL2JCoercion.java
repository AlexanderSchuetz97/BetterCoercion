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

import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

public class DateL2JCoercion implements LuaToJavaCoercion<Date> {

    @Override
    public int score(LuaValue value) {
        if (value.isuserdata(Date.class)) {
            return COERCION_INSTANCE;
        }

        if (value.isnumber()) {
            if (value.islong()) {
                return COERCION_CONVERSION;
            }

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
    public Date coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value.isuserdata(Date.class)) {
            return (Date) value.touserdata(Date.class);
        }

        if (value.isnumber()) {
            return new Date(value.checklong());
        }

        throw new LuaError("bad argument: java.util.Date expected, got " + value.typename());
    }

    @Override
    public Class<Date> getCoercedClass() {
        return Date.class;
    }
}
