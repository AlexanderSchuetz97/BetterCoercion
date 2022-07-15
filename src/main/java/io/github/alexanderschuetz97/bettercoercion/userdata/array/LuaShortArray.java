package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;

public class LuaShortArray extends AbstractArray {

    private final short[] array;

    public LuaShortArray(LuaCoercion coercion, short[] array) {
        super(coercion, array, short.class, array.length);
        this.array = array;
    }

    @Override
    protected void setSlot(int index, LuaValue value) {
        array[index] = (short) value.checkint();
    }

    @Override
    protected LuaValue getSlot(int index) {
        return valueOf(array[index]);
    }
}
