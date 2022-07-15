package io.github.alexanderschuetz97.bettercoercion.coercion;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.BiCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaClass;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public class ClassBiCoercion implements BiCoercion<Class> {

    private final LuaCoercion coercion;

    public ClassBiCoercion(LuaCoercion coercion) {
        this.coercion = coercion;
    }

    @Override
    public LuaValue coerce2L(Class value, Map<Class<?>, Type[]> types) {
        return new JavaClass(coercion, value);
    }

    @Override
    public Varargs coerce2V(Class value, Map<Class<?>, Type[]> types) {
        return coerce2L(value, types);
    }

    @Override
    public int score(LuaValue value) {
        if (value.isnil()) {
            return COERCION_CONVERSION;
        }

        if (value.isuserdata(Class.class)) {
            return COERCION_INSTANCE;
        }

        if (value instanceof LuaString) {
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
    public Class<?> coerce2J(LuaValue value, Map<Class<?>, Type[]> types) {
        if (value.isnil()) {
            return null;
        }

        if (value instanceof LuaString) {
            return coercion.findClass(value.checkjstring());
        }

        return (Class<?>) value.checkuserdata(Class.class);
    }

    @Override
    public Class<Class> getCoercedClass() {
        return Class.class;
    }

}
