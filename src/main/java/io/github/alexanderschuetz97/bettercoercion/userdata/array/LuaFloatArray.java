package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;

public class LuaFloatArray extends AbstractArray {

    private final float[] array;

    public LuaFloatArray(LuaCoercion coercion, float[] array) {
        super(coercion, array, float.class, array.length);
        this.array = array;
    }

    @Override
    protected void setSlot(int index, LuaValue value) {
        array[index] = (float) value.checkdouble();
    }

    @Override
    protected LuaValue getSlot(int index) {
        return valueOf(array[index]);
    }
}
