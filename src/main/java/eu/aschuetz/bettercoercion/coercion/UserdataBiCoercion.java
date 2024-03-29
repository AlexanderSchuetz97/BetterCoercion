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

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.api.JavaToLuaCoercion;
import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;
import eu.aschuetz.bettercoercion.userdata.generic.JavaInstance;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class UserdataBiCoercion<T> implements LuaToJavaCoercion<T>, JavaToLuaCoercion<T> {

    protected final Class<T> target;

    protected final LuaCoercion coercion;

    protected volatile boolean zeroArgFetched = false;
    protected volatile Constructor<T> zeroArgConstructor;

    public UserdataBiCoercion(LuaCoercion coercion, Class<T> target) {
        this.target = Objects.requireNonNull(target);
        this.coercion = coercion;
    }

    private boolean hasZeroArgConstructor() {
        if (zeroArgFetched) {
            return zeroArgConstructor != null;
        }

        zeroArgConstructor = Util.getZeroArgConstructor(target);
        zeroArgFetched = true;
        return zeroArgConstructor != null;
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }
        Object o = value.touserdata();
        if (o == null) {
            if (hasZeroArgConstructor() && value.istable() && value.rawlen() == 0) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }
            return COERCION_IMPOSSIBLE;
        }

        if (o.getClass() == target) {
            return COERCION_INSTANCE;
        }

        if (target.isInstance(o)) {
            return COERCION_CONVERSION;
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

        if (hasZeroArgConstructor() && value.istable() && value.rawlen() == 0) {
            try {
                return zeroArgConstructor.newInstance();
            } catch (InvocationTargetException e) {
                throw new LuaError(e.getCause());
            } catch (Throwable e) {
                throw new LuaError(e);
            }
        }

        throw new LuaError("bad argument: userdata "+target.getName()+" expected, got "+value.typename());
    }

    @Override
    public LuaValue coerce2L(T value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }

        return new JavaInstance(coercion, value);
    }

    @Override
    public Varargs coerce2V(T value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    public Class<T> getCoercedClass() {
        return target;
    }
}
