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
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;


public class JavaMethods extends VarArgFunction {

    protected final MethodAccessor[] descriptors;



    public JavaMethods(MethodAccessor[] descriptors) {
        this.descriptors = descriptors;
    }

    public Varargs invoke(Varargs varargs) {
        int maxScore = Integer.MAX_VALUE;
        MethodAccessor best = null;
        for (MethodAccessor accessor : descriptors) {
            int score = accessor.score(varargs);
            if (score < 0) {
                continue;
            }

            if (maxScore > score) {
                best = accessor;
                maxScore = score;
                if (maxScore == 0) {
                    break;
                }
            }
        }

        if (best == null) {
            return error("invalid method arguments for " + descriptors[0].getDeclaringClass().getName() + "." +  descriptors[0].getName() + " " + descriptors.length + " possible methods evaluated");
        }

        return best.invoke(varargs);
    }

    protected ReadOnlyTable signatureTable;

    public void initSignatureTable() {
        signatureTable = new ReadOnlyTable();

        for (MethodAccessor accessor : descriptors) {
            signatureTable.set(accessor.getParameterSignature(), new JavaMethod(accessor));
        }

        signatureTable.makeReadOnly();
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        return checktable().get(key);

    }

    @Override
    public LuaTable checktable() {
        if (signatureTable == null) {
            initSignatureTable();
        }
        return signatureTable;
    }

    @Override
    public boolean istable() {
        return true;
    }

    @Override
    public LuaTable opttable(LuaTable defval) {
        return checktable();
    }

    @Override
    public LuaValue get(LuaValue key) {
        return rawget(key);
    }
}
