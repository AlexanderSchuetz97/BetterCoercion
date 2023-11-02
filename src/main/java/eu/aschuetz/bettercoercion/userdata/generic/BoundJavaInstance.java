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
package eu.aschuetz.bettercoercion.userdata.generic;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.util.FixedFirstParameterFunction;
import org.luaj.vm2.LuaValue;

import java.util.LinkedHashMap;
import java.util.Map;

public class BoundJavaInstance extends JavaInstance {

    public BoundJavaInstance(LuaCoercion coercion, Object obj) {
        super(coercion, obj);
    }

    protected void init() {
        super.init();
        Map<LuaValue, LuaValue> bound = new LinkedHashMap<>();
        for (Map.Entry<LuaValue, LuaValue> e : methods.entrySet()) {
            bound.put(e.getKey(), new FixedFirstParameterFunction(this, e.getValue()));
        }

        methods = bound;
    }


}
