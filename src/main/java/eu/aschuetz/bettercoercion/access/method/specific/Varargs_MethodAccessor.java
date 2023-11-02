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


package eu.aschuetz.bettercoercion.access.method.specific;

import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;
import eu.aschuetz.bettercoercion.util.ScoreHelper;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Method;

/**
 * for all non static "(Varargs)" methods
 */
public class Varargs_MethodAccessor extends AbstractSpecificMethodAccessor {

    public Varargs_MethodAccessor(Method method) {
        super(method);
    }

    @Override
    public int score() {
        return ScoreHelper.chain(instanceCoercion.score(LuaValue.NIL), LuaToJavaCoercion.COERCION_INSTANCE);
    }

    @Override
    public int score(Varargs args) {
        return ScoreHelper.chain(instanceCoercion.score(args.arg1()), LuaToJavaCoercion.COERCION_INSTANCE);
    }

    @Override
    public int score(LuaValue param1) {
        return ScoreHelper.chain(instanceCoercion.score(param1), LuaToJavaCoercion.COERCION_INSTANCE);
    }

    @Override
    public int score(LuaValue param1, LuaValue param2) {
        return ScoreHelper.chain(instanceCoercion.score(param1), LuaToJavaCoercion.COERCION_INSTANCE);
    }

    @Override
    public int score(LuaValue param1, LuaValue param2, LuaValue param3) {
        return ScoreHelper.chain(instanceCoercion.score(param1), LuaToJavaCoercion.COERCION_INSTANCE);
    }


    @Override
    public Varargs invoke(Varargs args) {
        Object inst = coerceInstance(args);

        try {
            return coerceResult(args.arg1(), inst, method.invoke(inst, args.subargs(2)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke() {
        Object inst = coerceInstance(LuaValue.NONE);
        try {
            return coerceResult(LuaValue.NIL, null, method.invoke(inst, LuaValue.NONE));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke(LuaValue param1) {
        Object inst = coerceInstance(param1);
        try {
            return coerceResult(param1, inst, method.invoke(inst, LuaValue.NONE));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke(LuaValue param1, LuaValue param2) {
        Object inst = coerceInstance(param1);

        try {
            return coerceResult(param1, inst, method.invoke(inst, param2));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke(LuaValue param1, LuaValue param2, LuaValue param3) {
        Object inst = coerceInstance(param1);

        try {
            return coerceResult(param1, inst, method.invoke(inst, LuaValue.varargsOf(param2, param3)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }
}
