package io.github.alexanderschuetz97.bettercoercion.userdata.generic;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.util.FixedFirstParameterFunction;
import org.luaj.vm2.LuaValue;

import java.util.LinkedHashMap;
import java.util.Map;

public class BoundJavaInstance extends JavaInstance {

    public BoundJavaInstance(LuaCoercion coercion, Object obj) {
        super(coercion, obj);
    }

    protected void init() {
        super.init();
        Map<LuaValue, LuaValue> bound = new LinkedHashMap<>();
        for (Map.Entry<LuaValue, LuaValue> e : methods.entrySet()) {
            bound.put(e.getKey(), new FixedFirstParameterFunction(this, e.getValue()));
        }

        methods = bound;
    }


}
