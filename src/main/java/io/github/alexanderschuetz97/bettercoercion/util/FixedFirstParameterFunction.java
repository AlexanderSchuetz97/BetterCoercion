package io.github.alexanderschuetz97.bettercoercion.util;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.Objects;

public class FixedFirstParameterFunction extends VarArgFunction {

    private final LuaValue delegate;
    private final LuaValue fixed;

    public FixedFirstParameterFunction(LuaValue fixed, LuaValue delegate) {
        this.fixed = Objects.requireNonNull(fixed);
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public LuaValue call() {
        return delegate.call(fixed);
    }

    @Override
    public LuaValue call(LuaValue arg) {
        return delegate.call(fixed, arg);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return delegate.call(fixed, arg1, arg2);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return delegate.invoke(new LuaValue[]{fixed, arg1, arg2, arg3}).arg1();
    }

    @Override
    public Varargs invoke(Varargs args) {
        return delegate.invoke(varargsOf(fixed, args));
    }

    @Override
    public Varargs invoke() {
        return delegate.invoke(fixed);
    }

    @Override
    public Varargs invoke(LuaValue arg, Varargs varargs) {
        return delegate.invoke(fixed, arg, varargs);
    }

    @Override
    public Varargs invoke(LuaValue arg1, LuaValue arg2, Varargs varargs) {
        return delegate.invoke(varargsOf(fixed, arg1, varargsOf(arg2, varargs)));
    }

    @Override
    public Varargs invoke(LuaValue[] args) {
        return delegate.invoke(fixed, varargsOf(args));
    }

    @Override
    public Varargs invoke(LuaValue[] args, Varargs varargs) {
        return delegate.invoke(fixed, varargsOf(args, varargs));
    }
}
