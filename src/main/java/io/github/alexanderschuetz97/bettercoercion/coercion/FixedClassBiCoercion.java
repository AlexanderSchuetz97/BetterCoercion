package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaClass;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Special coercion used to invoke static methods to transform the first parameter.
 */
public class FixedClassBiCoercion implements BiCoercion<Class> {

    private final Class<?> clazz;
    private final LuaCoercion coercion;
    private final LuaValue lstring;

    public FixedClassBiCoercion(LuaCoercion coercion, Class<?> clazz) {
        this.clazz = clazz;
        this.coercion = coercion;
        this.lstring = LuaValue.valueOf(clazz.getName());
    }

    @Override
    public LuaValue coerce2L(Class value, Map<Class<?>, Type[]> genericTypes) {
        if (value != clazz) {
            return LuaValue.NIL;
        }
        return new JavaClass(coercion, value);
    }

    @Override
    public Varargs coerce2V(Class value, Map<Class<?>, Type[]> genericTypes) {
        if (value != clazz) {
            return LuaValue.NONE;
        }

        return new JavaClass(coercion, value);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isuserdata(Class.class)) {
            Class<?> clazz = (Class<?>) value.checkuserdata(Class.class);
            if (clazz == this.clazz) {
                return COERCION_INSTANCE;
            }

            return COERCION_IMPOSSIBLE;
        }


        if (value.type() == LuaValue.TSTRING) {
            if (lstring.eq_b(value)) {
                return COERCION_CONVERSION;
            }

            return COERCION_IMPOSSIBLE;
        }

        if (clazz.isInstance(value)) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        if (value.isuserdata()) {
            Object o = value.touserdata();
            if (clazz.isInstance(o)) {
                return COERCION_CONVERSION_LOSS_OF_PRECISION;
            }

            return COERCION_IMPOSSIBLE;
        }

        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public Class coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes) {
        if (value.type() == LuaValue.TSTRING) {
            if (lstring.eq_b(value)) {
                return this.clazz;
            }
        }

        Class<?> clazz = (Class<?>) value.touserdata(Class.class);
        if (clazz == this.clazz) {
            return this.clazz;
        }

        if (clazz != null) {
            throw new LuaError("bad argument: " + this.clazz.getName() + ".class expected, got " + clazz.getName()  + ".class");
        }

        if (this.clazz.isInstance(value.touserdata())) {
            return this.clazz;
        }

        if (value.isnil()) {
            return this.clazz;
        }

        throw new LuaError("bad argument: " + this.clazz.getName() + ".class expected, got " + value.typename());
    }

    @Override
    public Class<Class> getCoercedClass() {
        return Class.class;
    }
}
