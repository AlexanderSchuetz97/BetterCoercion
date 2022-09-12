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
