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

import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;
import eu.aschuetz.bettercoercion.util.ScoreHelper;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.Map;

public class NumberL2jCoercion implements LuaToJavaCoercion<Number>  {

    private static final Integer ZERO = 0;

    @Override
    public int score(LuaValue value) {
        if (value.isuserdata(Number.class)) {
            return COERCION_INSTANCE;
        }

        if (value.isnumber()) {
            if (value.type() == LuaValue.TSTRING) {
                return COERCION_CONVERSION;
            }
            return COERCION_INSTANCE;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        return ScoreHelper.chain(score(value), chain);
    }

    @Override
    public Number coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value.isuserdata(Number.class)) {
            return (Number) value.touserdata(Number.class);
        }

        if (value.isnil()) {
            return ZERO;
        }

        LuaValue number = value.tonumber();
        if (number.isnil()) {
            throw new LuaError("bad argument: number expected, got " + value.typename());
        }

        if (number.isint()) {
            return Integer.valueOf(number.toint());
        }

        if (number.islong()) {
            return Long.valueOf(number.tolong());
        }

        return Double.valueOf(number.todouble());
    }

    @Override
    public Class<Number> getCoercedClass() {
        return Number.class;
    }
}
