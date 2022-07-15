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


package io.github.alexanderschuetz97.bettercoercion.access.method;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public interface MethodAccessor<X> {

    String getName();

    /**
     * Returns the method parameter signature.
     * ex: "public long blah(String x, int y)"
     * would return
     * "Ljava/lang/String;I"
     *
     * This is unique for all methods in declaring with the same name
     */
    String getParameterSignature();


    boolean isStatic();

    Class<X> getDeclaringClass();

    Class<?> getReturnType();

    int score();

    int score(Varargs args);

    int score(LuaValue param1);

    int score(LuaValue param1, LuaValue param2);

    int score(LuaValue param1, LuaValue param2, LuaValue param3);

    void init(LuaCoercion coercion);

    Varargs invoke(Varargs args);

    Varargs invoke();

    Varargs invoke(LuaValue param1);

    Varargs invoke(LuaValue param1, LuaValue param2);

    Varargs invoke(LuaValue param1, LuaValue param2, LuaValue param3);


}
