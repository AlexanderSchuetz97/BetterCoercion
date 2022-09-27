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

import io.github.alexanderschuetz97.bettercoercion.access.Accessor;
import io.github.alexanderschuetz97.bettercoercion.access.AccessorRegistry;
import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessorFactory;
import io.github.alexanderschuetz97.bettercoercion.access.method.specific.LuaValue_LuaValue_LuaValue_MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.access.method.specific.LuaValue_LuaValue_MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.access.method.specific.LuaValue_MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.access.method.specific.Varargs_MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.access.method.specific.Void_MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.coercion.*;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.CharArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.DoubleArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.FloatArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.IntArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.LongArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.ShortArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.collection.CollectionBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.collection.IterableBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.collection.ListBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.collection.MapBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.LuaDoubleBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.LuaIntegerBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.LuaNumberBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.LuaStringBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.LuaTableBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.VarargsBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.GenericArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.luavalues.LuaValueBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.BooleanBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.array.ByteArrayBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.ByteBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.CharBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.DoubleBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.FloatBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.IntBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.LongBiConversion;
import io.github.alexanderschuetz97.bettercoercion.coercion.primitives.ShortBiCoercion;
import io.github.alexanderschuetz97.bettercoercion.math.LuaMathHelper;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.LuaByteArray;
import io.github.alexanderschuetz97.bettercoercion.userdata.array.LuaCharArray;
import io.github.alexanderschuetz97.bettercoercion.util.DummyUserdataFactory;
import io.github.alexanderschuetz97.bettercoercion.util.FilteredUserdataFactory;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Registry/State/Cache object for all coercions/transformation between LuaValue and Java Objects.
 *
 * This object is usually acquired by calling {@link LuaType#coercion()} and usually created by a
 * {@link LuaType.CoercionProvider} that can be set by calling
 * {@link LuaType#setCoercionProvider(LuaType.CoercionProvider)}.
 * The Default {@link LuaType.CoercionProvider} will create a new singleton instance of this class.
 * The default implementation of LuaCoercion can safely be used from any number of threads using any number of different globals.
 */
public class LuaCoercion {

    protected static final BiCoercion VOID_COERCION = new VoidBiCoercion();
    protected static final VarargsBiCoercion VARARGS_COERCION = new VarargsBiCoercion();
    protected static final StringBiCoercion STRING_COERCION = new StringBiCoercion();
    protected static final BooleanBiCoercion BOOLEAN_COERCION_P = new BooleanBiCoercion(true);
    protected static final BooleanBiCoercion BOOLEAN_COERCION = new BooleanBiCoercion(false);
    protected static final ByteBiCoercion BYTE_COERCION_P = new ByteBiCoercion(true);
    protected static final ByteBiCoercion BYTE_COERCION = new ByteBiCoercion(false);
    protected static final CharBiCoercion CHAR_COERCION_P = new CharBiCoercion(true);
    protected static final CharBiCoercion CHAR_COERCION = new CharBiCoercion(false);
    protected static final DoubleBiCoercion DOUBLE_COERCION_P = new DoubleBiCoercion(true);
    protected static final DoubleBiCoercion DOUBLE_COERCION = new DoubleBiCoercion(false);
    protected static final FloatBiCoercion FLOAT_COERCION_P = new FloatBiCoercion(true);
    protected static final FloatBiCoercion FLOAT_COERCION = new FloatBiCoercion(false);
    protected static final IntBiCoercion INT_COERCION_P = new IntBiCoercion(true);
    protected static final IntBiCoercion INT_COERCION = new IntBiCoercion(false);
    protected static final ShortBiCoercion SHORT_COERCION_P = new ShortBiCoercion(true);
    protected static final ShortBiCoercion SHORT_COERCION = new ShortBiCoercion(false);
    protected static final LuaIntegerBiCoercion LUA_INTEGER_COERCION = new LuaIntegerBiCoercion();
    protected static final LuaStringBiCoercion LUA_STRING_COERCION = new LuaStringBiCoercion();
    protected static final LuaNumberBiCoercion LUA_NUMBER_COERCION = new LuaNumberBiCoercion();
    protected static final LuaTableBiCoercion LUA_TABLE_COERCION = new LuaTableBiCoercion();
    protected static final LuaDoubleBiCoercion LUA_DOUBLE_COERCION = new LuaDoubleBiCoercion();
    protected static final ByteArrayBiCoercion BYTE_ARRAY_COERCION = new ByteArrayBiCoercion();
    protected static final CharArrayBiCoercion CHAR_ARRAY_COERCION = new CharArrayBiCoercion();
    protected static final NumberL2jCoercion NUMBER_COERCION = new NumberL2jCoercion();

    protected final ConcurrentMap<Class<?>, JavaToLuaCoercion<?>> j2lCoercions = new ConcurrentHashMap<Class<?>, JavaToLuaCoercion<?>>();
    protected final ConcurrentMap<Class<?>, LuaToJavaCoercion<?>> l2jCoercions = new ConcurrentHashMap<Class<?>, LuaToJavaCoercion<?>>();
    protected final Map<Class<?>, List<Class<?>>> inheritanceMapping = new LinkedHashMap<>();

    protected final AccessorRegistry accessors = new AccessorRegistry(this);

    public LuaCoercion() {
        //Empty.
    }

    protected void addDefaultCoercions() {
        addBiCoercion(STRING_COERCION);
        addBiCoercion(BOOLEAN_COERCION_P);
        addBiCoercion(BOOLEAN_COERCION);
        addBiCoercion(BYTE_COERCION_P);
        addBiCoercion(BYTE_COERCION);
        addBiCoercion(CHAR_COERCION_P);
        addBiCoercion(CHAR_COERCION);
        addBiCoercion(DOUBLE_COERCION_P);
        addBiCoercion(DOUBLE_COERCION);
        addBiCoercion(FLOAT_COERCION_P);
        addBiCoercion(FLOAT_COERCION);
        addBiCoercion(INT_COERCION_P);
        addBiCoercion(INT_COERCION);
        addBiCoercion(new LongBiConversion(this, true));
        addBiCoercion(new LongBiConversion(this, false));
        addBiCoercion(SHORT_COERCION_P);
        addBiCoercion(SHORT_COERCION);
        addL2JCoercion(NUMBER_COERCION);

        addBiCoercion(BYTE_ARRAY_COERCION);
        addBiCoercion(CHAR_ARRAY_COERCION);

        addBiCoercion(LUA_STRING_COERCION);
        addBiCoercion(LUA_INTEGER_COERCION);
        addBiCoercion(LUA_NUMBER_COERCION);
        addBiCoercion(LUA_DOUBLE_COERCION);
        addBiCoercion(LUA_TABLE_COERCION);

        addBiCoercion(new ClassBiCoercion(this));
        addBiCoercion(new BigDecimalBiCoercion(this));
        addBiCoercion(new BigIntegerBiCoercion(this));
        addL2JCoercion(new ObjectL2JCoercion(this));

        addBiCoercion(new ShortArrayBiCoercion(this));
        addBiCoercion(new IntArrayBiCoercion(this));
        addBiCoercion(new LongArrayBiCoercion(this));
        addBiCoercion(new FloatArrayBiCoercion(this));
        addBiCoercion(new DoubleArrayBiCoercion(this));

        addBiCoercion(new ListBiCoercion(this));
        addBiCoercion(new CollectionBiCoercion(this));
        addBiCoercion(new IterableBiCoercion(this));
        addBiCoercion(new MapBiCoercion(this));

        addL2JCoercion(new DateL2JCoercion());
    }

    protected void addInheritanceMapping() {
        List<Class<?>> iterables = (List) Arrays.asList(List.class, Collection.class);
        inheritanceMapping.put(Iterable.class, iterables);
        inheritanceMapping.put(Map.class, (List) Collections.emptyList());
    }


    protected void addDefaultMethodAccessorFactories() {
        addMethodAccessorFactory(new MethodAccessorFactory.InstanceMethodAccessorFactory() {
            @Override
            public MethodAccessor create(Method method) {
                return new Void_MethodAccessor(method);
            }
        });

        addMethodAccessorFactory(new MethodAccessorFactory.InstanceMethodAccessorFactory(Varargs.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new Varargs_MethodAccessor(method);
            }
        });


        addMethodAccessorFactory(new MethodAccessorFactory.InstanceMethodAccessorFactory(LuaValue.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new LuaValue_MethodAccessor(method);
            }
        });

        addMethodAccessorFactory(new MethodAccessorFactory.InstanceMethodAccessorFactory(LuaValue.class, LuaValue.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new LuaValue_LuaValue_MethodAccessor(method);
            }
        });

        addMethodAccessorFactory(new MethodAccessorFactory.InstanceMethodAccessorFactory(LuaValue.class, LuaValue.class, LuaValue.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new LuaValue_LuaValue_LuaValue_MethodAccessor(method);
            }
        });

        //static

        addMethodAccessorFactory(new MethodAccessorFactory.StaticMethodAccessorFactory() {
            @Override
            public MethodAccessor create(Method method) {
                return new Void_MethodAccessor(method);
            }
        });

        addMethodAccessorFactory(new MethodAccessorFactory.StaticMethodAccessorFactory(Varargs.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new Varargs_MethodAccessor(method);
            }
        });


        addMethodAccessorFactory(new MethodAccessorFactory.StaticMethodAccessorFactory(LuaValue.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new LuaValue_MethodAccessor(method);
            }
        });

        addMethodAccessorFactory(new MethodAccessorFactory.StaticMethodAccessorFactory(LuaValue.class, LuaValue.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new LuaValue_LuaValue_MethodAccessor(method);
            }
        });

        addMethodAccessorFactory(new MethodAccessorFactory.StaticMethodAccessorFactory(LuaValue.class, LuaValue.class, LuaValue.class) {
            @Override
            public MethodAccessor create(Method method) {
                return new LuaValue_LuaValue_LuaValue_MethodAccessor(method);
            }
        });

    }

    /**
     * Returns a helper for Mathematical functions.
     * Mainly used for the non-trivial pow() operation between 2 {@link java.math.BigDecimal} values.
     * Since the operation is non-trivial this method can be overwritten to improve or use a different pow() implementation.
     *
     * The default implementation provides only an approximation and should NOT be used to calculate values where accuracy is required.
     */
    public LuaMathHelper getMathHelper() {
        return LuaMathHelper.DEFAULT;
    }

    /**
     * Returns the internal reflection cache.
     */
    public AccessorRegistry getAccessorRegistry() {
        return accessors;
    }

    /**
     * Adds a coercion. This does NOT affect any objects already coerced in the past by this coercion.
     */
    public void addJ2LCoercion(JavaToLuaCoercion<?> coercion) {
        this.j2lCoercions.put(coercion.getCoercedClass(), coercion);
        accessors.reloadCoercions();
    }

    public void addBiCoercion(BiCoercion<?> coercion) {
        this.j2lCoercions.put(coercion.getCoercedClass(), coercion);
        this.l2jCoercions.put(coercion.getCoercedClass(), coercion);
        accessors.reloadCoercions();
    }

    /**
     * Adds a coercion. This does NOT affect any objects already coerced in the past by this coercion.
     */
    public void addL2JCoercion(LuaToJavaCoercion<?> coercion) {
        this.l2jCoercions.put(coercion.getCoercedClass(), coercion);
        accessors.reloadCoercions();
    }

    /**
     * Removes a coercion. This does NOT affect any objects already coerced in the past by this coercion.
     */
    public void removeCoercion(Class<?> type) {
        this.l2jCoercions.remove(type);
        this.j2lCoercions.remove(type);
        accessors.reloadCoercions();
    }

    /**
     * Coerce a Java Object into a LuaValue.
     *
     * @param object the object to coerce to Lua. NULL maps to NIL.
     * @return the coerced LuaValue.
     */
    public LuaValue coerce(Object object) {
        if (object == null) {
            return LuaValue.NIL;
        }

        return getJ2LCoercion((Class)object.getClass()).coerce2L(object, Util.NO_TYPE_MAP);
    }

    /**
     * Coerce a Java Object into a LuaValue.
     *
     * @param object the object to coerce to Lua. NULL maps to NIL.
     * @param types generic parameters to the {@link LuaToJavaCoercion}. This represents the java generics. A Object of Map(String, Integer) would have new Type[]{String.class, Integer.class} as parameter to this method.
     * @return the coerced LuaValue.
     */
    public LuaValue coerce(Object object, Type[] types) {
        if (object == null) {
            return LuaValue.NIL;
        }



        return getJ2LCoercion((Class)object.getClass()).coerce2L(object, Util.getGenericHierarchy(object.getClass(), types));
    }

    /**
     * @see LuaType#valueOfMutable(byte[])
     */
    public LuaValue coerceMutableArray(byte[] array) {
        if (array == null) {
            return LuaValue.NIL;
        }

        return new LuaByteArray(this, array);
    }

    /**
     * @see LuaType#valueOfMutable(char[])
     */
    public LuaValue coerceMutableArray(char[] array) {
        if (array == null) {
            return LuaValue.NIL;
        }

        return new LuaCharArray(this, array);
    }

    /**
     * Will try to coerce a LuaValue to the closest possible java value if possible else the LuaValue is returned as is.
     * This method can be used to coerce generics when type erasure removed the information which actual java type is needed.
     * (for example java Collections framework)
     */
    public Object coerce(LuaValue value) {
        return coerceInternal(value, null);
    }

    protected Object coerceInternal(LuaValue value, Map<LuaValue, Map<Object,Object>> recursion) {
        if (value.isnil()) {
            return null;
        }

        if (value.isuserdata()) {
            return value.touserdata();
        }

        if (value.isboolean()) {
            return value.checkboolean();
        }

        if (value.isinttype()) {
            return value.checkint();
        }

        if (value.isnumber()) {
            return value.islong() ? value.checklong() : value.checkdouble();
        }

        if (value.isstring()) {
            return value.checkjstring();
        }

        if (value.istable()) {
            if (recursion == null) {
                recursion = new HashMap<>();
            }

            Map<Object, Object> v = recursion.get(value);
            if (v != null) {
                return v;
            }

            v = new LinkedHashMap<>();
            recursion.put(value, v);

            LuaValue key = LuaValue.NIL;
            while ( true ) {
                Varargs n = value.next(key);
                key = n.arg1();
                if (key.isnil()) {
                    return v;
                }
                v.put(coerceInternal(key, recursion), coerceInternal(n.arg(2), recursion));
            }
        }

        //Function
        //Thread
        //Coroutine
        //Misc
        return value;
    }

    /**
     * Coerce a LuaValue to a Java Object instanceof the give class or interface.
     * Throw LuaError otherwise.
     */
    public <T> T coerce(LuaValue value, Class<T> target) {
        return coerce(value, target, Util.NO_TYPES);
    }

    /**
     * Coerce a LuaValue to a Java Object instanceof the give class or interface.
     * Throw LuaError otherwise.
     */
    public <T> T coerce(LuaValue value, Class<T> target, Type[] generics) {
        if (value.isnil()) {
            return null;
        }

        return getL2JCoercion(target).coerce2J(value, Util.NO_TYPE_MAP);
    }

    /**
     * Perform analysis of method and getters of a given class and optimize all calls.
     * This will make coercion of objects for the given class faster.
     * Normally Classes are optimized when the first getter/setter/method is called on a instance of a given class
     * from lua. This can take ~100ms (for a class with around 100 Methods) to do,
     * as it may or may not generate some bytecode and load some synthetic accessor classes.
     * If the class has already been optimized before that then this "delay" does not occur during execution of lua code.
     *
     * I recommend calling this method for all classes that are known to be used as userdata when you initialize
     * your application. This may be done when that classes themselves are loaded by for example including this call
     * in the userdata classes static constructor.
     */
    public void optimize(Class<?> clazz) {
        if (Varargs.class.isAssignableFrom(clazz)) {
            return;
        }

        getJ2LCoercion(clazz);
        getL2JCoercion(clazz);

        if (!clazz.isArray()) {
            accessors.getAccessorsFor(clazz);
        }


    }


    /**
     * Adds a factory for optimized call to a certain method.
     * This will only affect classes that are not yet optimized.
     * Call clearOptimizationCache afterwards to force re-optimization using the new factory.
     */
    public void addMethodAccessorFactory(MethodAccessorFactory factory) {
        accessors.registerFactory(factory);
    }

    /**
     * This method has to be called when a class should be unloaded.
     */
    public void removeFromOptimizationCache(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        accessors.removeFromCache(clazz);
    }

    /**
     * Returns a unmodifable "view" on the currently optimized classes.
     * The returned collection is unmodifiable but may change as the cache is modified concurrently.
     * You may safely call removeFromOptimizationCache while iterating over the returned set.
     */
    public Set<Class<?>> getClassesInOptimizationCache() {
        return accessors.getOptimizedClasses();
    }

    /**
     * Clears the optimization cache releasing all references to all previously optimized classes.
     * Calling this makes sense when you are using a framework that unloads classes.
     */
    public void clearOptimizationCache() {
        accessors.clearCache();
    }

    /**
     * Returns a coercion that converts Java Objects (instanceof the given class) to LuaValues.
     * This method can be used instead of coerce() if the type is known the be reused.
     */
    public <T> JavaToLuaCoercion<T> getJ2LCoercion(Class<T> source) {
        if (source == null || source == void.class) {
            return VOID_COERCION;
        }

        JavaToLuaCoercion<?> coercion =  j2lCoercions.get(source);
        if (coercion == null) {
            if (LuaValue.class.isAssignableFrom(source)) {
                return new LuaValueBiCoercion(source);
            }

            if (Varargs.class.isAssignableFrom(source)) {
                return (JavaToLuaCoercion) VARARGS_COERCION;
            }

            if (source.isArray()) {
                return new GenericArrayBiCoercion<>(this, source);
            }

           for (Map.Entry<Class<?>, List<Class<?>>> cl : inheritanceMapping.entrySet()) {
               if (cl.getKey().isAssignableFrom(source)) {
                   for (Class cl2 : cl.getValue()) {
                       if (cl2.isAssignableFrom(source)) {
                           coercion = j2lCoercions.get(cl2);
                           if (coercion != null) {
                               return (JavaToLuaCoercion<T>) coercion;
                           }
                       }
                   }

                   coercion = j2lCoercions.get(cl.getKey());
                   if (coercion != null) {
                       return (JavaToLuaCoercion<T>) coercion;
                   }
               }
            }

            if (source.isEnum()) {
                return new EnumBiCoercion(this, source);
            }

            return new UserdataBiCoercion<>(this, source);
        }

        return (JavaToLuaCoercion<T>) coercion;
    }

    /**
     * Returns a coercion that converts LuaValues to Java Objects instanceof the given class.
     * This method can be used instead of coerce() if the type is known the be reused.
     */
    public <T> LuaToJavaCoercion<T> getL2JCoercion(Class<T> target) {
        if (target == null) {
            return VOID_COERCION;
        }

        LuaToJavaCoercion<?> coercion =  l2jCoercions.get(target);
        if (coercion == null) {
            if (target.isArray()) {
                return new GenericArrayBiCoercion<>(this, target);
            }

            if (LuaValue.class.isAssignableFrom(target)) {
                return new LuaValueBiCoercion(target);
            }

            if (Varargs.class == target) {
                return (LuaToJavaCoercion) VARARGS_COERCION;
            }

            if (target.isEnum()) {
                return new EnumBiCoercion(this, target);
            }

            return new UserdataBiCoercion<>(this, target);
        }
        return (LuaToJavaCoercion<T>) coercion;
    }

    /**
     * Overwrite if you use frameworks that restrict classloaders.
     * (Thread.currentThread().getContextClassLoader(); may also be an idea or just...
     * or use some god classloader that sees everything...
     */
    public ClassLoader getClassLoader() {
        return LuaCoercion.class.getClassLoader();
    }

    /**
     * Overwrite if you want more elaborate class lookup.
     * You need to overwrite both getClassLoader() and this method tho as getClassLoader()
     * is still used for class definitions of dynamic Proxies (if you intended to use those...)
     */
    public Class<?> findClass(String name) {
        try {
            return Class.forName(name, true, getClassLoader());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @see LuaType#bindInstance(Object) 
     */
    public <T> LuaUserdataBindingFactory<T> getBindingFactory(Class<T> impl) {
        if (impl == null) {
            return DummyUserdataFactory.INSTANCE;
        }

        return new FilteredUserdataFactory<>(accessors.getAccessorsFor(impl).getNonStaticByName());
    }

    /**
     * @see LuaType#getBindingFactory(Class, Collection)
     */
    public <T> LuaUserdataBindingFactory<T> getBindingFactory(Class<T> impl, Collection<Class<?>> iface) {
        if (impl == null) {
            return DummyUserdataFactory.INSTANCE;
        }

        if (iface == null || iface.size() == 0) {
            return getBindingFactory(impl);
        }

        Map<LuaValue, Collection<MethodAccessor<?>>> filteredMethods = joinInterfaceMethods(impl, iface);
        if (filteredMethods.isEmpty()) {
            return DummyUserdataFactory.INSTANCE;
        }

        return new FilteredUserdataFactory<>(filteredMethods);
    }

    protected Map<LuaValue, Collection<MethodAccessor<?>>> joinInterfaceMethods(Class<?> instance, Collection<Class<?>> interfaces) {
        Map<LuaValue, Collection<MethodAccessor<?>>> results = new HashMap<>();
        Accessor instanceAccessor = accessors.getAccessorsFor(instance);
        Map<LuaValue, Map<String, MethodAccessor<?>>> bySignature = instanceAccessor.getNonStaticMethodsGroupedBySignature();

        for (Class<?> ifaceClass : interfaces) {
            if (!ifaceClass.isAssignableFrom(instance)) {
                continue;
            }

            Accessor ifaceAccessor = accessors.getAccessorsFor(ifaceClass);

            Map<LuaValue, MethodAccessor[]> methodMapping = ifaceAccessor.getMethods();

            for (Map.Entry<LuaValue, MethodAccessor[]> nameToAccessor : methodMapping.entrySet()) {
                Collection<MethodAccessor<?>> resultsForMethodName = results.get(nameToAccessor.getKey());
                Map<String, MethodAccessor<?>> byNamedSignature = bySignature.get(nameToAccessor.getKey());


                for (MethodAccessor<?> accessor : nameToAccessor.getValue()) {
                    if (accessor.isStatic()) {
                        continue;
                    }

                    MethodAccessor<?> mapping = byNamedSignature.get(accessor.getParameterSignature());

                    if (resultsForMethodName == null) {
                        resultsForMethodName = new LinkedHashSet<>(); //Set to avoid overloads.
                        results.put(nameToAccessor.getKey(), resultsForMethodName);
                    }

                    resultsForMethodName.add(mapping);
                }
            }
        }

        return results;
    }
}
