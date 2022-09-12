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
package io.github.alexanderschuetz97.bettercoercion.userdata.generic;

import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.util.ReadOnlyTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


public class JavaInstancedMethods extends JavaMethods {

    private final LuaValue instance;


    public JavaInstancedMethods(LuaValue instance, MethodAccessor[] descriptors) {
        super(descriptors);
        this.instance = instance;
    }

    public Varargs invoke(Varargs varargs) {
        return super.invoke(varargsOf(instance, varargs));

    }

    public void initSignatureTable() {
        signatureTable = new ReadOnlyTable();

        for (MethodAccessor accessor : descriptors) {
            signatureTable.set(accessor.getParameterSignature(), new JavaInstancedMethod(instance, accessor));
        }

        signatureTable.makeReadOnly();
    }
}
