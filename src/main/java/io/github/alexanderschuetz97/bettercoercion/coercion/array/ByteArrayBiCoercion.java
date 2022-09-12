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

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class ByteArrayBiCoercion implements BiCoercion<byte[]> {



    @Override
    public LuaValue coerce2L(byte[] value, Map<Class<?>, Type[]> types) {
        return value == null ? LuaValue.NIL : LuaString.valueUsing(Arrays.copyOf(value, value.length));
    }

    @Override
    public Varargs coerce2V(byte[] value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value instanceof LuaString) {
            return COERCION_INSTANCE;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(byte[].class)) {
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
    public byte[] coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object arr = value.touserdata(byte[].class);
        if (arr != null) {
            return (byte[]) arr;
        }

        LuaString str = value.checkstring();
        byte[] copy = new byte[str.m_length];
        System.arraycopy(str.m_bytes, str.m_offset, copy, 0, str.m_length);
        return copy;
    }

    @Override
    public Class<byte[]> getCoercedClass() {
        return byte[].class;
    }
}
