package io.github.alexanderschuetz97.bettercoercion.userdata.generic;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.access.Accessor;
import io.github.alexanderschuetz97.bettercoercion.access.field.AbstractFieldAccessor;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

public class JavaClass extends JavaInstance {


    private static final LuaValue PREFIX = LuaValue.valueOf("?c");

    private final Class<?> clazz;

    public JavaClass(LuaCoercion coercion, Class<?> obj) {
        super(coercion, obj);
        this.clazz = obj;
    }

    @Override
    protected void init() {
        accessors = coercion.getAccessorRegistry().getAccessorsFor(Class.class);
        Accessor childAccessors = coercion.getAccessorRegistry().getAccessorsFor(clazz);
        fields = new HashMap<>();
        methods = new HashMap<>();

        //Statics and constructors...
        fields.putAll(childAccessors.getStaticFields());
        methods.putAll(childAccessors.getStaticMethods());
        methods.putAll(childAccessors.getConstructorsLua());

        //Class methods available under ?c and if not already present as static.
        //if the class has a static method "getName" for example then calling getName() will call that method.
        //but the class name can be retrieved by calling class["?cgetName"]() in this case.
        //should it not have "getName" as a static then you can just call getName. "?cgetName" will always be available.
        for (Map.Entry<LuaValue, LuaValue> v : accessors.getMethodsLua().entrySet()) {
            if (!methods.containsKey(v.getKey())) {
                methods.put(v.getKey(), v.getValue());
            }

            methods.put(PREFIX.concat(v.getKey()), v.getValue());
        }

        for (Map.Entry<LuaValue, AbstractFieldAccessor> v : accessors.getFields().entrySet()) {
            if (!fields.containsKey(v.getKey())) {
                fields.put(v.getKey(), v.getValue());
            }

            fields.put(PREFIX.concat(v.getKey()), v.getValue());
        }


    }

}
