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


package eu.aschuetz.bettercoercion.access;

import eu.aschuetz.bettercoercion.access.method.generic.ReflectionConstructorAccessor;
import eu.aschuetz.bettercoercion.access.method.generic.ReflectionMethodAccessor;
import eu.aschuetz.bettercoercion.access.method.generic.VarArgReflectionConstructorAccessor;
import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.access.field.AbstractFieldAccessor;
import eu.aschuetz.bettercoercion.access.field.ClassFieldAccessor;
import eu.aschuetz.bettercoercion.access.field.ReflectionFieldAccessor;
import eu.aschuetz.bettercoercion.access.method.MethodAccessor;
import eu.aschuetz.bettercoercion.access.method.MethodAccessorFactory;
import eu.aschuetz.bettercoercion.access.method.generic.VarArgReflectionMethodAccessor;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccessorRegistry {

    private final LuaCoercion coercion;
    private final ConcurrentMap<Class<?>, Accessor> accessorMap = new ConcurrentHashMap<>();

    private final Map<MethodAccessorFactoryKey, MethodAccessorFactory> methodFactories = new ConcurrentHashMap<>();
    private static final class MethodAccessorFactoryKey {
        final boolean isStatic;
        final Class[] parameters;

        private MethodAccessorFactoryKey(boolean isStatic, Class[] parameters) {
            this.isStatic = isStatic;
            this.parameters = parameters;
        }

        public MethodAccessorFactoryKey(Method method) {
            this.isStatic = Modifier.isStatic(method.getModifiers());
            this.parameters = method.getParameterTypes();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MethodAccessorFactoryKey that = (MethodAccessorFactoryKey) o;

            if (isStatic != that.isStatic) {
                return false;
            }

            return Arrays.equals(parameters, that.parameters);
        }

        @Override
        public int hashCode() {
            int result = (isStatic ? 1 : 0);
            result = 31 * result + Arrays.hashCode(parameters);
            return result;
        }
    }


    public AccessorRegistry(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    public void registerFactory(MethodAccessorFactory factory) {
        methodFactories.put(new MethodAccessorFactoryKey(factory.isStatic(), factory.parameters()), factory);
    }

    public void reloadCoercions() {
        for (Accessor value : accessorMap.values()) {
            for (AbstractFieldAccessor accessor : value.getFields().values()) {
                accessor.init(coercion);
            }

            for (MethodAccessor[] methodAccessors : value.getMethods().values()) {
                for (MethodAccessor ma : methodAccessors) {
                    ma.init(coercion);
                }
            }

            for (MethodAccessor[] methodAccessors : value.getConstructors().values()) {
                for (MethodAccessor ma : methodAccessors) {
                    ma.init(coercion);
                }
            }
        }
    }

    public void clearCache() {
        accessorMap.clear();
    }

    public Set<Class<?>> getOptimizedClasses() {
        return Collections.unmodifiableSet(accessorMap.keySet());
    }

    public void removeFromCache(Class<?> clazz) {
        accessorMap.remove(clazz);
    }

    public Accessor getAccessorsFor(Class<?> aCL) {
        Accessor entry = accessorMap.get(aCL);
        if (entry != null) {
            return entry;
        }

        synchronized (this) {
            entry = accessorMap.get(aCL);
            if (entry != null) {
                return entry;
            }

            entry = loadEntry(aCL);
            Accessor other = accessorMap.putIfAbsent(aCL, entry);

            return other == null ? entry : other;
        }
    }

    private Accessor loadEntry(Class<?> aClass) {
        Map<LuaValue, MethodAccessor[]> methods = loadMethods(aClass);
        Map<LuaValue, AbstractFieldAccessor> fields = loadFields(aClass);
        return new Accessor(methods, fields, loadConstructors(aClass));
    }

    private Map<LuaValue, MethodAccessor[]> loadConstructors(Class<?> aClass) {
        Map<LuaValue, MethodAccessor[]> constructors = new HashMap<>();

        for (Map.Entry<String, Set<Constructor<?>>> entry : Util.getAllClassConstructor(aClass).entrySet()) {
            constructors.put(LuaValue.valueOf(entry.getKey()), loadConstructors(entry.getValue()));
        }

        return constructors;
    }

    private MethodAccessor[] loadConstructors(Set<Constructor<?>> constructors) {
        MethodAccessor[] methodArray = new MethodAccessor[constructors.size()];
        int i = 0;
        for (Constructor<?> constructor : constructors) {
            MethodAccessor accessor = constructor.isVarArgs() ? new VarArgReflectionConstructorAccessor(constructor) : new ReflectionConstructorAccessor(constructor);
            accessor.init(coercion);
            methodArray[i++] = accessor;

        }

        return methodArray;
    }

    private Map<LuaValue, MethodAccessor[]> loadMethods(Class<?> aClass) {
        Map<LuaValue, MethodAccessor[]> methods = new HashMap<>();

        for (Map.Entry<String, Set<Method>> entry : Util.getAllClassMethods(aClass).entrySet()) {
            methods.put(LuaValue.valueOf(entry.getKey()), loadMethods(entry.getValue()));
        }

        return methods;
    }

    private MethodAccessor[] loadMethods(Set<Method> methods) {
        MethodAccessor[] methodArray = new MethodAccessor[methods.size()];
        int i = 0;
        for (Method method : methods) {
            methodArray[i++] = loadMethod(method);
        }

        return methodArray;
    }

    private MethodAccessor loadMethod(Method method) {
        MethodAccessor accessor;
        if (method.isVarArgs()) {
            accessor = new VarArgReflectionMethodAccessor(method);
        } else {
            MethodAccessorFactory factory = methodFactories.get(new MethodAccessorFactoryKey(method));
            if (factory != null) {
                accessor = factory.create(method);
            } else {
                accessor = new ReflectionMethodAccessor(method);
            }
        }

        accessor.init(coercion);
        return accessor;
    }


    private Map<LuaValue, AbstractFieldAccessor> loadFields(Class<?> aClass) {
        Map<LuaValue, AbstractFieldAccessor> fields = new LinkedHashMap<>();

        AbstractFieldAccessor classAccessor = new ClassFieldAccessor<>(aClass);
        classAccessor.init(coercion);
        fields.put(Util.CLASS, classAccessor);

        for (Map.Entry<String, Field> entry : Util.getAllClassFields(aClass).entrySet()) {
            fields.put(LuaValue.valueOf(entry.getKey()), loadField(entry.getValue()));
        }

        return fields;
    }

    private AbstractFieldAccessor loadField(Field field) {
        AbstractFieldAccessor accessor = new ReflectionFieldAccessor(field);
        accessor.init(coercion);
        return accessor;
    }
}
