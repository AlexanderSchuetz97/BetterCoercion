package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;

public class LuaDoubleArray extends AbstractArray {

    private final double[] array;

    public LuaDoubleArray(LuaCoercion coercion, double[] array) {
        super(coercion, array, double.class, array.length);
        this.array = array;
    }

    @Override
    protected void setSlot(int index, LuaValue value) {
        array[index] = value.checkdouble();
    }

    @Override
    protected LuaValue getSlot(int index) {
        return valueOf(array[index]);
    }
}
