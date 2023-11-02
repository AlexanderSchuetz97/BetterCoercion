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

package eu.aschuetz.bettercoercion.api;

import eu.aschuetz.bettercoercion.util.ParameterizedTypeImpl;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class BetterCoercionLib extends TwoArgFunction {
    protected final String libName;
    protected final LuaCoercion coercion;
    protected Globals globals;

    public BetterCoercionLib(String libName, LuaCoercion coercion) {
        this.libName = Objects.requireNonNull(libName);
        this.coercion = Objects.requireNonNull(coercion);
    }


    public BetterCoercionLib() {
        this("java", LuaType.coercion());
    }

    public BetterCoercionLib(LuaCoercion coercion) {
        this("java", coercion);
    }

    public BetterCoercionLib(String libName) {
        this(libName, LuaType.coercion());
    }


    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        globals = env.checkglobals();
        LuaTable bc = new LuaTable();

        bc.set("bindClass", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return bindClass(arg);
            }
        });
        bc.set("new", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return newInstance(args);
            }
        });
        bc.set("loadLib", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                return loadLib(arg1, arg2);
            }
        });

        bc.set("createProxy", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return createProxy(args);
            }
        });

        bc.set("toBigInteger", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return toBigInteger(arg);
            }
        });

        bc.set("toBigDecimal", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                return toBigDecimal(arg);
            }
        });

        bc.set("newArray", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return newArray(args);
            }
        });

        bc.set("toArray", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                return toArray(arg1, arg2);
            }
        });

        bc.set("newCollection", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return newCollection(args);
            }
        });

        bc.set("newMap", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return newMap(args);
            }
        });

        bc.set("bindGenericType", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return bindGenericType(args);
            }
        });

        bc.set("bindTableInstance", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return bindTableInstance(args);
            }
        });

        bc.set("bindUserdataInstance", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                return bindUserdataInstance(args);
            }
        });

        if (globals.package_ != null) {
            globals.package_.setIsLoaded(libName, bc);
        }

        return bc;
    }

    /**
     * !!!THREAD SAFETY WARNING!!!
     * If the proxy is called from different threads then this will need to be synchronized. Luajs LuaJavaLib also does completely ignore this.
     * If this is needed overwrite this method and synchronize on a ReentrantLock that is held during execution of a lua code.
     * This cant be done by managed by the library in a meaningful way since this lock has to be managed by the java application that initally executes lua code
     * since the library cant have any knowledge of when lua code is running or not. If the lua code makes use of coroutines then this may become an even bigger nightmare
     * since all coroutines create their own java thread and must be treated as the same thread by the lock. This is probably only possible if either a custom reentrant lock is implemented
     * or the coroutine lib is modified to use the lock as the condition. I therefore recommend to only use the createProxy function with care and only if you know what you are doing.
     */
    protected LuaValue handleProxyInvocation(String methodName, LuaValue luaObject, LuaValue[] arguments) {
        return luaObject.get(methodName).invoke(LuaValue.varargsOf(luaObject, LuaValue.varargsOf(arguments))).arg1();
    }

    protected LuaValue bindClass(LuaValue arg) {
        String name = arg.checkjstring();
        Class<?> clazz = coercion.findClass(name);
        if (clazz == null) {
            throw new LuaError("Class not found " + name);
        }

        return coercion.coerce(clazz);
    }

    protected Varargs newInstance(Varargs args) {
        String name = args.arg1().checkjstring();
        Class<?> clazz = coercion.findClass(name);
        if (clazz == null) {
            throw new LuaError("Class not found " + name);
        }

        LuaValue value = coercion.coerce(clazz);
        return value.get("new").invoke(varargsOf(value, args.subargs(2)));
    }

    protected LuaValue loadLib(LuaValue arg1, LuaValue arg2) {
        // get constructor
        String classname = arg1.checkjstring(1);
        String methodname = arg2.checkjstring(2);
        Class<?> clazz = coercion.findClass(classname);
        if (clazz == null) {
            throw new LuaError("Class not found " + classname);
        }

        Method method = null;
        try {
            method = clazz.getMethod(methodname);
        } catch (NoSuchMethodException e) {
            throw new LuaError("Zero argument method not found " + methodname + " in " + classname);
        }

        Object result = null;
        try {
            result = method.invoke(clazz, new Object[] {});
        } catch (Exception e) {
            throw new LuaError(e);
        }

        if (result instanceof LuaValue ) {
            return (LuaValue) result;
        }

        return NIL;
    }

    protected Varargs createProxy(Varargs args) {
        final int niface = args.narg()-1;
        if ( niface <= 0 ) {
            throw new LuaError("no interfaces");
        }

        final LuaValue lobj = args.checktable(niface+1);


        final Class<?>[] ifaces = new Class[niface];
        for ( int i=0; i<niface; i++ ) {
            String name = args.checkjstring(i+1);
            Class<?> clazz = coercion.findClass(name);
            if (ifaces[i] == null) {
                throw new LuaError("Class not found " + name);
            }

            if (!clazz.isInterface()) {
                throw new LuaError(name + " is not an interface.");
            }

            ifaces[i] = clazz;
        }

        Object proxy = Proxy.newProxyInstance(coercion.getClassLoader(), ifaces, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                LuaValue[] parameters = new LuaValue[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameters[i] = coercion.coerce(args[i]);
                }

                return coercion.coerce(handleProxyInvocation(method.getName(), lobj, parameters), method.getReturnType(), Util.getGenericReturnType(method));
            }
        });


        return LuaValue.userdataOf(proxy);
    }

    protected LuaValue toBigInteger(LuaValue arg) {
        switch (arg.type()) {
            case TSTRING:
                try {
                    return coercion.coerce(new BigInteger(arg.checkjstring()));
                } catch (NumberFormatException e) {
                    throw Util.wrapException(e);
                }
            case TNUMBER:
            case TINT:
                return coercion.coerce(coercion.getMathHelper().toBigInteger(arg.checkdouble()));
            case TUSERDATA:
                if (arg.isuserdata(BigInteger.class)) {
                    return arg;
                }

                if (arg.isuserdata(Number.class)) {
                    return coercion.coerce(coercion.getMathHelper().toBigInteger((Number) arg.checkuserdata(Number.class)));
                }
        }

        return error(arg.typename() + "not coercible to BigInteger");
    }

    protected LuaValue toBigDecimal(LuaValue arg) {
        switch (arg.type()) {
            case TSTRING:
                try {
                    return coercion.coerce(new BigDecimal(arg.checkjstring()));
                } catch (NumberFormatException e) {
                    throw Util.wrapException(e);
                }
            case TINT:
                return coercion.coerce(coercion.getMathHelper().toBigInteger(arg.checkdouble()));
            case TUSERDATA:
                if (arg.isuserdata(BigDecimal.class)) {
                    return arg;
                }

                if (arg.isuserdata(Number.class)) {
                    return coercion.coerce(coercion.getMathHelper().toBigDecimal((Number) arg.checkuserdata(Number.class)));
                }
        }

        return error(arg.typename() + "not coercible to BigDecimal");
    }

    protected LuaValue toArray(LuaValue clazz, LuaValue table) {
        Util.notNull(table);
        Class<?> cl = classFromLuaValue(clazz);

        Object array = null;
        if (table.isuserdata()) {
            array = userdataToArray(cl, table.checkuserdata());
        } else if (table.istable()) {
            int len = table.length();
            if (len < 0) {
                throw new LuaError("table len < 0");
            }
            array = Array.newInstance(cl, len);
            LuaToJavaCoercion<?> eleCoercion = coercion.getL2JCoercion(cl);

            for (int i = 0; i < len; i++) {
                Array.set(array, i, eleCoercion.coerce2J(table.get(i), Util.NO_TYPE_MAP));
            }
        } else {
            throw new LuaError("cannot convert " + table.typename() + " to array");
        }

        return coerceArray(array);
    }



    protected Object userdataToArray(Class<?> arrayClass, Object userdata) {

        if (userdata instanceof Iterable) {
            userdata = Util.iterableToCollection((Iterable<?>) userdata).toArray();
        }

        if (!userdata.getClass().isArray()) {
            throw new LuaError("cannot convert " + userdata.getClass().getName() + " to array");
        }

        if (userdata.getClass().getComponentType() == arrayClass) {
            return userdata;
        }

        int aLen = Array.getLength(userdata);
        Object array = Array.newInstance(arrayClass, aLen);


        for (int i = 0; i <aLen;i++) {
            Object ele = Array.get(userdata, i);
            if (ele == null && arrayClass.isPrimitive()) {
                throw new LuaError("cannot assign null of j-index " + i + " to primitive type " + arrayClass.getName());
            }

            if (ele != null && !arrayClass.isInstance(ele)) {
                throw new LuaError("cannot cast " + ele.getClass().getName() + " to " + arrayClass.getName());
            }

            Array.set(array, i, arrayClass.cast(ele));
        }

        return array;
    }

    protected LuaValue newArray(Varargs args) {
        Util.notNull(args);
        Class<?> cl = classFromLuaValue(args.arg1());

        int n = args.narg();

        Object array;
        try {
            if (n <= 2) {
                array = Array.newInstance(cl, args.optint(2, 0));
            } else {
                int[] x = new int[n-1];
                for (int i = 0; i < x.length; i++) {
                    x[i] = args.checkint(i+2);
                }

                array = Array.newInstance(cl, x);
            }
        } catch (IllegalArgumentException | NegativeArraySizeException exc) {
            throw Util.wrapException(exc);
        }

        return coerceArray(array);
    }

    protected Class<?> classFromLuaValue(LuaValue value) {
        Util.notNull(value);

        Class<?> cl;
        if (value.isstring()) {
            String name = value.checkjstring();
            cl = coercion.findClass(name);
            if (cl == null) {
                throw new LuaError("Class not found " + name);
            }
        } else {
            cl = (Class<?>) value.checkuserdata(1, Class.class);
        }

        return cl;
    }

    protected LuaValue coerceArray(Object array) {
        if (array == null) {
            return coercion.coerce(array);
        }

        Class<?> cl = array.getClass();

        if (cl == byte.class) {
            return coercion.coerceMutableArray((byte[]) array);
        } else if (cl == char.class) {
            return coercion.coerceMutableArray((char[]) array);
        } else {
            return coercion.coerce(array);
        }
    }

    protected LuaValue newCollection(Varargs varargs) {
        Type clazz = (Type) varargs.checkuserdata(1, Type.class);
        if (clazz instanceof Class) {
            Type type = (Type) varargs.optuserdata(2, Type.class, Object.class);
            return newCollectionFromClass((Class<?>) clazz, type);
        }

        if (clazz instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) clazz;
            clazz = ptype.getRawType();
            if (!(clazz instanceof Class)) {
                throw new LuaError("expected collection class got non class type " + ptype.getClass().getName());
            }

            Type[] types = ptype.getActualTypeArguments();
            if (types == null || types.length == 0) {
                return newCollectionFromClass((Class<?>) clazz, Object.class);
            }

            if (types.length != 1) {
                throw new LuaError("more than 1 type parameters got " + types.length);
            }

            return newCollectionFromClass((Class<?>) clazz, types[0]);
        }

        throw new LuaError("expected collection class got unexpected type " + clazz.getClass().getName());
    }

    protected LuaValue newCollectionFromClass(Class<?> clazz, Type type) {
        if (!Collection.class.isAssignableFrom(clazz)) {
            return error("expected collection class got " + clazz.getName());
        }

        Collection collection;
        try {
            collection = (Collection) clazz.newInstance();
        } catch (Exception e) {
            throw Util.wrapException(e);
        }

        return coercion.coerce(collection, new Type[]{type});
    }

    protected LuaValue newMap(Varargs varargs) {
        Type clazz = (Type) varargs.checkuserdata(1, Type.class);

        if (clazz instanceof Class) {
            Type k = (Type) varargs.optuserdata(2, Type.class, Object.class);
            Type v = (Type) varargs.optuserdata(3, Type.class, Object.class);
            return newMapFromClass((Class<?>)clazz, k, v);
        }

        if (clazz instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) clazz;
            clazz = ptype.getRawType();
            if (!(clazz instanceof Class)) {
                throw new LuaError("expected map class got non class type " + ptype.getClass().getName());
            }

            Type[] types = ptype.getActualTypeArguments();
            if (types == null || types.length == 0) {
                return newMapFromClass((Class<?>) clazz, Object.class, Object.class);
            }

            if (types.length != 2) {
                throw new LuaError("more or less than 2 type parameters got " + types.length);
            }

            return newMapFromClass((Class<?>) clazz, types[0], types[1]);
        }

        throw new LuaError("expected map class got unexpected type " + clazz.getClass().getName());
    }

    protected LuaValue newMapFromClass(Class<?> mapClass, Type key, Type value) {
        if (!Map.class.isAssignableFrom(mapClass)) {
            return error("expected map class got " + mapClass.getName());
        }

        Map map;
        try {
            map = (Map) mapClass.newInstance();
        } catch (Exception e) {
            throw Util.wrapException(e);
        }
        return coercion.coerce(map, new Type[]{key, value});
    }

    protected LuaValue bindGenericType(Varargs varargs) {
        Type type = (Type) varargs.checkuserdata(1, Type.class);
        Type[] types;
        if (varargs.istable(2)) {
            LuaValue table = varargs.arg(2);
            types = new Type[table.length()];

            for (int i = 0; i < types.length; i++) {
                types[i] = (Type) table.get(i+1).checkuserdata(Type.class);
            }

        } else {
            types = new Type[varargs.narg()-1];
            for (int i = 0; i < types.length; i++) {
                types[i] = (Type) varargs.checkuserdata(i+2, Type.class);
            }
        }

        return coercion.coerce(new ParameterizedTypeImpl(type, types));
    }

    protected LuaValue bindUserdataInstance(Varargs args) {
        return getBindingFactory(args).bindAsUserdata(args.checkuserdata(1));
    }

    protected LuaValue bindTableInstance(Varargs args) {
        return getBindingFactory(args).bindAsTable(args.checkuserdata(1));
    }

    protected LuaUserdataBindingFactory getBindingFactory(Varargs args) {
        Object object = args.checkuserdata(1);
        if (args.narg() == 1) {
            return coercion.getBindingFactory(object.getClass());
        }

        Collection<Class<?>> interfaces = new ArrayList<>(args.narg()-1);
        for (int i = 2; i <= args.narg(); i++) {
            interfaces.add((Class<?>) args.checkuserdata(i, Class.class));
        }

        return coercion.getBindingFactory(object.getClass(), interfaces);
    }
}
