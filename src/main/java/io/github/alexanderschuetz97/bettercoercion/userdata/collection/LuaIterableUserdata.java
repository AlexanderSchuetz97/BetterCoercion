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
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaInstance;
import io.github.alexanderschuetz97.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.ListIterator;

public class LuaIterableUserdata extends JavaInstance {

    protected final Class type;

    protected final Type[] generics;

    protected LuaValue componentLuaClazz;

    public LuaIterableUserdata(LuaCoercion coercion, Iterable obj) {
        this(coercion, obj, Object.class);
    }


    public LuaIterableUserdata(LuaCoercion coercion, Iterable<?> obj, Type type) {
        super(coercion, obj);
        Util.notNull(type);
        this.type = Util.getTypeClass(type);
        generics = Util.getTypeGenerics(type);
    }

    protected Iterator jiter() {
        return ((Iterable)checkuserdata(Iterable.class)).iterator();
    }

    @Override
    protected LuaValue componentType() {
        if (componentLuaClazz == null) {
            componentLuaClazz = coercion.coerce(type);
        }

        return componentLuaClazz;
    }

    @Override
    public Varargs next(LuaValue key) {
        LuaIterator iter;
        if (key.isnil()) {
            Iterator jiter =  jiter();
            iter = jiter instanceof ListIterator ? new LuaListIterator(coercion, (ListIterator) jiter, type, generics) : new LuaIterator(coercion, jiter, generics);
        } else {
            if (!(key instanceof LuaIterator)) {
                return NIL;
            }

            iter = (LuaIterator) key;
        }

        if (TRUE.eq_b(iter.hasNext())) {
            return varargsOf(iter, iter.next());
        }

        return NIL;
    }

}
