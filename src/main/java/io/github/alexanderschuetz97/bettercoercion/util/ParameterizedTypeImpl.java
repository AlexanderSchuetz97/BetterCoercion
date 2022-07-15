package io.github.alexanderschuetz97.bettercoercion.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {

    private final Type type;
    private final Type[] typeArguments;

    public ParameterizedTypeImpl(Type type, Type[] typeArguments) {
        this.type = type;
        this.typeArguments = typeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Type getRawType() {
        return type;
    }

    @Override
    public Type getOwnerType() {
        return Util.getTypeClass(getRawType()).getDeclaringClass();
    }
}
