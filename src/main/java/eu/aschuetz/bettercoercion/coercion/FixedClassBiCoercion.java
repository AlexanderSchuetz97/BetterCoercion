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

import eu.aschuetz.bettercoercion.api.BiCoercion;
import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.userdata.generic.JavaClass;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Special coercion used to invoke static methods to transform the first parameter.
 */
public class FixedClassBiCoercion implements BiCoercion<Class> {

    private final Class<?> clazz;
    private final LuaCoercion coercion;
    private final LuaValue lstring;

    public FixedClassBiCoercion(LuaCoercion coercion, Class<?> clazz) {
        this.clazz = clazz;
        this.coercion = coercion;
        this.lstring = LuaValue.valueOf(clazz.getName());
    }

    @Override
    public LuaValue coerce2L(Class value, Map<Class<?>, Type[]> genericTypes) {
        if (value != clazz) {
            return LuaValue.NIL;
        }
        return new JavaClass(coercion, value);
    }

    @Override
    public Varargs coerce2V(Class value, Map<Class<?>, Type[]> genericTypes) {
        if (value != clazz) {
            return LuaValue.NONE;
        }

        return new JavaClass(coercion, value);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isuserdata(Class.class)) {
            Class<?> clazz = (Class<?>) value.checkuserdata(Class.class);
            if (clazz == this.clazz) {
                return COERCION_INSTANCE;
            }

            return COERCION_IMPOSSIBLE;
        }


        if (value.type() == LuaValue.TSTRING) {
            if (lstring.eq_b(value)) {
                return COERCION_CONVERSION;
            }

            return COERCION_IMPOSSIBLE;
        }

        if (clazz.isInstance(value)) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isuserdata()) {
            Object o = value.touserdata();
            if (clazz.isInstance(o)) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            return COERCION_IMPOSSIBLE;
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
    public Class coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value.type() == LuaValue.TSTRING) {
            if (lstring.eq_b(value)) {
                return this.clazz;
            }
        }

        Class<?> clazz = (Class<?>) value.touserdata(Class.class);
        if (clazz == this.clazz) {
            return this.clazz;
        }

        if (clazz != null) {
            throw new LuaError("bad argument: " + this.clazz.getName() + ".class expected, got " + clazz.getName()  + ".class");
        }

        if (this.clazz.isInstance(value.touserdata())) {
            return this.clazz;
        }

        if (value.isnil()) {
            return this.clazz;
        }

        throw new LuaError("bad argument: " + this.clazz.getName() + ".class expected, got " + value.typename());
    }

    @Override
    public Class<Class> getCoercedClass() {
        return Class.class;
    }
}
