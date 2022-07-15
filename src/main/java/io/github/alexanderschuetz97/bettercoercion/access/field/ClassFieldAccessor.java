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

import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;

import java.lang.reflect.Type;
import java.util.Map;

public class ClassFieldAccessor<X> extends AbstractFieldAccessor<Class, X> {


    private final Class<X> value;
    public ClassFieldAccessor(Class<X> value) {
        this.value = value;
    }

    @Override
    public String getFieldName() {
        return "class";
    }

    @Override
    public Class<Class> getFieldType() {
        return Class.class;
    }

    @Override
    public Class<X> getDeclaringClass() {
        return value;
    }

    @Override
    protected Map<Class<?>, Type[]> getGenericType() {
        return Util.NO_TYPE_MAP;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    protected void set0(X instance, Class value) {
        throw new LuaError("attempt to set class on instance of " + value.getName());
    }

    @Override
    protected Class get0(Object instance) {
        return value;
    }


}
