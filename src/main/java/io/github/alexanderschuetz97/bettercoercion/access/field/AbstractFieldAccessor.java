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

package io.github.alexanderschuetz97.bettercoercion.access.field;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.JavaToLuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractFieldAccessor<T, X> {

    protected LuaToJavaCoercion<T> l2j;
    protected JavaToLuaCoercion<T> j2l;

    public abstract String getFieldName();

    public abstract Class<T> getFieldType();

    public abstract Class<X> getDeclaringClass();

    protected abstract Map<Class<?>, Type[]> getGenericType();

    public abstract boolean isStatic();

    protected abstract T get0(X instance);

    protected void set0(X instance, T value) {
        throw new LuaError("attempt to set final field " + getFieldName() + " declared in " + getDeclaringClass().getName());
    }

    public void init(LuaCoercion coercion) {
        Class<T> ft = getFieldType();
        this.l2j = coercion.getL2JCoercion(ft);
        this.j2l = coercion.getJ2LCoercion(ft);
    }

    public void set(X instance, LuaValue value) {
        set0(instance, l2j.coerce2J(value, getGenericType()));
    }

    public LuaValue get(X instance) {
        return j2l.coerce2L(get0(instance), getGenericType());
    }

}
