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


package io.github.alexanderschuetz97.bettercoercion.access.method.specific;

import io.github.alexanderschuetz97.bettercoercion.api.JavaToLuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.coercion.FixedClassBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractSpecificMethodAccessor implements MethodAccessor {

    protected final Method method;

    protected final String signature;


    protected final Map<Class<?>, Type[]> returnTypes;

    protected final Map<Class<?>, Type[]>[] parameterTypes;

    protected final boolean isStatic;

    protected AbstractSpecificMethodAccessor(Method method) {
        this.method = method;
        Type returnType = method.getGenericReturnType();
        this.returnTypes = Util.getGenericHierarchy(Util.getTypeClass(returnType), Util.getTypeGenerics(returnType));
        this.parameterTypes = Util.getGenericParameterTypes(method);
        //TODO LAZY?
        signature = Util.getParameterSignature(method.getParameterTypes());
        isStatic = Modifier.isStatic(method.getModifiers());
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Class<?> getInstanceClass() {
        return method.getDeclaringClass();
    }

    @Override
    public Class getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public String getParameterSignature() {
        return signature;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    protected LuaCoercion coercion;
    protected LuaToJavaCoercion instanceCoercion;

    @Override
    public void init(LuaCoercion coercion) {
        instanceCoercion = isStatic ? new FixedClassBiCoercion(coercion, getDeclaringClass()) : coercion.getL2JCoercion(getInstanceClass());
        this.coercion = coercion;
    }

    protected Varargs coerceResult(Object object) {
        if (object == null) {
            return LuaValue.NIL;
        }

        JavaToLuaCoercion j2LCoercion = coercion.getJ2LCoercion(object.getClass());
        return j2LCoercion.coerce2V(object, returnTypes);
    }

    protected Object coerceInstance(Varargs value) {
        Object x = instanceCoercion.coerce2J(value.arg1(), Util.NO_TYPE_MAP);
        if (!isStatic && x == null) {
            throw new LuaError("attempt to call " + getName() + " declared in " + getInstanceClass().getName() + " without a instance");
        }

        return x;
    }





}
