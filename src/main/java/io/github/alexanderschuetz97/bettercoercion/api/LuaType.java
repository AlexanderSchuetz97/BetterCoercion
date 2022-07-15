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


package io.github.alexanderschuetz97.bettercoercion.api;

import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;

import java.util.*;

public class LuaType {

    /**
     * Your application may manage a certain scope of calls to Lua/BetterCoercion by implementing this interface.
     * One way of maintaining instances of BetterCoercion could be a ThreadLocal.
     * By Default there is only ONE instance the default instance.
     *
     * Ideally you configure this class (if you want to change it away from the singleton pattern) once when your application starts.
     */
    public interface CoercionProvider {
        LuaCoercion instance();
    }

    private static class DefaultProvider implements CoercionProvider {

        private final LuaCoercion instance = new LuaCoercion();

        public DefaultProvider() {
            instance.addDefaultCoercions();
            instance.addInheritanceMapping();
            instance.addDefaultMethodAccessorFactories();

        }

        @Override
        public LuaCoercion instance() {
            return instance;
        }
    }

    private static CoercionProvider PROVIDER = new DefaultProvider();

    /**
     * Sets the Coercion Provider which provides the coercion instance used by this static "Factory" class.
     * Any calls made to this class prior to this are not affected by the new provider.
     *
     * This method does not implement any "Multi Threading" semantics as it is intended to be only called once
     * if at all during startup of the application.
     */
    public void setCoercionProvider(CoercionProvider provider) {
        PROVIDER = Objects.requireNonNull(provider);
    }

    /**
     * returns the coercion instance used by default.
     */
    public static LuaCoercion coercion() {
        return PROVIDER.instance();
    }

    /**
     * Return value will be a LuaTable/LuaUserdata instead of an immutable LuaString.
     */
    public static LuaValue valueOfMutable(char[] value) {
        return coercion().coerceMutableArray(value);
    }

    /**
     * Return value will be a LuaTable/LuaUserdata instead of an immutable LuaString.
     */
    public static LuaValue valueOfMutable(byte[] value) {
        return coercion().coerceMutableArray(value);
    }


    public static LuaValue valueOf(Object object) {
        return coercion().coerce(object);
    }

    /**
     * Coerces a Map to a LuaTable/LuaUserdata with type checking.
     */
    public static <K, V> LuaValue valueOfMap(Map<K, V> map, Class<K> key, Class<V> v) {
        return coercion().getJ2LCoercion(Map.class).coerce2L(map, Util.typeMap(Map.class, key, v));
    }

    /**
     * Coerces a List to a LuaTable/LuaUserdata with type checking.
     */
    public static <X> LuaValue valueOfList(List<X> list, Class<X> type) {
        return coercion().getJ2LCoercion(List.class).coerce2L(list, Util.typeMap(Iterable.class, type));
    }

    /**
     * Coerces a Collection to a LuaIterable/LuaUserdata with type checking.
     */
    public static <X> LuaValue valueOfCollection(Collection<X> collection, Class<X> type) {
        return coercion().getJ2LCoercion(Collection.class).coerce2L(collection, Util.typeMap(Iterable.class, type));
    }

    /**
     * Coerces a Collection to a LuaIterable/LuaUserdata with type checking.
     */
    public static <X> LuaValue valueOfIterable(Iterable<X> iterable, Class<X> type) {
        return coercion().getJ2LCoercion(Iterable.class).coerce2L(iterable, Util.typeMap(Iterable.class, type));
    }

    /**
     * returns a Factory for Userdata with a exposed mapping for each method of the class.
     * This call can be used to get rid of the requirement for ":" notation on an object.
     */
    public static <T> LuaUserdataBindingFactory<T> getBindingFactory(Class<T> clazz) {
        return coercion().getBindingFactory(clazz);
    }

    /**
     * returns a Factory for Userdata with a exposed mapping for each method of the provided interface classes.
     * Each method must NOT be called with a instance as first parameter.
     * This call can be used to get rid of the requirement for ":" notation on an object.
     *
     * This method differs from bindInstance as it does not expose any methods that are not inside the interfaces
     * allowing for finer control on what is exposed as API and what is not exposed.
     */
    public static <T> LuaUserdataBindingFactory<T> getBindingFactory(Class<T> impl, Class... iface) {
        return coercion().getBindingFactory(impl, (List) Arrays.asList(iface));
    }

    /**
     * returns a Table with entries for each method any of the provided interface classes.
     * Each method must NOT be called with a instance as first parameter.
     * This call can be used to get rid of the requirement for ":" notation on an object.
     *
     * This method differs from bindInstance as it does not expose any methods that are not inside the interfaces
     * allowing for finer control on what is exposed as API and what is not exposed.
     */
    public static <T> LuaUserdataBindingFactory<T> getBindingFactory(Class<T> impl, Collection<Class<?>> ifaces) {
        return coercion().getBindingFactory(impl, ifaces);
    }

    public static <T> LuaValue bindInstanceAsUserdata(T object) {
        return coercion().getBindingFactory((Class<T>) object.getClass()).bindAsUserdata(object);
    }

    public static <T> LuaValue bindInstanceAsTable(T object) {
        return coercion().getBindingFactory((Class<T>) object.getClass()).bindAsUserdata(object);
    }

    public static <T> LuaValue bindInstanceAsModule(String module, T object) {
        return getBindingFactory((Class<T>) object.getClass()).bindAsModuleLoader(module, object);
    }

    public static <T> LuaValue bindInstanceAsUserdata(T object, Class... iface) {
        return getBindingFactory((Class<T>) object.getClass(), iface).bindAsUserdata(object);
    }

    public static <T> LuaValue bindInstanceAsTable(T object, Class... iface) {
        return getBindingFactory((Class<T>) object.getClass(), iface).bindAsTable(object);
    }

    public static <T> LuaValue bindInstanceAsModule(String module, T object, Class... iface) {
        return getBindingFactory((Class<T>) object.getClass(), iface).bindAsModuleLoader(module, object);
    }



}
