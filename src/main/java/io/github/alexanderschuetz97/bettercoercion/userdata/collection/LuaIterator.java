package io.github.alexanderschuetz97.bettercoercion.userdata.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class LuaIterator<T extends Iterator> extends LuaUserdata {

    protected static final LuaString NEXT = valueOf("next");
    protected static final LuaString REMOVE = valueOf("remove");
    protected static final LuaString HAS_NEXT = valueOf("hasNext");

    protected static final LuaValue NEXT_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaIterator)) {
                return error("bad argument: LuaIterator expected, got " + arg.typename());
            }

            return ((LuaIterator) arg).next();


        }
    };

    protected static final LuaValue HAS_NEXT_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaIterator)) {
                return error("bad argument: LuaIterator expected, got " + arg.typename());
            }

            return ((LuaIterator) arg).hasNext();


        }
    };

    protected static final LuaValue REMOVE_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaIterator)) {
                return error("bad argument: LuaIterator expected, got " + arg.typename());
            }

            return ((LuaIterator) arg).remove();
        }
    };

    protected static final Map<LuaValue, LuaValue> FUNC = new HashMap<>();
    static {
        FUNC.put(NEXT, NEXT_F);
        FUNC.put(REMOVE, REMOVE_F);
        FUNC.put(HAS_NEXT, HAS_NEXT_F);
    }


    protected final T iterator;
    protected final LuaCoercion coercion;
    protected final Type[] generics;

    public LuaIterator(LuaCoercion coercion, T obj, Type[] generics) {
        super(obj);
        this.coercion = Util.notNull(coercion);
        this.iterator = Util.notNull(obj);
        this.generics = Util.notNull(generics);
    }

    public LuaValue next() {
        try {
            return coercion.coerce(iterator.next(), generics);
        } catch (NoSuchElementException exc) {
            return NIL;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    public LuaValue hasNext() {
        try {
            return valueOf(iterator.hasNext());
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    public LuaValue remove() {
        try {
            iterator.remove();
            return TRUE;
        } catch (UnsupportedOperationException | IllegalStateException exc) {
            return FALSE;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public LuaValue get(LuaValue key) {
        return Util.handleGet(this, key);
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        LuaValue lv = FUNC.get(key);
        if (lv == null) {
            return NIL;
        }
        return lv;
    }
}
