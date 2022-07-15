package io.github.alexanderschuetz97.bettercoercion.userdata.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.access.field.AbstractFieldAccessor;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaInstance;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.Type;
import java.util.Map;

public class LuaMapUserdata extends JavaInstance {

    protected final Map map;

    protected final LuaValue PUT_F = new ThreeArgFunction() {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
            if (!(arg1 instanceof LuaMapUserdata)) {
                return error("bad argument: LuaMapUserdata expected, got " + arg1.typename());
            }
            return ((LuaMapUserdata) arg1).jput(arg2, arg3);
        }
    };

    protected final LuaValue REMOVE_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            if (!(arg1 instanceof LuaMapUserdata)) {
                return error("bad argument: LuaMapUserdata expected, got " + arg1.typename());
            }
            return ((LuaMapUserdata) arg1).jremove(arg2);
        }
    };

    protected final LuaValue CONTAINS_KEY_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            if (!(arg1 instanceof LuaMapUserdata)) {
                return error("bad argument: LuaMapUserdata expected, got " + arg1.typename());
            }
            return ((LuaMapUserdata) arg1).jcontainsKey(arg2);
        }
    };

    protected final LuaValue CONTAINS_VALUE_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            if (!(arg1 instanceof LuaMapUserdata)) {
                return error("bad argument: LuaMapUserdata expected, got " + arg1.typename());
            }
            return ((LuaMapUserdata) arg1).jcontainsValue(arg2);
        }
    };


    protected final Class key;

    protected final Type[] keyGenerics;


    protected final Class value;

    protected final Type[] valueGenerics;

    public LuaMapUserdata(LuaCoercion coercion, Map<?, ?> obj, Type key, Type value) {
        super(coercion, obj);
        this.key = Util.getTypeClass(key);
        keyGenerics = Util.getTypeGenerics(key);
        this.value = Util.getTypeClass(value);
        this.valueGenerics = Util.getTypeGenerics(value);
        this.map = obj;
    }

    @Override
    public int rawlen() {
        return map.size();
    }

    @Override
    public LuaValue len()  {
        final LuaValue h = metatag(LEN);
        if (h.toboolean()) {
            return h.call(this);
        }
        return valueOf(rawlen());
    }

    protected static final LuaValue PUT = valueOf("put");
    protected static final LuaValue REMOVE = valueOf("remove");
    protected static final LuaValue CONTAINS_KEY = valueOf("containsKey");
    protected static final LuaValue CONTAINS_VALUE = valueOf("containsValue");



    @Override
    public LuaValue rawget(LuaValue key) {
        if (PUT.eq_b(key)) {
            return PUT_F;
        }

        if (REMOVE.eq_b(key)) {
            return REMOVE_F;
        }

        if (CONTAINS_KEY.eq_b(key)) {
            return CONTAINS_KEY_F;
        }

        if (CONTAINS_VALUE.eq_b(key)) {
            return CONTAINS_VALUE_F;
        }

        LuaValue raw = super.rawgetInternal(key);
        if (raw != null) {
            return raw;
        }

        Object o = coercion.coerce(key, this.key, keyGenerics);
        o = map.get(o);
        return coercion.coerce(o, valueGenerics);
    }

    @Override
    public void rawset(LuaValue key, LuaValue value) {
        if (accessors == null) {
            init();
        }

        AbstractFieldAccessor field = fields.get(key);
        if (field != null) {
            field.set(m_instance, value);
            return;
        }

        Object k = coercion.coerce(key, this.key, keyGenerics);
        Object v = coercion.coerce(value, this.value, valueGenerics);
        try {
            map.put(k, v);
        } catch (Throwable e) {
            throw Util.wrapException(e);
        }
    }

    public LuaValue jput(LuaValue k, LuaValue v) {
        Object jk = coercion.coerce(k, key, keyGenerics);
        Object jv = coercion.coerce(v, value, valueGenerics);
        try {
            return coercion.coerce(map.put(jk, jv));
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }


    public LuaValue jremove(LuaValue value) {
        Object jk = coercion.coerce(value, key, keyGenerics);
        try {
            return coercion.coerce(map.remove(jk));
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }


    public LuaValue jcontainsKey(LuaValue k) {
        Object jk = coercion.coerce(k, key, keyGenerics);
        try {
            return map.containsKey(jk) ? TRUE : FALSE;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    public LuaValue jcontainsValue(LuaValue k) {
        Object jk = coercion.coerce(k, key, keyGenerics);
        try {
            return map.containsKey(jk) ? TRUE : FALSE;
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs next(LuaValue key) {
        LuaEntryIterator iter;
        if (key.isnil()) {
            iter = new LuaEntryIterator(coercion, map.entrySet().iterator(), this.key, keyGenerics, value, valueGenerics);
        } else {
            if (!(key instanceof LuaEntryIterator)) {
                return NIL;
            }

            iter = (LuaEntryIterator) key;
        }

        if (TRUE.eq_b(iter.hasNext())) {
            return iter.next();
        }

        return NIL;
    }
}
