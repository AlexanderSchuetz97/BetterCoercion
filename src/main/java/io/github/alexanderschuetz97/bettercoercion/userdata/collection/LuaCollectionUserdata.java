package io.github.alexanderschuetz97.bettercoercion.userdata.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.Type;
import java.util.Collection;

public class LuaCollectionUserdata extends LuaIterableUserdata {

    protected final Collection collection;

    protected final LuaValue ADD_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            if (!(arg1 instanceof LuaCollectionUserdata)) {
                return error("bad argument: LuaCollectionUserdata expected, got " + arg1.typename());
            }
            return ((LuaCollectionUserdata) arg1).jadd(arg2);
        }
    };



    public LuaCollectionUserdata(LuaCoercion coercion, Collection obj) {
        this(coercion, obj, Object.class);
    }

    public LuaCollectionUserdata(LuaCoercion coercion, Collection<?> obj, Type type) {
        super(coercion, obj, type);
        collection = obj;
    }



    @Override
    public int rawlen() {
        return collection.size();
    }

    @Override
    public LuaValue len()  {
        final LuaValue h = metatag(LEN);
        if (h.toboolean()) {
            return h.call(this);
        }
        return valueOf(rawlen());
    }

    protected static final LuaValue ADD = valueOf("add");

    @Override
    public LuaValue rawget(LuaValue key) {
        if (ADD.eq_b(key)) {
            return ADD_F;
        }

        return super.rawget(key);
    }

    public LuaValue jadd(LuaValue value) {
        Object jobj = coercion.coerce(value, type);
        try {
            collection.add(jobj);
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }

        return TRUE;
    }
}
