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


package eu.aschuetz.bettercoercion.access.method;

import java.lang.reflect.Method;

/**
 * A factory for a fast accessor to a java method.
 * This class will create accessors for a given parameter signature (as indicated by the parameters()) method
 * and will create a method accessor for every method that matches these parameters.
 */
public interface MethodAccessorFactory {

    /**
     * Does this factory make accessors for static methods?
     */
    boolean isStatic();

    /**
     * Does this factory make accessors for varargs methods?
     */
    //Currently not implemented -> must return false
    boolean isVarArgs();

    Class<?>[] parameters();

    /**
     * Create a method where method.getTypeParameters is guaranteed equals to this.parameters().<br>
     * Method is guaranteed to be accessible.<br>
     * Method is guaranteed to have the static/not static modified according to this.isStatic()<br>
     * Method is guaranteed to be varargs or not varargs according to this.isVarArgs()<br>
     * !MUST NEVER RETURN NULL OR THROW A EXCEPTION!
     */
    MethodAccessor create(Method method);

    /**
     * Convenience abstract class for static methods
     */
    abstract class StaticMethodAccessorFactory implements MethodAccessorFactory {

        private final Class[] clazz;

        public StaticMethodAccessorFactory(Class... classes) {
            this.clazz = classes;
        }

        @Override
        public boolean isStatic() {
            return true;
        }

        @Override
        public boolean isVarArgs() {
            return false;
        }

        @Override
        public Class<?>[] parameters() {
            return clazz;
        }

        @Override
        public abstract MethodAccessor create(Method method);
    }

    /**
     * Convenience abstract class for instance methods
     */
    abstract class InstanceMethodAccessorFactory implements MethodAccessorFactory {

        private final Class[] clazz;

        public InstanceMethodAccessorFactory(Class... classes) {
            this.clazz = classes;
        }

        @Override
        public boolean isVarArgs() {
            return false;
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public Class<?>[] parameters() {
            return clazz;
        }

        @Override
        public abstract MethodAccessor create(Method method);
    }
}
