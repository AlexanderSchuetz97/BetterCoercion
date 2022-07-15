package io.github.alexanderschuetz97.bettercoercion.util;

import io.github.alexanderschuetz97.bettercoercion.api.LuaToJavaCoercion;

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
