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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

public class ReflectionFieldAccessor extends AbstractFieldAccessor {

    private final Field field;
    private final boolean isFinal;
    private final boolean isStatic;
    private final Map<Class<?>, Type[]> superTypes;


    public ReflectionFieldAccessor(Field field) {
        this.field = field;
        int mod = field.getModifiers();
        this.isFinal = Modifier.isFinal(mod);
        this.isStatic = Modifier.isStatic(mod);
        this.superTypes = Util.getGenericHierarchy(field.getType(), Util.getGenericFieldType(field));
    }

    @Override
    public String getFieldName() {
        return field.getName();
    }

    @Override
    public Class getFieldType() {
        return field.getType();
    }

    @Override
    public Class getDeclaringClass() {
        return field.getDeclaringClass();
    }

    @Override
    protected Map<Class<?>, Type[]> getGenericType() {
        return superTypes;
    }


    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    protected Object get0(Object instance) {
        try {
            return field.get(isStatic ? null: instance);
        } catch (IllegalAccessException e) {
            throw new LuaError(e);
        }
    }

    @Override
    protected void set0(Object instance, Object value) {
        if (isFinal) {
            throw new LuaError("attempt to set final field " + getFieldName() + " declared in " + getDeclaringClass().getName());
        }

        try {
            field.set(isStatic ? null : instance, value);
        } catch (IllegalAccessException e) {
            throw new LuaError(e);
        }
    }
}
