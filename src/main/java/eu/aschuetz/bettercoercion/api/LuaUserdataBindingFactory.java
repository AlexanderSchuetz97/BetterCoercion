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

package eu.aschuetz.bettercoercion.api;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

public interface LuaUserdataBindingFactory<T> {

    /**
     * Will return a LuaUserdata that wraps the param userdata only exposing the methods bound by this binding factory.
     */
    LuaValue bindAsUserdata(T userdata);

    /**
     * Will return a LuaUserdata that wraps the param userdata only exposing the methods bound by this binding factory.
     * The returned table may be written to like any normal table.
     */
    LuaValue bindAsTable(T userdata);

    /**
     * Will return a LuaTable that contains only the methods bound by this binding factory.
     * Any attempt to write to this table will raise an error
     */
    LuaValue bindAsReadOnlyTable(T userdata);

    /**
     * Will return a loader suitable for a call to {@link org.luaj.vm2.Globals#load(LuaValue)} 
     * to load a module into the globals with the given name.
     * The module value will be set the equivalent of {@link #bindAsTable(Object)}.
     */
    LuaValue bindAsModuleLoader(String module, T userdata);
}
