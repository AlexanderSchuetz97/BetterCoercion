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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class ReflectionConstructorAccessor<X, T> extends AbstractGenericMethodAccessor<X, T> {

    private final Constructor method;
    private final Class<?>[] parameterTypes;
    private final Object[] defaultValues;
    private final String signature;
    private final Map<Class<?>, Type[]>[] paramGenerics;

    public ReflectionConstructorAccessor(Constructor method) {
        this.method = method;
        parameterTypes = method.getParameterTypes();
        defaultValues = new Object[parameterTypes.length];
        for (int i = 0; i < defaultValues.length;i++) {
            defaultValues[i] = Util.defaultValue(parameterTypes[i]);
        }
        signature = Util.getParameterSignature(parameterTypes);
        paramGenerics = Util.getGenericParameterTypes(method);
    }


    @Override
    protected Map<Class<?>, Type[]> getGenericReturnTypes() {
        return Util.NO_TYPE_MAP;
    }

    @Override
    public Class getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public Class getInstanceClass() {
        return null;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    protected Map<Class<?>, Type[]>[] getGenericParameterTypes() {
        return paramGenerics;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public int getArgCount() {
        return parameterTypes.length;
    }

    @Override
    public Class getReturnType() {
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
    public Object call0(Object instance) {
        try {
            return method.newInstance(pack());
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
            return method.newInstance(pack(param1));
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
            return method.newInstance(pack(param1, param2));
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
            return method.newInstance(pack(param1, param2, param3));
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
            return method.newInstance(pack(params));
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
        if (args.length == parameterTypes.length) {
            return args;
        }

        if (args.length > parameterTypes.length) {
            return Arrays.copyOfRange(args, 0, parameterTypes.length);
        }

        Object[] packed = new Object[parameterTypes.length];
        System.arraycopy(args, 0, packed, 0, args.length);
        System.arraycopy(defaultValues, args.length, packed, args.length, packed.length-args.length);
        return packed;
    }
}
