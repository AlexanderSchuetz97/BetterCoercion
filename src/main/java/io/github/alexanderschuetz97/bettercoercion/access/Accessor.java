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

package io.github.alexanderschuetz97.bettercoercion.access;

import io.github.alexanderschuetz97.bettercoercion.access.field.AbstractFieldAccessor;
import io.github.alexanderschuetz97.bettercoercion.access.method.MethodAccessor;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaMethod;
import io.github.alexanderschuetz97.bettercoercion.userdata.generic.JavaMethods;
import org.luaj.vm2.LuaValue;

import java.util.*;

public class Accessor {

    private final Map<LuaValue, MethodAccessor[]> methods;

    private final Map<LuaValue, Map<String, MethodAccessor<?>>> nonStaticBySignature = new HashMap<>();

    private final Map<LuaValue, Collection<MethodAccessor<?>>> nonStaticByName = new HashMap<>();
    private final Map<LuaValue, LuaValue> methodsLua = new HashMap<>();
    private final Map<LuaValue, LuaValue> staticMethods = new HashMap<>();
    private final Map<LuaValue, AbstractFieldAccessor> fields;
    private final Map<LuaValue, AbstractFieldAccessor> staticFields = new HashMap<>();
    private final Map<LuaValue, LuaValue> constructorLua = new HashMap<>();


    private final Map<LuaValue, MethodAccessor[]> constructor;

    public Accessor(Map<LuaValue, MethodAccessor[]> methods, Map<LuaValue, AbstractFieldAccessor> fields, Map<LuaValue, MethodAccessor[]> constructors) {
        this.methods = methods;
        for (Map.Entry<LuaValue, MethodAccessor[]> entry : methods.entrySet()) {
            MethodAccessor[] value = entry.getValue();
            if (value.length == 1) {
                this.methodsLua.put(entry.getKey(), new JavaMethod(value[0]));
            } else {
                this.methodsLua.put(entry.getKey(), new JavaMethods(value));
            }

            if (value[0].isStatic()) {
                if (value.length == 1) {
                    this.staticMethods.put(entry.getKey(), new JavaMethod(value[0]));
                } else {
                    this.staticMethods.put(entry.getKey(), new JavaMethods(value));
                }
            }

            Map<String, MethodAccessor<?>> signatureMapping = new HashMap<>();
            nonStaticBySignature.put(entry.getKey(), signatureMapping);
            for (MethodAccessor<?> accessor : value) {
                signatureMapping.put(accessor.getParameterSignature(), accessor);
            }

            Collection<MethodAccessor<?>> nonStatic = new ArrayList<>();
            for (MethodAccessor accessor : value) {
                if (accessor.isStatic()) {
                    continue;
                }

                nonStatic.add(accessor);
            }

            if (!nonStatic.isEmpty()) {
                nonStaticByName.put(entry.getKey(), nonStatic);
            }
        }

        this.fields = fields;
        for (Map.Entry<LuaValue, AbstractFieldAccessor> entry : fields.entrySet()) {
            if (entry.getValue().isStatic()) {
                this.staticFields.put(entry.getKey(), entry.getValue());
            }
        }

        this.constructor = constructors;
        for (Map.Entry<LuaValue, MethodAccessor[]> entry : constructors.entrySet()) {
            MethodAccessor[] value = entry.getValue();
            if (value.length == 1)  {
                this.constructorLua.put(entry.getKey(), new JavaMethod(value[0]));
            }  else if (value.length > 1) {
                this.constructorLua.put(entry.getKey(), new JavaMethods(value));
            }
        }

    }

    public Map<LuaValue, Map<String, MethodAccessor<?>>> getNonStaticMethodsGroupedBySignature() {
        return nonStaticBySignature;
    }

    public Map<LuaValue, Collection<MethodAccessor<?>>> getNonStaticByName() {
        return nonStaticByName;
    }

    public Map<LuaValue, LuaValue> getStaticMethods() {
        return staticMethods;
    }

    public Map<LuaValue, MethodAccessor[]> getMethods() {
        return methods;
    }

    public Map<LuaValue, LuaValue> getMethodsLua() {
        return methodsLua;
    }

    public Map<LuaValue, LuaValue> getInstancedMethods(Object instance) {
        Map<LuaValue, LuaValue> map = new HashMap<>(staticMethods);

        for (Map.Entry<LuaValue, MethodAccessor[]> entry : methods.entrySet()) {
            MethodAccessor[] value = entry.getValue();
            if (value[0].isStatic()) {
                continue;
            }

            if (value.length == 1) {
                this.methodsLua.put(entry.getKey(), new JavaMethod(value[0]));
            } else {
                this.methodsLua.put(entry.getKey(), new JavaMethods(value));
            }
        }

        //TODO FINISH
        return map;
    }

    public Map<LuaValue, AbstractFieldAccessor> getFields() {
        return fields;
    }

    public Map<LuaValue, AbstractFieldAccessor> getStaticFields() {
        return staticFields;
    }

    public Map<LuaValue, MethodAccessor[]> getConstructors() {
        return constructor;
    }


    public Map<LuaValue, LuaValue> getConstructorsLua() {
        return constructorLua;
    }
}
