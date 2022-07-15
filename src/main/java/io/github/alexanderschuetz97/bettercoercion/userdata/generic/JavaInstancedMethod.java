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
