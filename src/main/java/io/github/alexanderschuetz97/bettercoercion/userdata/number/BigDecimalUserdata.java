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
package io.github.alexanderschuetz97.bettercoercion.userdata.number;

import io.github.alexanderschuetz97.bettercoercion.api.LuaCoercion;
import io.github.alexanderschuetz97.bettercoercion.math.LuaMathHelper;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class BigDecimalUserdata extends AbstractNumberUserdata<BigDecimal> {



    public BigDecimalUserdata(LuaCoercion coercion, BigDecimal decimal) {
        super(coercion, decimal);
    }

    private static final BigDecimal MAX_INT = BigDecimal.valueOf(Integer.MAX_VALUE);
    private static final BigDecimal MIN_INT = BigDecimal.valueOf(Integer.MIN_VALUE);


    private boolean initIsInt = true;
    private boolean isInt;

    @Override
    public boolean isint() {
        if (initIsInt) {
            try {
                jnumber.intValueExact();
                isInt = true;
                isLong = true;
                initIsLong = false;
            } catch (ArithmeticException exc) {

            }
            initIsInt = false;
        }

        return isInt;
    }

    private boolean initIsLong = true;
    private boolean isLong;

    @Override
    public boolean islong() {
        if (initIsLong) {
            try {
                jnumber.longValueExact();
                isLong = true;
            } catch (ArithmeticException exc) {
                //If its not a long its not gonna be a int.
                initIsInt = false;
            }
            initIsLong = false;
        }

        return isLong;
    }

    @Override
    public BigDecimal tobigdecimal() {
        return jnumber;
    }

    @Override
    public int signum() {
        return jnumber.signum();
    }

    private boolean initCompareToInt = true;
    private boolean isIntOutOfRange;
    //The result if the intValue is equal or if isOutOfRange is true.
    private int intCompareResult;

    @Override
    protected int compareTo(int other) {
        if (isIntOutOfRange) {
            return intCompareResult;
        }

        if (initCompareToInt) {
            initCompareToInt = false;
            if (jnumber.compareTo(MAX_INT) > 0) {
                isIntOutOfRange = true;
                intCompareResult = 1;
                return 1;
            }

            if (jnumber.compareTo(MIN_INT) < 0) {
                isIntOutOfRange = true;
                intCompareResult = -1;
                return -1;
            }

            intCompareResult = jnumber.compareTo(new BigDecimal(toint()));
        }

        int res = Integer.compare(toint(), other);
        if (res == 0) {
            return intCompareResult;
        }

        return res;
    }

    @Override
    protected int compareTo(long other) {
        //TODO
        return compareTo(helper.toBigDecimal(other));
    }


    @Override
    protected int compareTo(double other) {
        if (Double.isNaN(other) || Double.NEGATIVE_INFINITY == other) {
            return 1;
        }

        if (other == Double.POSITIVE_INFINITY) {
            return -1;
        }

        //TODO
        return Double.compare(todouble(), other);
    }


    private boolean initCompareToBigInt = true;
    //Result of comparison if bigint is equal to bigIntForCompare
    private int bigIntCompareResult;
    private BigInteger bigIntForCompare;

    protected int compareTo(BigInteger other) {
        if (initCompareToBigInt) {
            initCompareToBigInt = false;
            bigIntForCompare = jnumber.toBigInteger();
            bigIntCompareResult = jnumber.compareTo(new BigDecimal(bigIntForCompare));
        }

        int res = bigIntForCompare.compareTo((BigInteger) other);
        if (res == 0) {
            return bigIntCompareResult;
        }

        return res;
    }

    @Override
    protected int compareTo(BigDecimal other) {
        return jnumber.compareTo(other);
    }

    private BigDecimal negated;
    @Override
    public LuaValue neg() {
        if (negated == null) {
            negated = jnumber.negate();
        }

        return coercion.coerce( negated);
    }

    @Override
    protected LuaValue add(AbstractNumberUserdata<?> other) {
        return other.add(jnumber);
    }

    @Override
    protected LuaValue add(BigDecimal other) {
        return coercion.coerce(jnumber.add(other));
    }

    @Override
    protected LuaValue add(BigInteger other) {
        return coercion.coerce(jnumber.add(helper.toBigDecimal(other)));
    }


    @Override
    protected LuaValue add(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.add(helper.toBigDecimal(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue add(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.add(helper.toBigDecimal(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue add(int rhs) {
        return coercion.coerce(jnumber.add(helper.toBigDecimal(rhs)));
    }

    @Override
    protected LuaValue sub(AbstractNumberUserdata<?> rhs) {
        return rhs.subFrom(jnumber);
    }

    @Override
    protected LuaValue sub(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.subtract(helper.toBigDecimal(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue sub(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.subtract(helper.toBigDecimal(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue sub(int rhs) {
        return coercion.coerce(jnumber.subtract(helper.toBigDecimal(rhs)));
    }

    @Override
    public LuaValue subFrom(double lhs) {
        switch (helper.investigate(lhs)) {
            case LuaMathHelper.T_POSINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(helper.toBigDecimal(lhs).subtract(jnumber));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue subFrom(int lhs) {
        return coercion.coerce(helper.toBigDecimal(lhs).subtract(jnumber));
    }

    @Override
    protected LuaValue subFrom(BigDecimal lhs) {
        return coercion.coerce(lhs.subtract(jnumber));
    }

    @Override
    protected LuaValue subFrom(BigInteger lhs) {
        return coercion.coerce(helper.toBigDecimal(lhs).subtract(jnumber));
    }

    @Override
    protected LuaValue mul(AbstractNumberUserdata<?> rhs) {
        return rhs.mul(jnumber);
    }

    @Override
    protected LuaValue mul(BigInteger rhs) {
        return coercion.coerce(jnumber.multiply(helper.toBigDecimal(rhs)));
    }

    @Override
    protected LuaValue mul(BigDecimal rhs) {
        return coercion.coerce(jnumber.multiply(rhs));
    }

    @Override
    protected LuaValue mul(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NEGINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.POSINF;
                    case 0:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 1:
                        return LuaDouble.NEGINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(helper.toBigDecimal(rhs).subtract(jnumber));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue mul(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NEGINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.POSINF;
                    case 0:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 1:
                        return LuaDouble.NEGINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(helper.toBigDecimal(rhs).subtract(jnumber));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue mul(int rhs) {
        return coercion.coerce(jnumber.multiply(helper.toBigDecimal(rhs)));
    }

    @Override
    protected LuaValue pow(AbstractNumberUserdata<?> rhs) {
        return rhs.powWith(jnumber);
    }

    protected int powScale(int base, int exponent) {
        return base+exponent+(base*exponent);
    }

    @Override
    protected LuaValue pow(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NEGINF:
                return jnumber.signum() == 0 ? LuaDouble.NAN : coercion.coerce(BigDecimal.ZERO);
            case LuaMathHelper.T_NUMBER:
                BigDecimal decimal = helper.toBigDecimal(rhs);
                return coercion.coerce(helper.pow(jnumber, decimal, powScale(jnumber.scale(), decimal.scale())));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue pow(double rhs) {
        int l = (int) rhs;
        if (l == rhs) {
            return pow(l);
        }

        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NEGINF:
                return jnumber.signum() == 0 ? LuaDouble.NAN : coercion.coerce(BigDecimal.ZERO);
            case LuaMathHelper.T_NUMBER:
                BigDecimal decimal = helper.toBigDecimal(rhs);
                return coercion.coerce(helper.pow(jnumber, decimal, powScale(jnumber.scale(), decimal.scale())));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue pow(int rhs) {
        if (rhs == 0) {
            return jnumber.signum() == 0 ? LuaDouble.NAN : coercion.coerce(BigDecimal.ZERO);
        }

        int abs = Math.abs(rhs);
        if (abs > helper.getMaxPowExponent()) {
            return rhs < 0 ? coercion.coerce(BigDecimal.ZERO) : LuaDouble.POSINF;
        }

        if (jnumber.signum() == 0) {
            return coercion.coerce(BigDecimal.ZERO);
        }

        BigDecimal newDeci = jnumber.pow(abs);
        if (rhs < 0) {
            if (newDeci.signum() == 0) {
                return LuaDouble.POSINF;
            }
            newDeci = BigDecimal.ONE.divide(newDeci, newDeci.scale(), RoundingMode.HALF_EVEN);
        }

        return coercion.coerce(newDeci);
    }

    @Override
    public LuaValue powWith(double lhs) {
        switch (helper.investigate(lhs)) {
            case LuaMathHelper.T_POSINF:
               switch (jnumber.signum()) {
                   case -1:
                       return coercion.coerce(BigDecimal.ZERO);
                   case 0:
                       return LuaDouble.NAN;
                   case 1:
                       return LuaDouble.POSINF;
                   default:
                       return error("signum");
               }
            case LuaMathHelper.T_NEGINF:
                switch (jnumber.signum()) {
                    case -1:
                        return coercion.coerce(BigDecimal.ZERO);
                    case 0:
                        return LuaDouble.NAN;
                    case 1:
                        return LuaDouble.NEGINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NUMBER:
                BigDecimal base = helper.toBigDecimal(lhs);
                return coercion.coerce(helper.pow(base, jnumber, powScale(jnumber.scale(), base.scale())));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue powWith(int lhs) {
        if (Math.abs(lhs) > helper.getMaxPowExponent()) {
            switch (jnumber.signum()) {
                case -1:
                    return coercion.coerce(BigDecimal.ZERO);
                case 0:
                    return ONE;
                case 1:
                    return lhs < 0 ? LuaDouble.NEGINF : LuaDouble.POSINF;
            }
        }

        BigDecimal base = helper.toBigDecimal(lhs);
        return coercion.coerce(helper.pow(base, jnumber, jnumber.scale()));
    }

    @Override
    protected LuaValue powWith(BigInteger lhs) {
        return powWith(helper.toBigDecimal(lhs));
    }

    @Override
    protected LuaValue powWith(BigDecimal lhs) {
        return coercion.coerce(helper.pow(lhs, jnumber, powScale(lhs.scale(), jnumber.scale())));
    }

    @Override
    protected LuaValue div(AbstractNumberUserdata<?> rhs) {
        return rhs.divInto(jnumber);
    }

    @Override
    protected LuaValue div(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
            case LuaMathHelper.T_NEGINF:
                return coercion.coerce(BigDecimal.ZERO);
            case LuaMathHelper.T_NUMBER:
                BigDecimal divident = helper.toBigDecimal(rhs);
                return coercion.coerce(jnumber.divide(divident, Math.max(jnumber.scale(), divident.scale()), RoundingMode.HALF_EVEN));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue div(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_POSINF:
            case LuaMathHelper.T_NEGINF:
                return coercion.coerce(BigDecimal.ZERO);
            case LuaMathHelper.T_NUMBER:
                BigDecimal divident = helper.toBigDecimal(rhs);
                return coercion.coerce(jnumber.divide(divident, Math.max(jnumber.scale(), divident.scale()), RoundingMode.HALF_EVEN));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue div(int rhs) {
        if (rhs == 0) {
            return LuaDouble.NAN;
        }

        BigDecimal divident = helper.toBigDecimal(rhs);
        BigDecimal result = jnumber.divide(divident, Math.max(jnumber.scale(), divident.scale()), RoundingMode.HALF_EVEN);
        return coercion.coerce(result);
    }

    @Override
    public LuaValue divInto(double lhs) {
        switch (helper.investigate(lhs)) {
            case LuaMathHelper.T_POSINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return LuaDouble.NAN;
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NEGINF:
                switch (jnumber.signum()) {
                    case -1:
                        return LuaDouble.POSINF;
                    case 0:
                        return LuaDouble.NAN;
                    case 1:
                        return LuaDouble.NEGINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NUMBER:
                BigDecimal divident = helper.toBigDecimal(lhs);
                return coercion.coerce(divident.divide(jnumber, Math.max(jnumber.scale(), divident.scale()), RoundingMode.HALF_EVEN));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    protected LuaValue divInto(BigInteger lhs) {
        return divInto(helper.toBigDecimal(lhs));
    }

    @Override
    protected LuaValue divInto(BigDecimal lhs) {
        if (jnumber.signum() == 0) {
            return LuaDouble.NAN;
        }

        return coercion.coerce(lhs.divide(jnumber, Math.max(jnumber.scale(), lhs.scale()), RoundingMode.HALF_EVEN));
    }

    @Override
    protected LuaValue mod(AbstractNumberUserdata<?> rhs) {
        return rhs.modFrom(jnumber);
    }

    @Override
    protected LuaValue mod(Number rhs) {
        if (helper.investigate(rhs) != LuaMathHelper.T_NUMBER) {
            return LuaDouble.NAN;
        }
        BigDecimal decimal = helper.toBigDecimal(rhs);
        if (decimal.signum() == 0) {
            return LuaDouble.NAN;
        }
        return coercion.coerce(jnumber.remainder(decimal).abs());
    }

    @Override
    public LuaValue mod(double rhs) {
        if (helper.investigate(rhs) != LuaMathHelper.T_NUMBER) {
            return LuaDouble.NAN;
        }

        BigDecimal decimal = helper.toBigDecimal(rhs);
        if (decimal.signum() == 0) {
            return LuaDouble.NAN;
        }

        return coercion.coerce(jnumber.remainder(decimal).abs());
    }

    @Override
    public LuaValue mod(int rhs) {
        if (rhs == 0) {
            return LuaDouble.NAN;
        }
        return coercion.coerce(jnumber.remainder(helper.toBigDecimal(rhs)));
    }

    @Override
    public LuaValue modFrom(double lhs) {
        if (Double.isNaN(lhs) || Double.isInfinite(lhs)) {
            return LuaDouble.NAN;
        }

        if (jnumber.signum() == 0) {
            return LuaDouble.NAN;
        }

        return coercion.coerce(helper.toBigDecimal(lhs).remainder(jnumber).abs());
    }

    @Override
    public LuaValue modFrom(BigDecimal lhs) {
        if (jnumber.signum() == 0) {
            return LuaDouble.NAN;
        }

        return coercion.coerce(lhs.remainder(jnumber).abs());
    }

    @Override
    public LuaValue modFrom(BigInteger lhs) {
        return modFrom(helper.toBigDecimal(lhs));
    }


}
