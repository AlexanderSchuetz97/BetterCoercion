package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;

public class LuaIntArray extends AbstractArray {

    private final int[] array;

    public LuaIntArray(LuaCoercion coercion, int[] array) {
        super(coercion, array, int.class, array.length);
        this.array = array;
    }

    @Override
    protected void setSlot(int index, LuaValue value) {
        array[index] = value.checkint();
    }

    @Override
    protected LuaValue getSlot(int index) {
        return valueOf(array[index]);
    }
}
