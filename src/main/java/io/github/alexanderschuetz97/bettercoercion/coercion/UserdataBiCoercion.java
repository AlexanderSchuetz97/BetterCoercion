package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.JavaToLuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaInstance;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class UserdataBiCoercion<T> implements LuaToJavaCoercion<T>, JavaToLuaCoercion<T> {

    protected final Class<T> target;

    protected final LuaCoercion coercion;

    public UserdataBiCoercion(LuaCoercion coercion, Class<T> target) {
        this.target = Objects.requireNonNull(target);
        this.coercion = coercion;
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION_LOSS_OF_PRECISION;
        }
        Object o = value.touserdata();
        if (o == null) {
            return COERCION_IMPOSSIBLE;
        }

        if (o.getClass() == target) {
            return COERCION_INSTANCE;
        }

        if (target.isInstance(o)) {
            return COERCION_CONVERSION;
        }

        return COERCION_IMPOSSIBLE;
    }

    @Override
    public int score(LuaValue value, int chain) {
        int score = score(value);
        return score < 0 ? score : score+chain;
    }

    @Override
    public T coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        Object o = value.touserdata(target);
        if (o != null) {
            return (T) o;
        }

        throw new LuaError("bad argument: userdata "+target.getName()+" expected, got "+value.typename());
    }

    @Override
    public LuaValue coerce2L(T value, Map<Class<?>, Type[]> types) {
        if (value == null) {
            return LuaValue.NIL;
        }

        return new JavaInstance(coercion, value);
    }

    @Override
    public Varargs coerce2V(T value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    public Class<T> getCoercedClass() {
        return target;
    }
}
