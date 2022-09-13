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
package io.github.alexanderschuetz97.bettercoercion.util;

import io.github.alexanderschuetz97.bettercoercion.api.LuaBinding;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;


public class Util {

    public static final LuaString LENGTH = LuaString.valueOf("length");

    public static final LuaString CLASS = LuaString.valueOf("class");

    public static final LuaString NEW = LuaString.valueOf("new");

    public static final Type[] NO_TYPES = new Type[0];

    public static final Map<Class<?>, Type[]> NO_TYPE_MAP = Collections.emptyMap();

    public static final TypeVariable[] NO_TYPES_VARS = new TypeVariable[0];

    @LuaBinding
    protected static LuaBinding DEFAULT_BINDING;

    public static Map<Class<?>, Type[]> typeMap(Class<?> dec, Type... types) {
        return (Map)Collections.singletonMap(dec, types);
    }

    static {
        try {
            DEFAULT_BINDING = Util.class.getDeclaredField("DEFAULT_BINDING").getAnnotation(LuaBinding.class);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }


    private static final boolean JDK9_OR_HIGHER;
    static {
        String str = System.getProperty("java.version");
        if (str != null) {
            if (str.startsWith("1.")) {
                JDK9_OR_HIGHER = false;
            } else {
                JDK9_OR_HIGHER = true;
            }
        } else {
            JDK9_OR_HIGHER = false;
        }


    }

    public static <T> Collection<T> iterableToCollection(Iterable<T> iterable) {
        Objects.requireNonNull(iterable);

        if (iterable instanceof Collection) {
            return (Collection<T>) iterable;
        }

        List<T> tmp = new ArrayList<>();
        Iterator<T> iter = iterable.iterator();
        while(iter.hasNext()) {
            tmp.add(iter.next());
        }

        return tmp;
    }

    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        Objects.requireNonNull(iterable);

        if (iterable instanceof List) {
            return (List<T>) iterable;
        }

        List<T> tmp = new ArrayList<>();
        Iterator<T> iter = iterable.iterator();
        while(iter.hasNext()) {
            tmp.add(iter.next());
        }

        return tmp;
    }

    public static short[] tableToArray(short[] array, LuaToJavaCoercion<Short> coercion, LuaTable source) {
        for (int i = 0; i < array.length; i++) {
            array[i] = coercion.coerce2J(source.get(i+1), NO_TYPE_MAP);
        }

        return array;
    }


    public static int[] tableToArray(int[] array, LuaToJavaCoercion<Integer> coercion, LuaTable source) {
        for (int i = 0; i < array.length; i++) {
            array[i] = coercion.coerce2J(source.get(i+1), NO_TYPE_MAP);
        }

        return array;
    }

    public static long[] tableToArray(long[] array, LuaToJavaCoercion<Long> coercion, LuaTable source) {
        for (int i = 0; i < array.length; i++) {
            array[i] = coercion.coerce2J(source.get(i+1), NO_TYPE_MAP);
        }

        return array;
    }

    public static float[] tableToArray(float[] array, LuaToJavaCoercion<Float> coercion, LuaTable source) {
        for (int i = 0; i < array.length; i++) {
            array[i] = coercion.coerce2J(source.get(i+1), NO_TYPE_MAP);
        }

        return array;
    }

    public static double[] tableToArray(double[] array, LuaToJavaCoercion<Double> coercion, LuaTable source) {
        for (int i = 0; i < array.length; i++) {
            array[i] = coercion.coerce2J(source.get(i+1), NO_TYPE_MAP);
        }

        return array;
    }

    public static <T> T[] tableToArray(Object array, LuaToJavaCoercion<T> coercion, LuaTable source) {
        T[] arr = (T[]) array;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = coercion.coerce2J(source.get(i+1), NO_TYPE_MAP);
        }

