package io.github.alexanderschuetz97.bettercoercion.userdata.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class LuaListIterator<T> extends LuaIterator<ListIterator<T>> {

    protected static final LuaString HAS_PREVIOUS = valueOf("hasPrevious");
    protected static final LuaString NEXT_INDEX = valueOf("nextIndex");
    protected static final LuaString PREVIOUS_INDEX = valueOf("previousIndex");
    protected static final LuaString PREVIOUS = valueOf("previous");
    protected static final LuaString SET = valueOf("set");

    protected static final LuaValue PREVIOUS_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaListIterator)) {
                return error("bad argument: LuaListIterator expected, got " + arg.typename());
            }

            return ((LuaListIterator) arg).previous();
        }
    };

    protected static final LuaValue HAS_PREVIOUS_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaListIterator)) {
                return error("bad argument: LuaListIterator expected, got " + arg.typename());
            }

            return ((LuaListIterator) arg).hasPrevious();
        }
    };

    protected static final LuaValue SET_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg2) {
            if (!(arg instanceof LuaListIterator)) {
                return error("bad argument: LuaListIterator expected, got " + arg.typename());
            }

            return ((LuaListIterator) arg).jset(arg2);
        }
    };

    protected static final LuaValue PREVIOUS_INDEX_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaListIterator)) {
                return error("bad argument: LuaListIterator expected, got " + arg.typename());
            }

            return ((LuaListIterator) arg).previousIndex();
        }
    };

    protected static final LuaValue NEXT_INDEX_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaListIterator)) {
                return error("bad argument: LuaListIterator expected, got " + arg.typename());
            }

            return ((LuaListIterator) arg).nextIndex();
        }
    };



    private static final Map<LuaValue, LuaValue> FUNC = new HashMap<>();
    static {
        FUNC.put(NEXT, NEXT_F);
        FUNC.put(PREVIOUS, PREVIOUS_F);
        FUNC.put(REMOVE, REMOVE_F);
        FUNC.put(SET, SET_F);
        FUNC.put(HAS_NEXT, HAS_NEXT_F);
        FUNC.put(HAS_PREVIOUS, HAS_PREVIOUS_F);
        FUNC.put(NEXT_INDEX, NEXT_INDEX_F);
        FUNC.put(PREVIOUS_INDEX, PREVIOUS_INDEX_F);
    }

    protected final Class<T> type;

    public LuaListIterator(LuaCoercion coercion, ListIterator<T> obj, Class<T> type, Type[] generics) {
        super(coercion, obj, generics);
        this.type = Util.notNull(type);
    }


    public LuaValue previousIndex() {
        try {
            int i = iterator.previousIndex();
            if (i == -1) {
                return NIL;
            }
            return valueOf(i+1);
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    public LuaValue nextIndex() {
        try {
            if (!iterator.hasNext()) {
                return NIL;
            }
            return valueOf(iterator.nextIndex()+1);
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }

    }


    public LuaValue previous() {
        try {
            return coercion.coerce(iterator.previous());
        } catch (NoSuchElementException exc) {
            return NIL;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    public LuaValue jset(LuaValue value) {
        try {
            iterator.set(coercion.coerce(value, type, generics));
            return TRUE;
        } catch (UnsupportedOperationException exc) {
            return FALSE;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    public LuaValue hasPrevious() {
        try {
            return valueOf(iterator.hasPrevious());
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
