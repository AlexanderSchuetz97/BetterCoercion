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
package io.github.alexanderschuetz97.bettercoercion.util;

import io.github.alexanderschuetz97.bettercoercion.api.LuaUserdataBindingFactory;
import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaMethod;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaMethods;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.*;

public class FilteredUserdataFactory<T> implements LuaUserdataBindingFactory<T> {

    private final Map<LuaValue, LuaValue> filterMapping = new LinkedHashMap<>();

    public FilteredUserdataFactory(Map<LuaValue, Collection<MethodAccessor<?>>> data) {
        for (Map.Entry<LuaValue, Collection<MethodAccessor<?>>> entry : data.entrySet()) {
            Collection<MethodAccessor<?>> methods = entry.getValue();
            if (methods.isEmpty()) {
                continue;
            }

            if (methods.size() == 1) {
                filterMapping.put(entry.getKey(), new JavaMethod(methods.iterator().next()));
            }

            filterMapping.put(entry.getKey(), new JavaMethods(methods.toArray(new MethodAccessor[methods.size()])));
        }
    }


    @Override
    public LuaValue bindAsUserdata(T userdata) {
        if (userdata == null) {
            return LuaValue.NIL;
        }
        return new FilteredInstancedUserdata(userdata, bindAsReadOnlyTable(userdata));
    }

    @Override
    public LuaValue bindAsTable(T userdata) {
        LuaTable lt = new LuaTable();
        makeMapping(lt, userdata);
        return lt;
    }

    @Override
    public LuaValue bindAsReadOnlyTable(T userdata) {
        ReadOnlyTable lt = new ReadOnlyTable();
        makeMapping(lt, userdata);
        lt.makeReadOnly();
        return lt;
    }

    @Override
    public LuaValue bindAsModuleLoader(final String module, final T userdata) {
        Objects.requireNonNull(module);
        Objects.requireNonNull(userdata);

        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue env) {
                Globals globals = env.checkglobals();
                LuaTable table = bindAsTable(userdata).checktable();
                if (globals.package_ != null) {
                    globals.package_.setIsLoaded(module, table);
                }
                return table;
            }
        };
    }

    protected void makeMapping(LuaValue table, Object o) {
        if (o == null) {
            return;
        }

        LuaUserdata value = new LuaUserdata(o);
        for (Map.Entry<LuaValue, LuaValue> entry : filterMapping.entrySet()) {
            table.set(entry.getKey(), new FixedFirstParameterFunction(value, entry.getValue()));
        }
    }

    final class FilteredInstancedUserdata extends LuaUserdata {

        private final LuaValue delegate;

        public FilteredInstancedUserdata(Object o, LuaValue delegate) {
            super(o);
            this.delegate = delegate;
        }

        @Override
        public LuaValue get(LuaValue luaValue) {
            return delegate.get(luaValue);
        }

        @Override
        public LuaTable checktable() {
            return delegate.checktable();
        }

        @Override
        public Varargs next(LuaValue k) {
            return delegate.next(k);
        }

        @Override
        public Varargs inext(LuaValue k) {
            return delegate.inext(k);
        }

        @Override
        public boolean istable() {
            return true;
        }
    }
}
