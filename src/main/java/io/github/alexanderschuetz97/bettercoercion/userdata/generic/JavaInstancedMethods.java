package io.github.alexanderschuetz97.bettercoercion.userdata.generic;

import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.util.ReadOnlyTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;


public class JavaInstancedMethods extends JavaMethods {

    private final LuaValue instance;


    public JavaInstancedMethods(LuaValue instance, MethodAccessor[] descriptors) {
        super(descriptors);
        this.instance = instance;
    }

    public Varargs invoke(Varargs varargs) {
        return super.invoke(varargsOf(instance, varargs));

    }

    public void initSignatureTable() {
        signatureTable = new ReadOnlyTable();

        for (MethodAccessor accessor : descriptors) {
            signatureTable.set(accessor.getParameterSignature(), new JavaInstancedMethod(instance, accessor));
        }

        signatureTable.makeReadOnly();
    }
}
