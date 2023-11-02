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
package eu.aschuetz.bettercoercion.userdata.number;

import eu.aschuetz.bettercoercion.api.LuaCoercion;
import eu.aschuetz.bettercoercion.math.LuaMathHelper;
import eu.aschuetz.bettercoercion.userdata.generic.JavaInstance;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class AbstractNumberUserdata<T extends Number> extends JavaInstance {

    protected final T jnumber;

    protected final LuaMathHelper helper;

    public AbstractNumberUserdata(LuaCoercion coercion, T obj) {
        super(coercion, obj);
        this.helper = coercion.getMathHelper();
        jnumber = obj;
    }

    //MISC
    @Override
    public LuaValue len() {
        return strvalue().len();
    }

    @Override
    public int length() {
        return strvalue().length();
    }

    @Override
    public int rawlen() {
        return strvalue().rawlen();
    }


    //Instanceing/conversion
    @Override
    public boolean isnumber() {
        return true;
    }

    public abstract int signum();

    @Override
    public abstract boolean isint();

    @Override
    public abstract boolean islong();

    public abstract BigDecimal tobigdecimal();

    private boolean initIntValue = true;
    private int intValue;

    @Override
    public int toint() {
        if (initIntValue) {
            initIntValue = false;
            intValue = jnumber.intValue();
        }
        return intValue;
    }

    private boolean initLongValue = true;
    private long longValue;

    @Override
    public long tolong() {
        if (initLongValue) {
            initLongValue = false;
            longValue = jnumber.longValue();
        }
        return longValue;
    }

    private boolean initShortValue = true;
    private short shortValue;
    @Override
    public short toshort() {
        if (initShortValue) {
            initShortValue = false;
            shortValue = jnumber.shortValue();
        }
        return shortValue;
    }

    private boolean initByteValue = true;
    private byte byteValue;

    @Override
    public byte tobyte() {
        if (initByteValue) {
            initByteValue = false;
            byteValue = jnumber.byteValue();
        }
        return byteValue;
    }

    @Override
    public char tochar() {
        return (char) toint();
    }

    private boolean initDoubleValue = true;
    private double doubleValue;
    @Override
    public double todouble() {
        if (initDoubleValue) {
            initDoubleValue = false;
            doubleValue = jnumber.doubleValue();
        }
        return doubleValue;
    }

    private boolean initFloatValue = true;
    private float floatValue;
    @Override
    public float tofloat() {
        if (initFloatValue) {
            initFloatValue = false;
            floatValue = jnumber.floatValue();
        }
        return floatValue;
    }

    @Override
    public int checkint() {
        return toint();
    }

    @Override
    public LuaInteger checkinteger() {
        return LuaInteger.valueOf(toint());
    }

    @Override
    public int optint(int defval) {
        if (isint()) {
            return checkint();
        }
        return defval;
    }

    @Override
    public LuaInteger optinteger(LuaInteger defval) {
        if (isint()) {
            return checkinteger();
        }
        return defval;
    }

    @Override
    public long optlong(long defval) {
        if (islong()) {
            return checklong();
        }
        return defval;
    }

    @Override
    public double checkdouble() {
        return todouble();
    }

    @Override
    public long checklong() {
        return tolong();
    }

    //Comparison (High Level) (works like Comparable)
    // -1 this is smaller than other
    //  0 this is equal to other
    //  1 this is larger than other
    protected abstract int compareTo(int other);
    protected abstract int compareTo(long other);
    protected abstract int compareTo(double other);
    protected abstract int compareTo(BigInteger other);
    protected abstract int compareTo(BigDecimal other);
    protected int compareTo(Number other) {
        if (other instanceof BigDecimal) {
            return compareTo((BigDecimal) other);
        }

        if (other instanceof BigInteger) {
            return compareTo((BigInteger) other);
        }

        if (other instanceof Integer || other instanceof Short || other instanceof Byte) {
            return compareTo(other.intValue());
        }

        if (other instanceof Long) {
            return compareTo(other.longValue());
        }

        return compareTo(other.doubleValue());
    }


    //Comparison (Low Level)

    @Override
    public boolean eq_b(LuaValue val) {
        if (super.eq_b(val)) {
            return true;
        }

        if (val.isinttype()) {
            return compareTo(val.checkint()) == 0;
        }

        if (val.getClass() == LuaDouble.class) {
            return compareTo(val.checkdouble()) == 0;
        }

        if (val.isuserdata()) {
            return compareTo((Number) val.touserdata()) == 0;
        }

        LuaValue num = val.tonumber();

        if (num.isnil()) {
            return false;
        }

        return (num.isint() ? compareTo(num.checkint()) : compareTo(num.checkdouble())) == 0;
    }

    @Override
    public LuaValue eq(LuaValue val) {
        return eq_b(val) ? TRUE : FALSE;
    }

    @Override
    public boolean raweq(double val) {
        return compareTo(val) == 0;
    }

    @Override
    public boolean raweq(int val) {
        return compareTo(val) == 0;
    }

    @Override
    public LuaValue lt(LuaValue rhs) {
        return lt_b(rhs) ? TRUE : FALSE;
    }

    @Override
    public LuaValue lt(double rhs) {
        return compareTo(rhs) == -1 ? TRUE : FALSE;
    }

    @Override
    public LuaValue lt(int rhs) {
        return compareTo(rhs) == -1 ? TRUE : FALSE;
    }

    @Override
    public boolean lt_b(LuaValue rhs) {
        if (rhs.isinttype()) {
            return compareTo(rhs.checkint()) == -1;
        }

        if (rhs.getClass() == LuaDouble.class) {
            return compareTo(rhs.checkdouble()) == -1;
        }

        if (rhs.isuserdata(Number.class)) {
            return compareTo((Number) rhs.touserdata()) == -1;
        }

        LuaValue num = rhs.tonumber();

        if (num.isnil()) {
            return compareerror(rhs).toboolean();
        }

        return (num.isint() ? compareTo(num.checkint()) : compareTo(num.checkdouble())) == -1;
    }

    @Override
    public boolean lt_b(int rhs) {
        return compareTo(rhs) == -1;
    }

    @Override
    public boolean lt_b(double rhs) {
        return compareTo(rhs) == -1;
    }

    @Override
    public LuaValue lteq(LuaValue rhs) {
        return lteq_b(rhs) ? TRUE : FALSE;
    }

    @Override
    public LuaValue lteq(double rhs) {
        return compareTo(rhs) != 1 ? TRUE : FALSE;
    }

    @Override
    public LuaValue lteq(int rhs) {
        return compareTo(rhs) != 1 ? TRUE : FALSE;
    }

    @Override
    public boolean lteq_b(int rhs) {
        return super.lteq_b(rhs);
    }

    @Override
    public boolean lteq_b(double rhs) {
        return super.lteq_b(rhs);
    }

    @Override
    public boolean lteq_b(LuaValue rhs) {
        if (rhs.isinttype()) {
            return compareTo(rhs.checkint()) != 1;
        }

        if (rhs.getClass() == LuaDouble.class) {
            return compareTo(rhs.checkdouble()) != 1;
        }

        if (rhs.isuserdata(Number.class)) {
            return compareTo((Number) rhs.touserdata()) != 1;
        }

        LuaValue num = rhs.tonumber();

        if (num.isnil()) {
            return compareerror(rhs).toboolean();
        }

        return (num.isint() ? compareTo(num.checkint()) : compareTo(num.checkdouble())) != 1;
    }

    @Override
    public LuaValue gt(LuaValue rhs) {
        return gt_b(rhs) ? TRUE : FALSE;
    }

    @Override
    public LuaValue gt(double rhs) {
        return compareTo(rhs) == 1 ? TRUE : FALSE;
    }

    @Override
    public LuaValue gt(int rhs) {
        return compareTo(rhs) == 1 ? TRUE : FALSE;
    }

    @Override
    public boolean gt_b(int rhs) {
        return compareTo(rhs) == 1;
    }

    @Override
    public boolean gt_b(double rhs) {
        return compareTo(rhs) == 1;
    }

    @Override
    public boolean gt_b(LuaValue rhs) {
        if (rhs.isinttype()) {
            return compareTo(rhs.checkint()) == 1;
        }

        if (rhs.getClass() == LuaDouble.class) {
            return compareTo(rhs.checkdouble()) == 1;
        }

        if (rhs.isuserdata(Number.class)) {
            return compareTo((Number) rhs.touserdata()) == 1;
        }

        LuaValue num = rhs.tonumber();

        if (num.isnil()) {
            return compareerror(rhs).toboolean();
        }

        return (num.isint() ? compareTo(num.checkint()) : compareTo(num.checkdouble())) == 1;

    }

    @Override
    public LuaValue gteq(LuaValue rhs) {
        return gteq_b(rhs) ? TRUE : FALSE;
    }

    @Override
    public LuaValue gteq(double rhs) {
        return compareTo(rhs) != -1 ? TRUE : FALSE;
    }

    @Override
    public LuaValue gteq(int rhs) {
        return compareTo(rhs) != -1 ? TRUE : FALSE;
    }

    @Override
    public boolean gteq_b(int rhs) {
        return compareTo(rhs) != -1;
    }

    @Override
    public boolean gteq_b(double rhs) {
        return compareTo(rhs) != -1;
    }

    @Override
    public boolean gteq_b(LuaValue rhs) {
        return super.gteq_b(rhs);
    }

    @Override
    public boolean equals(Object val) {
        if (val == null) {
            return false;
        }

        if (val instanceof LuaValue) {
            return eq_b((LuaValue)val);
        }

        if (val instanceof Number) {
            return compareTo((Number) val) == 0;
        }

        return super.equals(val);
    }



    //Arithmetic operations.
    @Override
    public abstract LuaValue neg();

    @Override
    public LuaValue add(LuaValue rhs) {
        if (rhs.type() == TNUMBER) {
            if (rhs.isinttype()) {
                return add(rhs.checkint());
            }

            if (rhs.getClass() == LuaDouble.class) {
                return add(rhs.checkdouble());
            }
        } else if (rhs instanceof AbstractNumberUserdata) {
            return add((AbstractNumberUserdata<?>) rhs);
        }

        if (rhs.isuserdata(Number.class)) {
            return add((Number) rhs.checkuserdata());
        }

        return isint() ? rhs.add(toint()) : rhs.add(todouble());
    }

    protected abstract LuaValue add(AbstractNumberUserdata<?> rhs);

    protected abstract LuaValue add(BigDecimal rhs);

    protected abstract LuaValue add(BigInteger rhs);

    protected abstract LuaValue add(Number rhs);

    @Override
    public abstract LuaValue add(double rhs);

    @Override
    public abstract LuaValue add(int rhs);

    @Override
    public LuaValue sub(LuaValue rhs) {
        if (rhs.type() == TNUMBER) {
            if (rhs.isinttype()) {
                return sub(rhs.checkint());
            }

            if (rhs.getClass() == LuaDouble.class) {
                return sub(rhs.checkdouble());
            }
        } else if (rhs instanceof AbstractNumberUserdata) {
            return sub((AbstractNumberUserdata<?>) rhs);
        }

        if (rhs.isuserdata(Number.class)) {
            return sub((Number) rhs.checkuserdata());
        }

        return isint() ? rhs.subFrom(toint()) : rhs.subFrom(todouble());
    }

    protected abstract LuaValue sub(AbstractNumberUserdata<?> rhs);

    protected abstract LuaValue sub(Number rhs);

    @Override
    public abstract LuaValue sub(double rhs);

    @Override
    public abstract LuaValue sub(int rhs);

    @Override
    public abstract LuaValue subFrom(double lhs);

    @Override
    public abstract LuaValue subFrom(int lhs);

    protected abstract LuaValue subFrom(BigDecimal lhs);

    protected abstract LuaValue subFrom(BigInteger lhs);

    @Override
    public LuaValue mul(LuaValue rhs) {
        if (rhs.type() == TNUMBER) {
            if (rhs.isinttype()) {
                return mul(rhs.checkint());
            }

            if (rhs.getClass() == LuaDouble.class) {
                return mul(rhs.checkdouble());
            }
        } else if (rhs instanceof AbstractNumberUserdata) {
            return mul((AbstractNumberUserdata<?>) rhs);
        }

        if (rhs.isuserdata(Number.class)) {
            return mul((Number) rhs.checkuserdata());
        }

        return isint() ? rhs.subFrom(toint()) : rhs.subFrom(todouble());
    }

    protected abstract LuaValue mul(AbstractNumberUserdata<?> rhs);

    protected abstract LuaValue mul(BigInteger rhs);

    protected abstract LuaValue mul(BigDecimal rhs);

    protected abstract LuaValue mul(Number rhs);

    @Override
    public abstract LuaValue mul(double rhs);

    @Override
    public abstract LuaValue mul(int rhs);

    @Override
    public LuaValue pow(LuaValue rhs) {
        if (rhs.type() == TNUMBER) {
            if (rhs.isinttype()) {
                return pow(rhs.checkint());
            }

            if (rhs.getClass() == LuaDouble.class) {
                return pow(rhs.checkdouble());
            }
        } else if (rhs instanceof AbstractNumberUserdata) {
            return pow((AbstractNumberUserdata<?>) rhs);
        }


        if (rhs.isuserdata(Number.class)) {
            return pow((Number) rhs.checkuserdata());
        }

        return isint() ? rhs.powWith(toint()) : rhs.powWith(todouble());
    }

    protected abstract LuaValue pow(AbstractNumberUserdata<?> rhs);

    protected abstract LuaValue pow(Number rhs);

    @Override
    public abstract LuaValue pow(double rhs);

    @Override
    public abstract LuaValue pow(int rhs);

    @Override
    public abstract LuaValue powWith(double lhs);

    @Override
    public abstract LuaValue powWith(int lhs);

    protected abstract LuaValue powWith(BigInteger lhs);

    protected abstract LuaValue powWith(BigDecimal lhs);


    @Override
    public LuaValue div(LuaValue rhs) {
        if (rhs.type() == TNUMBER) {
            if (rhs.isinttype()) {
                return div(rhs.checkint());
            }

            if (rhs.getClass() == LuaDouble.class) {
                return div(rhs.checkdouble());
            }
        } else if (rhs instanceof AbstractNumberUserdata) {
            return div((AbstractNumberUserdata<?>) rhs);
        }

        if (rhs.isuserdata(Number.class)) {
            return div((Number) rhs.checkuserdata());
        }

        return isint() ? rhs.powWith(toint()) : rhs.powWith(todouble());
    }

    protected abstract LuaValue div(AbstractNumberUserdata<?> rhs);

    protected abstract LuaValue div(Number rhs);

    @Override
    public abstract LuaValue div(double rhs);

    @Override
    public abstract LuaValue div(int rhs);

    @Override
    public abstract LuaValue divInto(double lhs);

    protected abstract LuaValue divInto(BigInteger lhs);

    protected abstract LuaValue divInto(BigDecimal lhs);

    @Override
    public LuaValue mod(LuaValue rhs) {
        if (rhs.type() == TNUMBER) {
            if (rhs.isinttype()) {
                return mod(rhs.checkint());
            }

            if (rhs.getClass() == LuaDouble.class) {
                return mod(rhs.checkdouble());
            }
        } else if (rhs instanceof AbstractNumberUserdata) {
            return mod((AbstractNumberUserdata<?>) rhs);
        }

        if (rhs.isuserdata(Number.class)) {
            return mod((Number) rhs.checkuserdata());
        }

        return isint() ? rhs.modFrom(toint()) : rhs.modFrom(todouble());
    }

    protected abstract LuaValue mod(AbstractNumberUserdata<?> rhs);

    protected abstract LuaValue mod(Number rhs);

    @Override
    public abstract LuaValue mod(double rhs);

    @Override
    public abstract LuaValue mod(int rhs);

    @Override
    public abstract LuaValue modFrom(double lhs);


    public abstract LuaValue modFrom(BigDecimal lhs);

    public abstract LuaValue modFrom(BigInteger lhs);

}
