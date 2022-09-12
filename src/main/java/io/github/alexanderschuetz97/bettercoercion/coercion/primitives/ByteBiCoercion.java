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
package io.github.alexanderschuetz97.bettercoercion.coercion.primitives;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class ByteBiCoercion implements BiCoercion<Byte> {

    private final boolean primitive;
    private final Class<Byte> clazz;
    private final Byte nullValue;

    public ByteBiCoercion(boolean isPrimitive) {
        this.primitive = isPrimitive;
        nullValue = isPrimitive ? (byte) 0 : null;
        clazz = isPrimitive ? byte.class : Byte.class;
    }

    @Override
    public LuaValue coerce2L(Byte value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaInteger.valueOf(value);
    }

    @Override
    public Varargs coerce2V(Byte value, Map<Class<?>, Type[]> types) {
        return coerce2L(value,types);
    }


    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return primitive ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
        }

        if (value instanceof LuaString) {
            LuaString str = value.checkstring();
            LuaValue num = value.tonumber();
            if (num.isnil()) {
                return str.m_length > 1 ? COERCION_CONVERSION_LOSS_OF_PRECISION : COERCION_CONVERSION;
            }

            int val = num.checkint();
            if (val < Byte.MIN_VALUE || val > 0xff) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            return COERCION_CONVERSION;
        }



        if (value.isnumber()) {
            int val = value.checkint();
            if (val < Byte.MIN_VALUE || val > 0xff) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            return COERCION_INSTANCE;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Byte coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.type() == LuaValue.TSTRING) {
            LuaString str = value.checkstring();
            if (str.m_length == 0) {
                return nullValue;
            }

            return str.m_bytes[str.m_offset];
        }

        return value.isnil() ? nullValue : (byte)value.checkint();
    }

    @Override
    public Class<Byte> getCoercedClass() {
        return clazz;
    }


}
