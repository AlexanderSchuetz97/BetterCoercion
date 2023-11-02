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
package eu.aschuetz.bettercoercion.userdata;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.Buffer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInstance extends LuaUserdata {
    protected final LuaCoercion coercion;
    protected LuaValue clazz;

    protected interface MetaProcessor {
        LuaValue process(AbstractInstance instance);
    }

    private static Map<LuaValue, MetaProcessor> metaProcessorMap = new HashMap<>();
    static {
        metaProcessorMap.put(LuaString.valueOf("?ct"), new MetaProcessor() {
            @Override
            public LuaValue process(AbstractInstance instance) {
                return instance.componentType();
            }
        });

        metaProcessorMap.put(valueOf("?c"), new MetaProcessor() {
            @Override
            public LuaValue process(AbstractInstance instance) {
                return instance.classType();
            }
        });
    }




    public AbstractInstance(LuaCoercion coercion, Object obj) {
        super(Util.notNull(obj));
        this.coercion = Util.notNull(coercion);
    }

    public final LuaValue get(LuaValue key) {
        if (key instanceof LuaString) {
            LuaValue meta = processMetaInformation(key.checkstring());
            if (meta != null) {
                return meta;
            }
        }

        return Util.handleGet(this, key);
    }

    @Override
    public final void set(LuaValue key, LuaValue value) {
        if (m_metatable != null) {
            if (settable(m_metatable, key, value)) {
                return;
            }
        }

        rawset(key, value);
    }

    protected final LuaValue processMetaInformation(LuaString value) {
        if (value.m_length < 2) {
            return null;
        }
        if (value.m_bytes[value.m_offset] != '?') {
            return null;
        }

        MetaProcessor processor = metaProcessorMap.get(value);
        if (processor == null) {
            return null;
        }

        return processor.process(this);
    }

    @Override
    public Varargs next(LuaValue key) {
        return NIL;
    }

    @Override
    public Varargs inext(LuaValue key) {
        return NIL;
    }

    /**
     * Component type of a Collection/Map/Array if known.
     */
    protected LuaValue componentType() {
        return NIL;
    }

    protected LuaValue classType() {
        if (clazz == null) {
            clazz = coercion.coerce(m_instance.getClass());
        }

        return clazz;
    }




    private LuaString strValueCache;
    public LuaString strvalue() {
        if (strValueCache == null) {
            try {
                strValueCache = LuaString.valueOf(tojstring());
            } catch (Exception exc) {
                throw new LuaError(exc);
            }
        }

        return strValueCache;
    }


    @Override
    public LuaValue tostring() {
        return strvalue();
    }


    public boolean isArray() {
        return m_instance.getClass().isArray();
    }

    protected LuaTableUserdataView tableview;
    protected LuaNumberUserdataView numberview;
    @Override
    public boolean eq_b(LuaValue val) {
        return val == this || val == tableview || val == numberview || super.eq_b(val);
    }

    @Override
    public boolean equals(Object val) {
        return val == this || val == tableview || val == numberview || super.equals(val);
    }

    @Override
    public boolean raweq(LuaValue val) {
        return val == this || val == tableview || val == numberview || super.raweq(val);
    }

    @Override
    public int strcmp(LuaValue rhs) {
        return strvalue().strcmp(rhs);
    }

    @Override
    public int strcmp(LuaString rhs) {
        return strvalue().strcmp(rhs);
    }

    @Override
    public LuaValue concat(LuaValue rhs) {
        return strvalue().concat(rhs);
    }

    @Override
    public LuaValue concatTo(LuaValue lhs) {
        return strvalue().concatTo(lhs);
    }

    @Override
    public LuaValue concatTo(LuaNumber lhs) {
        return strvalue().concatTo(lhs);
    }

    @Override
    public LuaValue concatTo(LuaString lhs) {
        return strvalue().concatTo(lhs);
    }

    @Override
    public Buffer buffer() {
        return strvalue().buffer();
    }

    @Override
    public Buffer concat(Buffer rhs) {
        return strvalue().concat(rhs);
    }

    //DELEGATE STUFF
    @Override
    public LuaTable checktable() {
        if (tableview == null) {
            tableview = new LuaTableUserdataView(this);
        }
        return tableview;
    }

    @Override
    public LuaTable opttable(LuaTable defVal) {
        return checktable();
    }

    @Override
    public boolean istable() {
        return false;
    }


    @Override
    public boolean isnumber() {
        return false;
    }

    @Override
    public LuaValue tonumber() {
        if (!isnumber()) {
            return super.tonumber();
        }
        return initNumberView();
    }


    @Override
    public LuaNumber optnumber(LuaNumber defval) {
        if (!isnumber()) {
            return defval;
        }
        return initNumberView();
    }

    @Override
    public LuaNumber checknumber() {
        if (!isnumber()) {
            return super.checknumber();
        }

        return initNumberView();
    }

    @Override
    public LuaNumber checknumber(String msg) {
        if (!isnumber()) {
            return super.checknumber(msg);
        }

        return initNumberView();
    }

    @Override
    public String optjstring(String defval) {
        return checkjstring();
    }

    @Override
    public LuaString optstring(LuaString defval) {
        return strvalue();
    }

    protected LuaNumberUserdataView initNumberView() {
        if (numberview == null) {
            numberview = new LuaNumberUserdataView(this);
        }

        return numberview;
    }
}
