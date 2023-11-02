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

package eu.aschuetz.bettercoercion.access.method.generic;

import eu.aschuetz.bettercoercion.api.JavaToLuaCoercion;
import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;
import eu.aschuetz.bettercoercion.coercion.FixedClassBiCoercion;
import eu.aschuetz.bettercoercion.userdata.AbstractInstance;
import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.access.method.MethodAccessor;
import eu.aschuetz.bettercoercion.util.ScoreHelper;
import eu.aschuetz.bettercoercion.util.Util;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractGenericMethodAccessor<X, T> implements MethodAccessor<X> {

    protected abstract Map<Class<?>, Type[]> getGenericReturnTypes();

    public abstract Class<X> getDeclaringClass();

    public abstract Class<X> getInstanceClass();

    public abstract Class<?>[] getParameterTypes();

    protected abstract Map<Class<?>, Type[]>[] getGenericParameterTypes();

    public abstract boolean isVarArgs();

    public abstract int getArgCount();

    public abstract Class<T> getReturnType();

    public abstract String getName();

    public abstract String getParameterSignature();

    public abstract T call0(X instance);

    public abstract T call1(X instance, Object param1);

    public abstract T call2(X instance, Object param1, Object param2);

    public abstract T call3(X instance, Object param1, Object param2, Object param3);

    public abstract T callV(X instance, Object[] params);

    protected LuaCoercion coercion;
    protected LuaToJavaCoercion<Class> clazzCoercion;
    protected LuaToJavaCoercion<X> instanceCoercion;
    protected LuaToJavaCoercion<?>[] paramCoercion;

    @Override
    public boolean isStatic() {
        return getInstanceClass() == null;
    }

    public void init(LuaCoercion coercion) {
        this.coercion = coercion;
        Class<?>[] params = this.getParameterTypes();
        int end = params.length;
        paramCoercion = new LuaToJavaCoercion[params.length];
        if (isVarArgs()) {
            end--;
            paramCoercion[end] = coercion.getL2JCoercion(params[end].getComponentType());
        }

        for (int i = 0; i < end; i++)  {
            paramCoercion[i] = coercion.getL2JCoercion(params[i]);
        }

        clazzCoercion = new FixedClassBiCoercion(coercion, getDeclaringClass());

        Class<X> instanceClass = getInstanceClass();
        if (instanceClass != null) {
            instanceCoercion = coercion.getL2JCoercion(instanceClass);
        } else {
            instanceCoercion = null;
        }
    }

    public int score(Varargs args) {
        int score = instanceCoercion != null ? instanceCoercion.score(args.arg1()) : clazzCoercion.score(args.arg1());
        if (score < 0) {
            return score;
        }

        if (isVarArgs()) {
            for (int i = 0; i < getArgCount()-1; i++) {
                score = paramCoercion[i].score(args.arg(i+2));
                if (score < 0) {
                    return score;
                }
            }

            LuaToJavaCoercion<?> last = paramCoercion[paramCoercion.length-1];

            for (int i = getArgCount() + 2; i <= args.narg(); i++) {
                score = last.score(args.arg(i));
                if (score < 0) {
                    return score;
                }
            }

            return score;
        }


        for (int i = 0; i < getArgCount(); i++) {
            score = paramCoercion[i].score(args.arg(i+2));
            if (score < 0) {
                return score;
            }
        }


        return ScoreHelper.argCountMismatch(score, args.narg() - getArgCount() - 2);
    }

    public int score() {
        return score((Varargs)LuaValue.NONE);
    }

    public int score(LuaValue param1) {
        return score((Varargs)param1);
    }


    public int score(LuaValue param1, LuaValue param2) {
        return score(LuaValue.varargsOf(param1, param2));
    }

    public int score(LuaValue param1, LuaValue param2, LuaValue param3) {
        return score(LuaValue.varargsOf(param1, param2, param3));
    }

    protected Varargs coerceResult(LuaValue instanceValue, X instance, Object object) {
        if (object == null) {
            return LuaValue.NIL;
        }

        if (object == instance && instanceValue instanceof AbstractInstance) {
            return instanceValue;
        }

        JavaToLuaCoercion j2LCoercion = coercion.getJ2LCoercion(object.getClass());
        return j2LCoercion.coerce2V(object, getGenericReturnTypes());
    }

    public Varargs invoke(Varargs args) {
        int n = args.narg();
        switch (n) {
            case(0):
                return invoke();
            case(1):
                return invoke(args.arg1());
            case(2):
                return invoke(args.arg1(), args.arg(2));
            case(3):
                return invoke(args.arg1(), args.arg(2), args.arg(3));
            default:
                break;
        }

        X instance = coerceInstance(args);

        Map<Class<?>, Type[]>[] generics = getGenericParameterTypes();

        if (isVarArgs()) {
            Object[] jargs = new Object[Math.max(getArgCount()-1, n)];
            for (int i = 0; i < paramCoercion.length; i++) {
                jargs[i] = paramCoercion[i].coerce2J(args.arg(i+1), generics[i]);
            }

            Map<Class<?>, Type[]> lastType = generics[generics.length -1];
            LuaToJavaCoercion<?> lastCoercion = paramCoercion[paramCoercion.length-1];
            for (int i = paramCoercion.length; i < n; i++) {
                jargs[i] = lastCoercion.coerce2J(args.arg(i+1), lastType);
            }

            return coerceResult(args.arg1(), instance, callV(instance, jargs));
        }

        Object[] jargs = new Object[getArgCount()];
        for (int i = 0; i < jargs.length; i++) {
            jargs[i] = paramCoercion[i].coerce2J(args.arg(i+1), generics[i]);
        }

        return coerceResult(args.arg1(), instance, callV(instance, jargs));
    }

    public Varargs invoke() {
        return coerceResult(LuaValue.NIL, null, call0(coerceInstance(LuaValue.NONE)));
    }

    public Varargs invoke(LuaValue param1) {
        X inst = coerceInstance(param1);
        return coerceResult(param1, inst, call0(inst));
    }

    public Varargs invoke(LuaValue param1, LuaValue param2) {
        X instance = coerceInstance(param1);
        Map<Class<?>, Type[]>[] generics = getGenericParameterTypes();

        if (getArgCount() > 0) {
            return coerceResult(param1, instance, call1(instance, paramCoercion[0].coerce2J(param2, generics[0])));
        }

        return coerceResult(param1, instance, call0(instance));
    }

    public Varargs invoke(LuaValue param1, LuaValue param2, LuaValue param3) {
        X instance = coerceInstance(param1);
        Map<Class<?>, Type[]>[] generics = getGenericParameterTypes();

        switch (getArgCount()) {
            case(0):
                return coerceResult(param1, instance, call0(instance));
            case(1):
                return coerceResult(param1, instance, call1(instance, paramCoercion[0].coerce2J(param2, generics[0])));
            default:
                return coerceResult(param1, instance, call2(instance, paramCoercion[0].coerce2J(param2, generics[0]), paramCoercion[1].coerce2J(param3, generics[1])));
        }
    }

    public X coerceInstance(Varargs value) {
        if (instanceCoercion != null) {
            X x = instanceCoercion.coerce2J(value.arg1(), Util.NO_TYPE_MAP);
            if (x == null) {
                throw new LuaError("attempt to call " + getName() + " declared in " + getInstanceClass().getName() + " without a instance");
            }

            return x;
        }

        clazzCoercion.coerce2J(value.arg1(), Util.NO_TYPE_MAP);

        return null;
    }




}
