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
import java.lang.reflect.Type;
import java.util.Map;

/**
 * A L2JCoercion converts/transforms LuaValue Objects to Java objects.
 * To register a custom coercion implement this Interface and call
 * {@link LuaCoercion#addL2JCoercion(LuaToJavaCoercion)} with an instance of your implementation.
 *
 * @param <T> The output type from the conversion
 */
public interface LuaToJavaCoercion<T> {

    //Coercion is impossible.
    int COERCION_IMPOSSIBLE = -1;

    //No conversion necessary
    int COERCION_INSTANCE = 1;

    //Conversion without loss of precision and O(1) complexity.
    //int -> long for example.
    int COERCION_CONVERSION = 2;

    //Conversion with loss of precision and O(1) complexity.
    //This would be discarding a argument or passing null/0 for LuaValue.NONE or rounding a double to a float
    int COERCION_CONVERSION_LOSS_OF_PRECISION = 3;

    //Conversion with or without loss of precision and O(n) complexity. (Table -> Array for example)
    int COERCION_CONVERSION_N = 4;

    //Conversion with or without loss of precision and O(n^2) complexity (Table -> 2D Array for example)
    int COERCION_CONVERSION_N_2 = 5;

    /**
     * returns a score of how well this LuaValue fits the desired output java value.
     * negative values indicate that its impossible to convert the LuaValue to the desired type.
     */
    int score(LuaValue value);

    /**
     * returns a score of how well this LuaValue fits the desired output java value.
     * negative values indicate that its impossible to convert the LuaValue to the desired type.
     * if the returned score would greater than 0 then the value of chain+score must be returned.
     */
    int score(LuaValue value, int chain);

    /**
     * Coerce a lua value to a java value.
     *
     * This method may return null.
     * This method must never throw any exceptions except LuaError.
     *
     * The provided generic Type array may contain information of generic typing that the target value needs to have.
     * Since generic typing is not always known due to type erasure this may be an empty array. It is never null.
     * For a List(String) this would be (unless type erasure happened) have the Class java.lang.String at index 0
     * Be aware that if the typing is more complex such as List(List(String)) index 0 has a Object instanceof
     * {@link java.lang.reflect.ParameterizedType} instead of a {@link Class}. The generic types (if present) will always
     * be the generic types of the class returned by getCoercedClass() not always of the instance class itself. For example
     * List coercion coercing a direct instance of X (class X(T,V) implements List(V)) will always only have a V in its generics array.
     * Any info about T is always lost due to type erasure in this example.
     */
    T coerce2J(LuaValue value, Map<Class<?>, Type[]> genericTypes);

    /**
     * The class this coercion is for.
     */
    Class<T> getCoercedClass();
}
