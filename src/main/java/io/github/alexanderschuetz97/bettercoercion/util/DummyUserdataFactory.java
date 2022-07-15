package io.github.alexanderschuetz97.bettercoercion.util;

import io.github.alexanderschuetz97.bettercoercion.api.LuaUserdataBindingFactory;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class DummyUserdataFactory implements LuaUserdataBindingFactory {
    public static final LuaUserdataBindingFactory INSTANCE = new DummyUserdataFactory();

    @Override
    public LuaValue bindAsUserdata(Object userdata) {
        if (userdata == null) {
            return LuaValue.NIL;
        }
        return new LuaUserdata(userdata);
    }

    @Override
    public LuaValue bindAsTable(Object userdata) {
        return new LuaTable();
    }

    @Override
    public LuaValue bindAsReadOnlyTable(Object userdata) {
        return new ReadOnlyTable().makeReadOnly();
    }

    @Override
    public LuaValue bindAsModuleLoader(String module, Object userdata) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                return NIL;
            }
        };
    }
}
