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
package eu.aschuetz.bettercoercion.userdata.generic;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.access.Accessor;
import eu.aschuetz.bettercoercion.access.field.AbstractFieldAccessor;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

public class JavaClass extends JavaInstance {


    private static final LuaValue PREFIX = valueOf("?c");

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
