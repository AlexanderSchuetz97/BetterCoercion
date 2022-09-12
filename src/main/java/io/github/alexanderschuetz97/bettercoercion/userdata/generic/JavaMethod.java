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

public class JavaMethod extends VarArgFunction {

    protected final MethodAccessor accessor;

    public JavaMethod(MethodAccessor accessor) {
        this.accessor = accessor;
    }

    public Varargs invoke(Varargs varargs) {
        if (accessor.score(varargs) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }
        return accessor.invoke(varargs);
    }

    @Override
    public Varargs invoke() {
        if (accessor.score() < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }

        return accessor.invoke();
    }

    @Override
    public Varargs invoke(LuaValue arg, Varargs varargs) {
        Varargs args = varargsOf(arg, varargs);
        if (accessor.score(args) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }


        return accessor.invoke(args);
    }

    @Override
    public Varargs invoke(LuaValue arg1, LuaValue arg2, Varargs varargs) {
        Varargs args = varargsOf(arg1, arg2, varargs);
        if (accessor.score(args) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }


        return accessor.invoke(args);
    }

    @Override
    public Varargs invoke(LuaValue[] arrayArgs) {
        Varargs args = varargsOf(arrayArgs);
        if (accessor.score(args) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }


        return accessor.invoke(args);
    }

    @Override
    public Varargs invoke(LuaValue[] arrayArgs, Varargs varargs) {
        Varargs args = varargsOf(arrayArgs, varargs);
        if (accessor.score(args) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }


        return accessor.invoke(args);
    }


    @Override
    public LuaValue call() {
        if (accessor.score() < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }

        return accessor.invoke().arg1();
    }

    @Override
    public LuaValue call(LuaValue arg) {
        if (accessor.score(arg) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }

        return accessor.invoke(arg).arg1();
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        if (accessor.score(arg1, arg2) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }

        return accessor.invoke(arg1, arg2).arg1();
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        if (accessor.score(arg1, arg2, arg3) < 0) {
            return error("invalid method arguments for " + accessor.getDeclaringClass().getName() + "." +  accessor.getName() + " " + accessor.getParameterSignature());
        }

        return accessor.invoke(arg1, arg2, arg3).arg1();
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4) {
        //THis is defined in LibFunction and just discards arg4 i believe this to be a bug in LuaJ
        return invoke(new LuaValue[] {arg1, arg2, arg3, arg4}).arg1();
    }

    private ReadOnlyTable signatureTable;

    public void initSignatureTable() {
        signatureTable = new ReadOnlyTable();
        signatureTable.set(accessor.getParameterSignature(), this);
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
