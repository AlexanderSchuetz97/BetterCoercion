package io.github.alexanderschuetz97.bettercoercion.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * This annotation gives more control over Lua to Java member bindings.
 *
 * This annotation will only have an effect in interface methods when the method
 * {@link LuaType#getBindingFactory(Class, Collection)} is used.
 * Otherwise, this annotation will be ignored on interfaces and interface methods.
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD,  ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaBinding {

    /**
     * should the member be visible.
     * If this is on a class then this will apply to all members in that class and superclasses unless
     * they also have the LuaBinding annotation.
     */
    boolean visible() default true;

    /**
     * name of the lua member, empty string means use default.
     * For fields/methods this is the name of the member in java. for constructors the default value is new
     * This value is ignored on classes
     *
     * Any value that starts with '?' is treated as if it was "".
     *
     * Note: constructor names always take priority over static methods!
     */
    String name() default "";

    /**
     * Creates a reflective alias for the method/field.
     * This can be useful if a field and method have the same name or if several methods have very similar signature
     * and you wish to be specific when calling the method. for example a method with the same name that exists both with a float and double parameter
     * is almost impossible to call from lua because lua numbers will almost always be doubles and so the double method will always be invoked.
     * example: java method: public void blah(float x) and public void blah(double x).
     *
     *
     * For example to get the field if it is reflected use prefix ?f for method use prefix ?m
     */
    boolean reflect() default true;
}
