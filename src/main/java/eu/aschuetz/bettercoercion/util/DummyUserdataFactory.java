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
package eu.aschuetz.bettercoercion.util;

import eu.aschuetz.bettercoercion.api.LuaUserdataBindingFactory;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

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
