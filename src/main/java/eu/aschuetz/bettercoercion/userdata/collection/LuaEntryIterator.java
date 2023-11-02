//
// Copyright Alexander Schütz, 2022
//
// This file is part of BetterCoercion.
//
// BetterCoercion is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BetterCoercion is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// A copy of the GNU Lesser General Public License should be provided
// in the COPYING & COPYING.LESSER files in top level directory of BetterCoercion.
// If not, see <https://www.gnu.org/licenses/>.
//
package eu.aschuetz.bettercoercion.userdata.collection;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class LuaEntryIterator<K,V> extends LuaUserdata {

    protected static final LuaString NEXT = valueOf("next");
    protected static final LuaString REMOVE = valueOf("remove");
    protected static final LuaString HAS_NEXT = valueOf("hasNext");
    protected static final LuaString SET_VALUE = valueOf("setValue");

    protected static final LuaValue NEXT_F = new VarArgFunction() {
        @Override
        public Varargs invoke(Varargs arg) {
            LuaValue a1 = arg.arg1();
            if (!(a1 instanceof LuaEntryIterator)) {
                return error("bad argument: LuaEntryIterator expected, got " + a1.typename());
            }

            return ((LuaEntryIterator) a1).next();


        }
    };

    protected static final LuaValue HAS_NEXT_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaEntryIterator)) {
                return error("bad argument: LuaEntryIterator expected, got " + arg.typename());
            }

            return ((LuaEntryIterator) arg).hasNext();


        }
    };

    protected static final LuaValue REMOVE_F = new OneArgFunction() {
        @Override
        public LuaValue call(LuaValue arg) {
            if (!(arg instanceof LuaEntryIterator)) {
                return error("bad argument: LuaEntryIterator expected, got " + arg.typename());
            }

            return ((LuaEntryIterator) arg).remove();
        }
    };

    protected static final LuaValue SET_VALUE_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg, LuaValue arg1) {
            if (!(arg instanceof LuaEntryIterator)) {
                return error("bad argument: LuaEntryIterator expected, got " + arg.typename());
            }

            return ((LuaEntryIterator) arg).jset(arg1);
        }
    };


    protected static final Map<LuaValue, LuaValue> FUNC = new HashMap<>();
    static {
        FUNC.put(NEXT, NEXT_F);
        FUNC.put(REMOVE, REMOVE_F);
        FUNC.put(HAS_NEXT, HAS_NEXT_F);
        FUNC.put(SET_VALUE, SET_VALUE_F);
    }


    protected final Iterator<Map.Entry<K, V>> iterator;
    protected final LuaCoercion coercion;
    protected final Class<K> keyType;
    protected final Type[] keyGenerics;
    protected final Class<V> valueType;
    protected final Type[] valueGenerics;


    public LuaEntryIterator(LuaCoercion coercion, Iterator<Map.Entry<K, V>> obj, Class<K> keyClass, Type[] keyGenerics, Class<V> valueType, Type[] valueGenerics) {
        super(obj);
        this.coercion = Util.notNull(coercion);
        this.iterator = Util.notNull(obj);
        this.keyType = Util.notNull(keyClass);
        this.keyGenerics = Util.notNull(keyGenerics);
        this.valueType = Util.notNull(valueType);
        this.valueGenerics = Util.notNull(valueGenerics);
    }

    public LuaValue jset(LuaValue value) {
        if (kvEntry == null) {
            throw new LuaError("last element unavailable.");
        }

        try {
            V old = kvEntry.setValue(coercion.coerce(value, valueType, valueGenerics));
            return coercion.coerce(old, valueGenerics);
        } catch (Throwable e) {
            throw Util.wrapException(e);
        }
    }

    private Map.Entry<K, V> kvEntry;

    public Varargs next() {
        try {
            kvEntry = null;
            kvEntry = iterator.next();
            return varargsOf(this, coercion.coerce(kvEntry.getKey(), keyGenerics), coercion.coerce(kvEntry.getValue(), valueGenerics));
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
            kvEntry = null;
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
