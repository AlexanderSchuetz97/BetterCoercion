package io.github.alexanderschuetz97.bettercoercion.userdata.array;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.userdata.AbstractInstance;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import static io.github.alexanderschuetz97.bettercoercion.util.Util.LENGTH;

public abstract class AbstractArray extends AbstractInstance {

    protected final int length;
    protected final Class<?> componentClazz;
    protected LuaValue componentLuaClazz;

    public AbstractArray(LuaCoercion coercion, Object array, Class<?> componentClazz, int length) {
        super(coercion, array);
        this.componentClazz = componentClazz;
        this.length = length;
    }

    @Override
    public void rawset(LuaValue key, LuaValue value) {
        int i = key.checkint()-1;
        if (i < 0 || i > length) {
            error("Array index out of bounds " + i);
            return;
        }


        setSlot(i, value);
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        if (LENGTH.equals(key)) {
            return valueOf(length);
        }

        int i = key.checkint()-1;
        if (i < 0 || i > length) {
            return NIL;
        }

        return getSlot(i);
    }

    protected abstract void setSlot(int index, LuaValue value);

    protected abstract LuaValue getSlot(int index);

    @Override
    protected LuaValue componentType() {
        if (componentLuaClazz == null) {
            componentLuaClazz = coercion.coerce(componentClazz);
        }

        return componentLuaClazz;
    }

    @Override
    public int rawlen() {
        return length;
    }

    @Override
    public LuaValue len()  {
        final LuaValue h = metatag(LEN);
        if (h.toboolean()) {
            return h.call(this);
        }
        return valueOf(rawlen());
    }

    @Override
    public Varargs next(LuaValue key) {
        if (key.isnil()) {
            return varargsOf(LENGTH, valueOf(length));
        }
        if (LENGTH.eq_b(key)) {
            return inext(NIL);
        }

        return inext(key);
    }

    @Override
    public Varargs inext(LuaValue key) {
        int i = key.optint(0);
        if (i < 0) {
            return NIL;
        }

        if (i < length) {
            return varargsOf(valueOf(i+1), getSlot(i));
        }

        return NIL;
    }
}
