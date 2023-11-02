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

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * A J2LCoercion converts/transforms Java Objects to LuaValue/Vararg objects.
 *
 * @param <T> The type of java object to convert to LuaValue/Vararg objects.
 */
public interface JavaToLuaCoercion<T> {

    /**
     * Coerce a java value to a LuaValue.
     * This method must never return null.
     * This method must never throw any exceptions except LuaError.
     * This method must be able to handle a null input value.
     *
     * The provided generic Type array may contain information of generic typing that the target value needs to have.
     * Since generic typing is not always known due to type erasure this may be an empty array. It is never null.
     * For a List(String) this would be (unless type erasure happened) have the Class java.lang.String at index 0
     * Be aware that if the typing is more complex such as List(List(String)) index 0 has a Object instanceof
     * {@link java.lang.reflect.ParameterizedType} instead of a {@link Class}. The generic types (if present) will always
     * be the generic types of the class returned by getCoercedClass() not always of the instance class itself. For example
     * List coercion coercing a direct instance of X (class X(T,V) implements List(V)) will always only have a V in its generics array.
     * Any info about T is always lost due to type erasure in this example.
     *
     */
    LuaValue coerce2L(T value, Map<Class<?>, Type[]> genericTypes);

    /**
     * Coerce a java value to a Varargs.
     * This method must never return null.
     * This method must never throw any exceptions except LuaError.
     * This method must be able to handle a null input value.
     *
     * The provided generic Type array may contain information of generic typing that the target value needs to have.
     * Since generic typing is not always known due to type erasure this may be an empty array. It is never null.
     * For a List(String) this would be (unless type erasure happened) have the Class java.lang.String at index 0
     * Be aware that if the typing is more complex such as List(List(String)) index 0 has an Object instanceof
     * {@link java.lang.reflect.ParameterizedType} instead of a {@link Class}. The generic types (if present) will always
     * be the generic types of the class returned by getCoercedClass() not always of the instance class itself. For example
     * List coercion coercing a direct instance of X (class X(T,V) implements List(V)) will always only have a V in its generics array.
     * Any info about T is always lost due to type erasure in this example.
     *
     */
    Varargs coerce2V(T value, Map<Class<?>, Type[]> genericTypes);

    /**
     * Returns the class type that coerce takes as input.
     * @return
     */
    Class<T> getCoercedClass();

}
