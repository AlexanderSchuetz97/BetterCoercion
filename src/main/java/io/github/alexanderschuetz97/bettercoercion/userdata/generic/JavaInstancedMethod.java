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

public class JavaInstancedMethod extends JavaMethod {

    protected final LuaValue instance;


    public JavaInstancedMethod(LuaValue instance, MethodAccessor accessor) {
        super(accessor);
        this.instance = instance;
    }

    public Varargs invoke(Varargs varargs) {
        return super.invoke(varargsOf(instance, varargs));
    }

    @Override
    public Varargs invoke() {
        return super.invoke(instance);
    }

    @Override
    public Varargs invoke(LuaValue arg, Varargs varargs) {
        return super.invoke(instance, arg, varargs);
    }

    @Override
    public Varargs invoke(LuaValue arg1, LuaValue arg2, Varargs varargs) {
        return super.invoke(instance, varargsOf(arg1, arg2, varargs));
    }

    @Override
    public Varargs invoke(LuaValue[] arrayArgs) {
        return super.invoke(instance, varargsOf(arrayArgs));
    }

    @Override
    public Varargs invoke(LuaValue[] arrayArgs, Varargs varargs) {
        return super.invoke(instance, varargsOf(arrayArgs, varargs));
    }


    @Override
    public LuaValue call() {
        return super.call(instance);
    }

    @Override
    public LuaValue call(LuaValue arg) {
        return super.call(instance, arg);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return super.call(instance, arg1, arg2);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return super.call(instance, arg1, arg2, arg3);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3, LuaValue arg4) {
        return super.invoke(new LuaValue[] {instance, arg1, arg2, arg3, arg4}).arg1();
    }
}
