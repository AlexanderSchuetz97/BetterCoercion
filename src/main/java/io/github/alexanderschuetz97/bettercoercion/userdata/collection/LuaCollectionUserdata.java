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
package io.github.alexanderschuetz97.bettercoercion.userdata.collection;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.lang.reflect.Type;
import java.util.Collection;

public class LuaCollectionUserdata extends LuaIterableUserdata {

    protected final Collection collection;

    protected final LuaValue ADD_F = new TwoArgFunction() {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            if (!(arg1 instanceof LuaCollectionUserdata)) {
                return error("bad argument: LuaCollectionUserdata expected, got " + arg1.typename());
            }
            return ((LuaCollectionUserdata) arg1).jadd(arg2);
        }
    };



    public LuaCollectionUserdata(LuaCoercion coercion, Collection obj) {
        this(coercion, obj, Object.class);
    }

    public LuaCollectionUserdata(LuaCoercion coercion, Collection<?> obj, Type type) {
        super(coercion, obj, type);
        collection = obj;
    }



    @Override
    public int rawlen() {
        return collection.size();
    }

    @Override
    public LuaValue len()  {
        final LuaValue h = metatag(LEN);
        if (h.toboolean()) {
            return h.call(this);
        }
        return valueOf(rawlen());
    }

    protected static final LuaValue ADD = valueOf("add");

    @Override
    public LuaValue rawget(LuaValue key) {
        if (ADD.eq_b(key)) {
            return ADD_F;
        }

        return super.rawget(key);
    }

    public LuaValue jadd(LuaValue value) {
        Object jobj = coercion.coerce(value, type);
        try {
            collection.add(jobj);
        } catch (Exception exc) {
            throw Util.wrapException(exc);
        }

        return TRUE;
    }
}
