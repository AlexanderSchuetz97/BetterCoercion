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
package eu.aschuetz.bettercoercion.userdata.array;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.api.JavaToLuaCoercion;
import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Array;

public class GenericArray extends AbstractArray {

    private final int length;
    private final JavaToLuaCoercion<Object> j2l;
    private final LuaToJavaCoercion<?> l2j;
    private LuaValue componentLuaClazz;

    public GenericArray(LuaCoercion coercion, Object array) {
        super(coercion, array, array.getClass().getComponentType(), Array.getLength(array));
        this.j2l = (JavaToLuaCoercion) coercion.getJ2LCoercion(componentClazz);
        this.l2j = coercion.getL2JCoercion(componentClazz);
        this.length = Array.getLength(array);
    }


    @Override
    protected void setSlot(int index, LuaValue value) {
        Array.set(m_instance, index, l2j.coerce2J(value, Util.NO_TYPE_MAP));
    }

    @Override
    protected LuaValue getSlot(int index) {
        return j2l.coerce2L(Array.get(m_instance, index), Util.NO_TYPE_MAP);
    }
}
