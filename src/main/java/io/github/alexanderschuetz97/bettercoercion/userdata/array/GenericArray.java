package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.JavaToLuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Array;

public class GenericArray extends AbstractArray {

    private final int length;
    private final JavaToLuaCoercion<Object> j2l;
    private final LuaToJavaCoercion<?> l2j;
    private LuaValue componentLuaClazz;

    public GenericArray(LuaCoercion coercion, Object array) {
        super(coercion, array, array.getClass().getComponentType(), Array.getLength(array));
        this.j2l = (JavaToLuaCoercion) coercion.getJ2LCoercion(componentClazz);
        this.l2j = coercion.getL2JCoercion(componentClazz);
        this.length = Array.getLength(array);
    }


    @Override
    protected void setSlot(int index, LuaValue value) {
        Array.set(m_instance, index, l2j.coerce2J(value, Util.NO_TYPE_MAP));
    }

    @Override
    protected LuaValue getSlot(int index) {
        return j2l.coerce2L(Array.get(m_instance, index), Util.NO_TYPE_MAP);
    }
}
