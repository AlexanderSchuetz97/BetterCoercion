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
