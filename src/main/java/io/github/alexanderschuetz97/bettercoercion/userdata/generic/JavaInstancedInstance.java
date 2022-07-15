package io.github.alexanderschuetz97.bettercoercion.userdata.generic;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.access.Accessor;
import io.github.alexanderschuetz97.bettercoercion.access.field.AbstractFieldAccessor;
import io.github.alexanderschuetz97.bettercoercion.userdata.AbstractInstance;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.Map;

public class JavaInstancedInstance extends AbstractInstance {


    protected Accessor accessors;
    protected Map<LuaValue, AbstractFieldAccessor> fields;
    protected Map<LuaValue, LuaValue> methods;
    protected LuaTable iterationTable;

    public JavaInstancedInstance(LuaCoercion coercion, Object obj) {
        super(coercion, obj);
    }

    protected void init() {
        accessors = coercion.getAccessorRegistry().getAccessorsFor(m_instance.getClass());
        fields = accessors.getFields();
        methods = accessors.getMethodsLua();
    }

    protected void initKeyTable() {
        iterationTable = new LuaTable();
        for (LuaValue key : fields.keySet()) {
            iterationTable.set(key, LuaValue.TRUE);
        }

        for (LuaValue key : methods.keySet()) {
            iterationTable.set(key, LuaValue.TRUE);
        }

    }

    @Override
    public Varargs next(LuaValue key) {
        LuaValue nextKey = iterationTable.next(key).arg1();
        if (nextKey.isnil()) {
            return NIL;
        }

        LuaValue nextValue = rawget(nextKey);
        return varargsOf(nextKey, nextValue);
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        if (accessors == null) {
            init();
        }


        AbstractFieldAccessor field = fields.get(key);

        if (field != null) {
            return field.get(m_instance);
        }

        LuaValue theMember = methods.get(key);
        if (theMember != null) {
            return theMember;
        }

        return NIL;
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

        error("no such field " + key.checkjstring() + " in " + m_instance.getClass().getName());
    }
}