        return arr;
    }

    public static <T> List<T> tableToCollection(LuaToJavaCoercion<T> coercion, LuaTable source) {
        int len = source.length();
        List<T> tmp = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            tmp.add(coercion.coerce2J(source.get(i+1), NO_TYPE_MAP));
        }

        return Collections.unmodifiableList(tmp);
    }

    public static <K,V> Map<K,V> tableToMap(LuaToJavaCoercion<K> key, Map<Class<?>, Type[]> keyGenerics, LuaToJavaCoercion<V> value, Map<Class<?>, Type[]> valueGenerics, LuaTable source) {
        int len = source.length();
        Map<K, V> map = new HashMap<>();
        Varargs res = source.next(LuaValue.NIL);
        while(!res.isnoneornil(1)) {
            map.put(key.coerce2J(res.arg1(), keyGenerics), value.coerce2J(res.arg(2), valueGenerics));
            res = source.next(res.arg1());
        }

        return Collections.unmodifiableMap(map);
    }

    public static Object[] convertArguments(Varargs luaArgs, int off, LuaToJavaCoercion<?>[] javaArgs, Class<?> varArgArrays, LuaToJavaCoercion<?> javaVarArgs) {

        Object[] result = javaVarArgs != null ? new Object[javaArgs.length+1] : new Object[javaArgs.length];

        int j,l;
        for (j = 0, l = off+1; j < javaArgs.length; j++) {
            result[j] = javaArgs[j].coerce2J(luaArgs.arg(l++), NO_TYPE_MAP);
        }

        if (varArgArrays == null) {
            return result;
        }

        int varArgCount = Math.max(0,luaArgs.narg() - off - javaArgs.length);
        Object array = Array.newInstance(varArgArrays.getComponentType(), varArgCount);
        result[result.length-1] = array;
        for(int i = 0; i < varArgCount; i++) {
            Array.set(array, i, javaVarArgs.coerce2J(luaArgs.arg(l++), NO_TYPE_MAP));
        }

        return result;
    }


    public static Map<String, Field> getAllClassFields(Class<?> aClass) {
        Map<String, Field> fields = new HashMap<>();

        LuaBinding classScope = aClass.getAnnotation(LuaBinding.class);
        if (classScope == null) {
            classScope = DEFAULT_BINDING;
        }

        if (!classScope.visible()) {
            return fields;
        }

        for (Field f : aClass.getFields()) {
            LuaBinding fieldScope = f.getAnnotation(LuaBinding.class);
            if (fieldScope == null) {
                fieldScope = classScope;
            }

            if (!fieldScope.visible()) {
                continue;
            }

            try {
                f.setAccessible(true);
            } catch (Exception e) {
                continue;
            }

            fields.put(f.getName(), f);
        }
        String prefix = "?f";

        while(aClass != null) {

            LuaBinding tmp = aClass.getAnnotation(LuaBinding.class);
            if (tmp != null) {
                classScope = tmp;
            }

            if (!classScope.visible() || !classScope.reflect()) {
                break;
            }

            for (Field f : aClass.getDeclaredFields()) {
                try {
                    f.setAccessible(true);
                } catch (Exception e) {
                    continue;
                }

                LuaBinding fieldScope = f.getAnnotation(LuaBinding.class);
                if (fieldScope == null) {
                    fieldScope = classScope;
                }

                if (!fieldScope.visible() || !fieldScope.reflect()) {
                    continue;
                }

                fields.put(prefix+f.getName(), f);
            }

            aClass = aClass.getSuperclass();
            prefix += "?";
        }

        return fields;
    }


    public static Map<String, Set<Method>> getAllClassMethods(Class<?> aClass) {
        Map<String, Set<Method>> methods = new HashMap<>();

        LuaBinding classScope = aClass.getAnnotation(LuaBinding.class);
        if (classScope == null) {
            classScope = DEFAULT_BINDING;
        }

        if (!classScope.visible()) {
            return methods;
        }

        for (Method m : aClass.getMethods()) {
            if (filterMethod(m)) {
                continue;
            }

            LuaBinding methodScope = m.getAnnotation(LuaBinding.class);
            if (methodScope == null) {
                methodScope = classScope;
            }

            if (!methodScope.visible()) {
                continue;
            }

            try {
                m.setAccessible(true);
            } catch (Exception e) {
                continue;
            }

            String name = methodScope.name();
            if (name.isEmpty() || name.startsWith("?")) {
                name = m.getName();
            }

            Set<Method> l = methods.get(name);
            if (l == null) {
                l = new LinkedHashSet<>();
                methods.put(name, l);
            }

            l.add(m);
        }

        String prefix = "?m";

        while(aClass != null) {
            LuaBinding tmp = aClass.getAnnotation(LuaBinding.class);
            if (tmp != null) {
                classScope = tmp;
            }

            if (!classScope.visible() || !classScope.reflect()) {
                break;
            }


            for (Method m : aClass.getDeclaredMethods()) {
                if (filterMethod(m)) {
                    continue;
                }

                LuaBinding methodScope = m.getAnnotation(LuaBinding.class);
                if (methodScope == null) {
                    methodScope = classScope;
                }

                if (!methodScope.visible() || !methodScope.reflect()) {
                    continue;
                }

                try {
                    m.setAccessible(true);
                } catch (Exception e) {
                    continue;
                }

                Set<Method> l = methods.get(prefix + m.getName());
                if (l == null) {
                    l = new LinkedHashSet<>();
                    methods.put(prefix + m.getName(), l);
                }

                l.add(m);

                String key2 = prefix + m.getName() + ";" + Util.getParameterSignature(m.getParameterTypes());

                l = methods.get(key2);
                if (l == null) {
                    l = new LinkedHashSet<>();
                    methods.put(key2, l);
                }

                l.add(m);
            }

            aClass = aClass.getSuperclass();
            prefix += "?";
        }


        return methods;
    }

    private static final Set<String> FILTER_METHOD_SET = new HashSet<>(Arrays.asList("finalize", "clone", "registerNatives"));

    private static boolean filterMethod(Method m) {
        if (JDK9_OR_HIGHER) {
            if (m.getDeclaringClass() == Object.class) {
                if (FILTER_METHOD_SET.contains(m.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Object defaultValue(Class<?> aClass) {
        if (!aClass.isPrimitive()) {
            return null;
        }

        if (aClass == int.class) {
            return Integer.valueOf(0);
        }

        if (aClass == long.class) {
            return Long.valueOf(0);
        }

        if (aClass == short.class) {
            return Short.valueOf((short)0);
        }

        if (aClass == boolean.class) {
            return Boolean.FALSE;
        }

        if (aClass == byte.class) {
            return Byte.valueOf((byte)0);
        }

        if (aClass == char.class) {
            return Character.valueOf((char) 0);
        }

        if (aClass == float.class) {
            return Float.valueOf(0f);
        }

        if (aClass == double.class) {
            return Double.valueOf(0d);
        }

        throw new RuntimeException("Unexpected primitive " + aClass.getName());
    }

    public static String getParameterSignature(Class<?>[] someParameter) {
        StringBuilder sb = new StringBuilder();
        for (Class c : someParameter) {
            sb.append(getSignature(c));
        }

        return sb.toString();
    }

    public static String getSignature(Class<?> tClass) {
        if (tClass == null) {
            return "V";
        }

        if (tClass.isArray()) {
            return "[" + getSignature(tClass.getComponentType());
        }

        if (tClass.isPrimitive()) {
            if (tClass == byte.class) {
                return "B";
            }
            if (tClass == char.class) {
                return "C";
            }
            if (tClass == double.class) {
                return "D";
            }
            if (tClass == float.class) {
                return "F";
            }
            if (tClass == int.class) {
                return "I";
            }
            if (tClass == long.class) {
                return "J";
            }
            if (tClass == short.class) {
                return "S";
            }
            if (tClass == boolean.class) {
                return "Z";
            }

            if (tClass == void.class) {
                return "V";
            }
            throw new RuntimeException("Unexpected primitive " + tClass.getName());
        }

        return "L" + tClass.getName().replace('.', '/') + ";";
    }

    public static LuaError wrapException(Throwable t) {
        if (t instanceof LuaError) {
            return (LuaError) t;
        }

        if (t instanceof InvocationTargetException) {
            Throwable cause = t.getCause();
            if (cause != null) {
                return wrapException(cause);
            }
        }

        return new LuaError(t);
    }

    public static Varargs nullToNone(Varargs varargs) {
        return varargs == null ? LuaValue.NONE : varargs;
    }

    public static LuaValue handleGet(LuaValue src, LuaValue key) {
        LuaValue meta = getFromMeta(src, src.getmetatable(), key);

        if (meta != null) {
            return meta;
        }

        return src.rawget(key);
    }

    public static LuaValue getFromMeta(LuaValue src, LuaValue metaTable, LuaValue key) {
        if (metaTable == null) {
            return null;
        }

        LuaValue tm;
        int loop = 0;
        do {
            if (metaTable.istable()) {
                LuaValue res = metaTable.rawget(key);
                if ((!res.isnil()) || (tm = metaTable.metatag(LuaValue.INDEX)).isnil())
                    return res;
            } else if ((tm = metaTable.metatag(LuaValue.INDEX)).isnil())
                LuaValue.error( "attempt to index ? (a "+src.typename()+" value)" );
            if (tm.isfunction())
                return tm.call(metaTable, key);
            metaTable = tm;
        }
        while ( ++loop < 100 );
        LuaValue.error("loop in gettable");
        return null;
    }

    public static Map<Class<?>, Type[]>[] getGenericParameterTypes(Method method) {
        Type[] t = method.getGenericParameterTypes();
        Map<Class<?>, Type[]>[] ret = new Map[t.length];

        for (int i = 0; i < t.length; i++) {
            ret[i] = Util.getGenericHierarchy(Util.getTypeClass(t[i]), Util.getTypeGenerics(t[i]));
        }

        return ret;
    }

    public static Map<Class<?>, Type[]>[] getGenericParameterTypes(Constructor method) {
        Type[] t = method.getGenericParameterTypes();
        Map<Class<?>, Type[]>[] ret = new Map[t.length];

        for (int i = 0; i < t.length; i++) {
            ret[i] = Util.getGenericHierarchy(Util.getTypeClass(t[i]), Util.getTypeGenerics(t[i]));
        }

        return ret;
    }

    public static Type[] getGenericReturnType(Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }

        return NO_TYPES;
    }

    public static Type[] getGenericFieldType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }

        return NO_TYPES;
    }



    public static Class<?> getTypeClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }

        if (type instanceof ParameterizedType) {
            return getTypeClass(((ParameterizedType) type).getRawType());
        }

        return Object.class;
    }

    public static Type[] getTypeGenerics(Type type) {
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }

        return NO_TYPES;
    }

    public static <T> T notNull(T t) {
        if (t == null) {
            throw new LuaError("NullPointer");
        }

        return t;

    }

    public static Type firstType(Type[] types) {
        return types == null || types.length == 0 ? Object.class : types[0];
    }

    public static Class<?> firstTypeClass(Type[] types) {
        return types.length == 0 ? Object.class : getTypeClass(types[0]);
    }

    /**
     * table.length() is array part only sadly. So we must iterate to figure out the length.
     */
    public static int tableSize(LuaValue table) {
        int c = 0;
        Varargs v = table.next(LuaValue.NIL);
        while(true) {
            if (v.isnoneornil(1)) {
                return c;
            }

            c++;
            v = table.next(v.arg1());
        }
    }

    public static Type resolveType(Type type, TypeVariable[] variables, Type[] resolved) {
        if (type instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable) type;
            Type[] bounds = tv.getBounds();
            if (bounds.length == 0) {
                return Object.class;
            }

            if (resolved.length == variables.length) {
                String n = tv.getName();
                for (int i = 0; i < variables.length; i++) {
                    if (Objects.equals(variables[i].getName(), n)) {
                        //We found our resolved type in the generic state use that one then.
                        return resolved[i];
                    }
                }
            }

            //At some point it must be bound to something useful, or we will just end up with Object at some point.
            return resolveType(bounds[0], variables, resolved);
        }

        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            //High and low may be stuff like ? extends X
            //So the high and low type may be a TypeVariable itself that we have to
            //resolve against the current resolved type state.
            Type[] low = wildcardType.getLowerBounds();
            Type[] high = wildcardType.getUpperBounds();
            if (low.length == 0) {
                if (high.length == 0) {
                    return Object.class;
                }

                return resolveType(high[0], variables, resolved);
            }

            return resolveType(low[0], variables, resolved);
        }

        return type;
    }

    public static Type[] resolveTypeState(Type type, TypeVariable[] variables, Type[] resolved) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pty = (ParameterizedType) type;
            Type[] actualArgs = pty.getActualTypeArguments();
            actualArgs = Arrays.copyOf(actualArgs, actualArgs.length);
            for (int i = 0; i < actualArgs.length; i++) {
                actualArgs[i] = resolveType(actualArgs[i], variables, resolved);
            }

            return actualArgs;
        }

        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] low = wildcardType.getLowerBounds();
            Type[] high = wildcardType.getUpperBounds();
            if (low.length == 0) {
                if (high.length == 0) {
                    return NO_TYPES;
                }

                return resolveTypeState(high[0], variables, resolved);
            }

            return resolveTypeState(low[0], variables, resolved);
        }

        return NO_TYPES;
    }

    /**
     * most generics are related. assuming a child is for example ArrayList.class then its ancestor would
     * be Iterable.class since all the way to Iterable.class it will have the same generics.
     *
     * Depending on the class it may have multiple of those. Only ancestor which share all generic semantics will apply.
     * Example: a class implementing List and Callable with the same generics will have both Iterable and Callable as ancestor.
     *
     *
     */
    public static Set<Class<?>> getGenericAncestors(Class<?> child) {
        if (child == null) {
            return Collections.emptySet();
        }

        Set<Class<?>> ancestors = new LinkedHashSet<>();


        Queue<Class<?>> candidates = new LinkedList<>();
        candidates.add(child);

        while(true) {
            Class<?> candidate = candidates.poll();
            if (candidate == null) {
                return ancestors;
            }

            if (!ancestors.add(candidate)) {
                continue;
            }

            TypeVariable[] type = candidate.getTypeParameters();
            if (type.length == 0) {
                return ancestors;
            }


            Type superType = candidate.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                Type[] superTypes = ((ParameterizedType) superType).getActualTypeArguments();
                if (Arrays.equals(superTypes, type)) {
                    candidates.add(candidate.getSuperclass());
                }
            }

            Type[] ifaces = candidate.getGenericInterfaces();
            Class<?>[] rawIfaces = candidate.getInterfaces();
            for (int i = 0; i < ifaces.length; i++) {
                if (ifaces[i] instanceof  ParameterizedType) {
                    Type[] ifaceType = ((ParameterizedType) ifaces[i]).getActualTypeArguments();
                    if (Arrays.equals(ifaceType, type)) {
                        candidates.add(rawIfaces[i]);
                    }
                }
            }
        }
    }

    public static Map<Class<?>, Type[]> getGenericHierarchy(Class<?> type, Type[] types) {
        Type[] selfCopy = Arrays.copyOf(types, types.length);

        for (int i = 0; i < selfCopy.length; i++) {
            selfCopy[i] = resolveType(selfCopy[i], NO_TYPES_VARS, NO_TYPES);
        }

        Map<Class<?>, Type[]> hierarchy = new HashMap<>();
        hierarchy.put(type, selfCopy);
        Queue<Class> toInspect = new LinkedList<>();
        toInspect.add(type);
        while(true) {
            Class current = toInspect.poll();
            if (current == null) {
                break;
            }
            Type[] state = hierarchy.get(current);
            TypeVariable[] vars = current.getTypeParameters();


            Type superType = current.getGenericSuperclass();
            Class superClass = getTypeClass(superType);
            if (!hierarchy.containsKey(superClass)) {
                hierarchy.put(superClass, resolveTypeState(superType, vars, state));
                toInspect.add(superClass);
            }

            Type[] ifaces = current.getGenericInterfaces();
            for (Type iface : ifaces) {
                Class ifaceClass = getTypeClass(iface);
                if (hierarchy.containsKey(ifaceClass)) {
                    continue;
                }
                hierarchy.put(ifaceClass, resolveTypeState(iface, vars, state));
                toInspect.add(ifaceClass);
            }




        }

        return hierarchy;

    }

    public static Map<String, Set<Constructor<?>>> getAllClassConstructor(Class<?> aClass) {
        Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
        Map<String, Set<Constructor<?>>> result = new LinkedHashMap<>();

        LuaBinding classBinding = aClass.getAnnotation(LuaBinding.class);
        if (classBinding == null) {
            classBinding = DEFAULT_BINDING;
        }

        for (int i = 0; i < declaredConstructors.length; i++) {
            Constructor<?> c = declaredConstructors[i];
            LuaBinding binding = c.getAnnotation(LuaBinding.class);
            if (binding == null) {
                binding = classBinding;
            }

            if (!binding.visible()) {
                continue;
            }

            try {
                c.setAccessible(true);
            } catch (Exception exc) {
                continue;
            }

            String name = binding.name();
            if (name.isEmpty() || name.startsWith("?")) {
                name = "new";
            }

            Set<Constructor<?>> current = result.get(name);
            if (current == null) {
                current = new LinkedHashSet<>();
                result.put(name, current);
            }

            current.add(c);
        }

        return result;
    }
}
