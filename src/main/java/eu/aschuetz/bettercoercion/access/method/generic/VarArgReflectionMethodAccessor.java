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

package eu.aschuetz.bettercoercion.access.method.generic;

import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

public class VarArgReflectionMethodAccessor<X, T> extends AbstractGenericMethodAccessor<X, T> {

    private final Class<?> instanceClass;
    private final Method method;
    private final Class<?>[] parameterTypes;
    private final Object[] defaultValues;
    private final Class<?> varargComponent;
    private final String signature;
    private final Map<Class<?>, Type[]> typeGenerics;
    private final Map<Class<?>, Type[]>[] paramGenerics;

    public VarArgReflectionMethodAccessor(Method method) {
        this.method = method;
        if (Modifier.isStatic(method.getModifiers())) {
            instanceClass = null;
        } else {
            instanceClass = method.getDeclaringClass();
        }

        parameterTypes = method.getParameterTypes();
        defaultValues = new Object[parameterTypes.length];
        int end = defaultValues.length-1;
        for (int i = 0; i < end;i++) {
            defaultValues[i] = Util.defaultValue(parameterTypes[i]);
        }

        varargComponent = parameterTypes[end].getComponentType();

        defaultValues[end] = Array.newInstance(varargComponent, 0);
        signature = Util.getParameterSignature(parameterTypes);
        Type returnType = method.getGenericReturnType();
        typeGenerics = Util.getGenericHierarchy(Util.getTypeClass(returnType), Util.getTypeGenerics(returnType));
        paramGenerics = Util.getGenericParameterTypes(method);
    }

    @Override
    protected Map<Class<?>, Type[]>[] getGenericParameterTypes() {
        return paramGenerics;
    }

    @Override
    protected Map<Class<?>, Type[]> getGenericReturnTypes() {
        return typeGenerics;
    }

    @Override
    public Class getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public Class getInstanceClass() {
        return instanceClass;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public boolean isVarArgs() {
        return true;
    }

    @Override
    public int getArgCount() {
        return parameterTypes.length;
    }

    @Override
    public Class getReturnType() {
        return method.getReturnType();
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
    public Object call0(Object instance) {
        try {
            return method.invoke(instance, pack());
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                throw new LuaError(e);
            }
            throw new LuaError(cause);
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }

    @Override
    public Object call1(Object instance, Object param1) {
        try {
            return method.invoke(instance, pack(param1));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                throw new LuaError(e);
            }
            throw new LuaError(cause);
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }

    @Override
    public Object call2(Object instance, Object param1, Object param2) {
        try {
            return method.invoke(instance, pack(param1, param2));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                throw new LuaError(e);
            }
            throw new LuaError(cause);
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }

    @Override
    public Object call3(Object instance, Object param1, Object param2, Object param3) {
        try {
            return method.invoke(instance, pack(param1, param2, param3));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                throw new LuaError(e);
            }
            throw new LuaError(cause);
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }

    @Override
    public Object callV(Object instance, Object[] params) {
        try {
            return method.invoke(instance, pack(params));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                throw new LuaError(e);
            }
            throw new LuaError(cause);
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }


    protected Object[] pack(Object... args) {
        Object[] packed = new Object[parameterTypes.length];
        if (args.length < parameterTypes.length) {
            System.arraycopy(args, 0, packed, 0, args.length);
            System.arraycopy(defaultValues, args.length,packed, args.length, packed.length - args.length);
            return packed;
        }

        int nonVarArgsCount = packed.length-1;
        int varVargCount = args.length-nonVarArgsCount;
        System.arraycopy(args, 0, packed, 0, nonVarArgsCount);
        Object varArgs = Array.newInstance(varargComponent, varVargCount);
        packed[packed.length-1] = varArgs;
        System.arraycopy(args, nonVarArgsCount, varArgs, 0, varVargCount);
        return packed;
    }
}
