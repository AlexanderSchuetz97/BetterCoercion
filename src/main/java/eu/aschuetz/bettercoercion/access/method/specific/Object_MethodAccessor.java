//
// Copyright Alexander Schütz, 2023
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

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;
import eu.aschuetz.bettercoercion.util.ScoreHelper;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Method;

public class Object_MethodAccessor extends AbstractSpecificMethodAccessor {

    private LuaToJavaCoercion<?> param1Cocercion;
    private final Class<?> param1Class;

    public Object_MethodAccessor(Method method) {
        super(method);
        param1Class = method.getParameterTypes()[0];
    }

    @Override
    public void init(LuaCoercion coercion) {
        super.init(coercion);
        param1Cocercion = coercion.getL2JCoercion(param1Class);
    }

    @Override
    public int score() {
        return ScoreHelper.chain(instanceCoercion.score(LuaValue.NIL), param1Cocercion.score(LuaValue.NIL));
    }

    @Override
    public int score(Varargs args) {
        return ScoreHelper.nArgFunction(instanceCoercion.score(args.arg1()), param1Cocercion.score(args.arg(2)), 1, args.narg());
    }

    @Override
    public int score(LuaValue param1) {
        return ScoreHelper.chain(instanceCoercion.score(param1), param1Cocercion.score(LuaValue.NIL));
    }

    @Override
    public int score(LuaValue param1, LuaValue param2) {
        return ScoreHelper.chain(instanceCoercion.score(param1), param1Cocercion.score(param2));
    }

    @Override
    public int score(LuaValue param1, LuaValue param2, LuaValue param3) {
        return ScoreHelper.chain(instanceCoercion.score(param1), param1Cocercion.score(param2), LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION);
    }

    @Override
    public Varargs invoke(Varargs args) {
        Object inst = coerceInstance(args);

        try {
            return coerceResult(args.arg1(), inst, method.invoke(inst, param1Cocercion.coerce2J(args.arg(2), Util.NO_TYPE_MAP)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }


    @Override
    public Varargs invoke() {
        Object inst = coerceInstance(LuaValue.NIL);

        try {
            return coerceResult(LuaValue.NIL, inst, method.invoke(inst, param1Cocercion.coerce2J(LuaValue.NIL, Util.NO_TYPE_MAP)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke(LuaValue param1) {
        Object inst = coerceInstance(param1);

        try {
            return coerceResult(param1, inst, method.invoke(inst, param1Cocercion.coerce2J(LuaValue.NIL, Util.NO_TYPE_MAP)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke(LuaValue param1, LuaValue param2) {
        Object inst = coerceInstance(param1);

        try {
            return coerceResult(param1, inst, method.invoke(inst, param1Cocercion.coerce2J(param2, Util.NO_TYPE_MAP)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }

    @Override
    public Varargs invoke(LuaValue param1, LuaValue param2, LuaValue param3) {
        Object inst = coerceInstance(param1);

        try {
            return coerceResult(param1, inst, method.invoke(inst, param1Cocercion.coerce2J(param2, Util.NO_TYPE_MAP)));
        } catch (Throwable exc) {
            throw Util.wrapException(exc);
        }
    }
}
