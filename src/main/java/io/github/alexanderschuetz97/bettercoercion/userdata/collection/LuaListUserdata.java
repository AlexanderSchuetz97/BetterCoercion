package io.github.alexanderschuetz97.bettercoercion.userdata.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class LuaListUserdata extends LuaCollectionUserdata {

    private final List list;


    public LuaListUserdata(LuaCoercion coercion, List<?> obj) {
        this(coercion, obj, Object.class);
    }

    public <T> LuaListUserdata(LuaCoercion coercion, List<?> obj, Type type) {
        super(coercion, obj, type);
        list = obj;
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        int i = key.toint();
        if (i <= 0) {
            return super.rawget(key);
        }

        boolean b = false;
        Object o = null;
        try {
            if (i > 0 && i <= list.size()) {
                o = list.get(i - 1);
                b = true;
            }
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }

        if (b) {
            return coercion.coerce(o, generics);
        }

        return super.rawget(key);
    }

    public void rawset(LuaValue key, LuaValue value) {
        int i = key.toint();
        if (i <= 0) {
            super.rawset(key, value);
            return;
        }

        Object o = coercion.coerce(value, type, generics);

        i--;
        try {
            int ls = list.size();
            if (i < ls) {
                list.set(i, o);
                return;
            }

            if (i == ls) {
                list.add(o);
            }

        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }

        super.rawset(key, value);
    }

    protected Iterator jiter() {
        return list.listIterator();
    }

    @Override
    public Varargs inext(LuaValue key) {
        int chk = key.checkint();
        Object o;
        try {
            if (chk >= list.size()) {
                return NIL;
            }

            o = list.get(chk);
        } catch (IndexOutOfBoundsException exc) {
            return NIL;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }

        return varargsOf(valueOf(chk+1), coercion.coerce(o));
    }
}
