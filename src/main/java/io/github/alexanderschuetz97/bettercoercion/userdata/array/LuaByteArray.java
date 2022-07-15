package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;

public class LuaByteArray extends AbstractArray {

    private final byte[] array;

    public LuaByteArray(LuaCoercion coercion, byte[] array) {
        super(coercion, array, byte.class, array.length);
        this.array = array;
    }

    @Override
    protected void setSlot(int index, LuaValue value) {
        array[index] = (byte) value.checkint();
    }

    @Override
    protected LuaValue getSlot(int index) {
        return valueOf(array[index]);
    }
}
