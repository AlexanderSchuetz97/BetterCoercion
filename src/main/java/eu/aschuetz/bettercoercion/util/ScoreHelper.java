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
package eu.aschuetz.bettercoercion.util;

import eu.aschuetz.bettercoercion.api.LuaToJavaCoercion;

public class ScoreHelper {

    public static int zeroArgFunction(int args) {
        if (args < 0) {
            return LuaToJavaCoercion.COERCION_IMPOSSIBLE;
        }

        if (args == 0) {
            return LuaToJavaCoercion.COERCION_INSTANCE;
        }

        return LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION * args;
    }

    public static int nArgFunction(int instance, int n, int args) {
        if (instance < 0) {
            return instance;
        }

        args--; //First arg is instance

        if (args < 0) {
            args = 0;
        }

        if (n < args) {
            return instance + (LuaToJavaCoercion.COERCION_INSTANCE * n + ((args-n) * LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION));
        }

        return instance + (LuaToJavaCoercion.COERCION_INSTANCE * args + ((n-args) * LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION));
    }

    public static int nArgFunction(int instance, int arg2, int n, int args) {
        if (instance < 0 || arg2 < 0) {
            return LuaToJavaCoercion.COERCION_IMPOSSIBLE;
        }

        args-=2; //First arg is instance second arg is taken

        if (args < 0) {
            args = 0;
        }

        if (n < args) {
            return instance + arg2 + (LuaToJavaCoercion.COERCION_INSTANCE * n + ((args-n) * LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION));
        }

        return instance + arg2 + (LuaToJavaCoercion.COERCION_INSTANCE * args + ((n-args) * LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION));
    }

    public static int argCountMismatch(int score, int mismatchedArgCount) {
        if (score < 0 || mismatchedArgCount < 0) {
            return score;
        }

        return score + LuaToJavaCoercion.COERCION_CONVERSION_LOSS_OF_PRECISION * mismatchedArgCount;
    }

    public static int chain(int score, int next) {
        if (score < 0 || next < 0) {
            return LuaToJavaCoercion.COERCION_IMPOSSIBLE;
        }

        return score + next;
    }

    public static int chain(int score, int next, int next2) {
        if (score < 0 || next < 0 || next2 < 0) {
            return LuaToJavaCoercion.COERCION_IMPOSSIBLE;
        }

        return score + next + next2;
    }

    public static int chain(int score, int next, int next2, int next3) {
        if (score < 0 || next < 0 || next2 < 0 || next3 < 0) {
            return LuaToJavaCoercion.COERCION_IMPOSSIBLE;
        }

        return score + next + next2 + next3;
    }
}
