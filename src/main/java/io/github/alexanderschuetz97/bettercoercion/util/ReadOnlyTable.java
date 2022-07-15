package io.github.alexanderschuetz97.bettercoercion.util;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class ReadOnlyTable extends LuaTable {

    private boolean isReadOnly;

    @Override
    public void rawset(LuaValue key, LuaValue value) {
        if (isReadOnly) {
            error("attempt to set " + key.tojstring() + " on read only table");
        }
        super.rawset(key, value);
    }

    @Override
    public void hashset(LuaValue key, LuaValue value) {
        if (isReadOnly) {
            error("attempt to set " + key.tojstring() + " on read only table");
        }
        super.hashset(key, value);
    }

    @Override
    public LuaValue setmetatable(LuaValue metatable) {
        if (isReadOnly) {
            error("attempt to change metatable on read only table");
        }
        return super.setmetatable(metatable);
    }

    /**
     * Called after filling the table ot make it read only.
     */
    public ReadOnlyTable makeReadOnly() {
        isReadOnly = true;
        return this;
    }

}
