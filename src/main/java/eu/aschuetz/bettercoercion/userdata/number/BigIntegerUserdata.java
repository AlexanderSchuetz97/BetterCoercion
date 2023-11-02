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
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class BigIntegerUserdata extends AbstractNumberUserdata<BigInteger> {



    public BigIntegerUserdata(LuaCoercion coercion, BigInteger obj) {
        super(coercion, obj);
    }

    @Override
    public boolean isint() {
        return jnumber.bitLength() <= 32;
    }

    @Override
    public boolean islong() {
        return jnumber.bitLength() <= 64;
    }

    private BigDecimal decimalValue;

    @Override
    public BigDecimal tobigdecimal() {
        if (decimalValue == null) {
            decimalValue = new BigDecimal(jnumber);
        }

        return decimalValue;
    }

    @Override
    public int signum() {
        return jnumber.signum();
    }

    @Override
    protected int compareTo(int other) {
        if (!isint()) {
            return jnumber.signum();
        }

        return Integer.compare(toint(), other);
    }

    @Override
    protected int compareTo(long other) {
        if (!islong()) {
            return jnumber.signum();
        }

        return Long.compare(tolong(), other);
    }

    @Override
    protected int compareTo(double other) {
        return Double.compare(todouble(), other);
    }

    @Override
    protected int compareTo(BigInteger other) {
        return jnumber.compareTo(other);
    }

    @Override
    protected int compareTo(BigDecimal other) {
        return tobigdecimal().compareTo(other);
    }

    @Override
    public LuaValue neg() {
        return coercion.coerce(jnumber.negate());
    }

    @Override
    protected LuaValue add(AbstractNumberUserdata<?> rhs) {
        return rhs.add(jnumber);
    }

    @Override
    protected LuaValue add(BigDecimal rhs) {
        return coercion.coerce(tobigdecimal().add(rhs));
    }

    @Override
    protected LuaValue add(BigInteger rhs) {
        return coercion.coerce(jnumber.add(rhs));
    }

    @Override
    protected LuaValue add(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_POSINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.add(helper.toBigInteger(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue add(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_POSINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NUMBER:
                long l = (long) rhs;
                if (l == rhs) {
                    return coercion.coerce(jnumber.add(helper.toBigInteger(rhs)));
                }
                return coercion.coerce(tobigdecimal().add(helper.toBigDecimal(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue add(int rhs) {
        return coercion.coerce(jnumber.add(helper.toBigInteger(rhs)));
    }

    @Override
    protected LuaValue sub(AbstractNumberUserdata<?> rhs) {
        return rhs.subFrom(jnumber);
    }

    @Override
    protected LuaValue sub(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_POSINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.subtract(helper.toBigInteger(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue sub(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_POSINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_NUMBER:
                long l = (long) rhs;
                if (l == rhs) {
                    return coercion.coerce(jnumber.subtract(helper.toBigInteger(rhs)));
                }
                return coercion.coerce(tobigdecimal().subtract(helper.toBigDecimal(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue sub(int rhs) {
        return coercion.coerce(jnumber.subtract(helper.toBigInteger(rhs)));
    }

    @Override
    public LuaValue subFrom(double lhs) {
        switch (helper.investigate(lhs)) {
            case LuaMathHelper.T_NEGINF:
                return LuaDouble.NEGINF;
            case LuaMathHelper.T_POSINF:
                return LuaDouble.POSINF;
            case LuaMathHelper.T_NUMBER:
                long l = (long) lhs;
                if (l == lhs) {
                    return coercion.coerce(helper.toBigInteger(l).subtract(jnumber));
                }
                return coercion.coerce(helper.toBigDecimal(lhs).subtract(tobigdecimal()));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue subFrom(int lhs) {
        return coercion.coerce(helper.toBigInteger(lhs).subtract(jnumber));
    }

    @Override
    protected LuaValue subFrom(BigDecimal lhs) {
        return coercion.coerce(lhs.subtract(tobigdecimal()));
    }

    @Override
    protected LuaValue subFrom(BigInteger lhs) {
        return coercion.coerce(lhs.subtract(jnumber));
    }

    @Override
    protected LuaValue mul(AbstractNumberUserdata<?> rhs) {
        return rhs.mul(jnumber);
    }

    @Override
    protected LuaValue mul(BigInteger rhs) {
        return coercion.coerce(jnumber.multiply(rhs));
    }

    @Override
    protected LuaValue mul(BigDecimal rhs) {
        return coercion.coerce(tobigdecimal().multiply(rhs));
    }

    @Override
    protected LuaValue mul(Number rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_NEGINF:
                switch (signum()) {
                    case -1:
                        return LuaDouble.POSINF;
                    case 0:
                        return coercion.coerce(BigInteger.ZERO);
                    case 1:
                        return LuaDouble.NEGINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_POSINF:
                switch (signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return coercion.coerce(BigInteger.ZERO);
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.multiply(helper.toBigInteger(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue mul(double rhs) {
        switch (helper.investigate(rhs)) {
            case LuaMathHelper.T_NEGINF:
                switch (signum()) {
                    case -1:
                        return LuaDouble.POSINF;
                    case 0:
                        return coercion.coerce(BigInteger.ZERO);
                    case 1:
                        return LuaDouble.NEGINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_POSINF:
                switch (signum()) {
                    case -1:
                        return LuaDouble.NEGINF;
                    case 0:
                        return coercion.coerce(BigInteger.ZERO);
                    case 1:
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            case LuaMathHelper.T_NUMBER:
                return coercion.coerce(jnumber.multiply(helper.toBigInteger(rhs)));
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue mul(int rhs) {
        return coercion.coerce(jnumber.multiply(helper.toBigInteger(rhs)));
    }

    @Override
    protected LuaValue pow(AbstractNumberUserdata<?> rhs) {
        return rhs.pow(jnumber);
    }

    @Override
    protected LuaValue pow(Number rhs) {
        if (rhs.doubleValue() == rhs.intValue()) {
            return pow(rhs.intValue());
        }

        return new BigDecimalUserdata(coercion, tobigdecimal()).pow(rhs);
    }

    @Override
    public LuaValue pow(double rhs) {
        int ival = (int) rhs;
        if (ival == rhs) {
            return pow(ival);
        }

        return new BigDecimalUserdata(coercion, tobigdecimal()).pow(rhs);
    }

    @Override
    public LuaValue pow(int rhs) {
        if (signum() == 0 && 0 == rhs) {
            return LuaDouble.NAN;
        }

        if (rhs < 0) {
            BigDecimal decimal = BigDecimal.ONE.divide(new BigDecimal(jnumber.pow(Math.abs(rhs))), 32, RoundingMode.HALF_EVEN).stripTrailingZeros();
            if (decimal.scale() < 0) {
                decimal = decimal.setScale(0, RoundingMode.UNNECESSARY);
            }
            return coercion.coerce(decimal);
        }

        return coercion.coerce(jnumber.pow(rhs));
    }

    @Override
    public LuaValue powWith(double lhs) {
        if (!isint()) {
            switch (helper.investigate(lhs)) {
                case LuaMathHelper.T_NAN:
                    return LuaDouble.NAN;
                case LuaMathHelper.T_NEGINF:
                    return coercion.coerce(BigInteger.ZERO);
                case LuaMathHelper.T_POSINF:
                    return LuaDouble.NAN;
                case LuaMathHelper.T_NUMBER:

                    return LuaDouble.POSINF;
            }
        }
        return error("not implemented");
    }

    @Override
    public LuaValue powWith(int lhs) {
        if (!isint()) {
            if (jnumber.signum() == 1) {
                switch (Integer.signum(lhs)) {
                    case -1:
                        return LuaDouble.NAN;
                    case 0:
                        return coercion.coerce(BigInteger.ZERO);
                    case 1:
                        if (lhs == 1) {
                            return coercion.coerce(BigInteger.ONE);
                        }
                        return LuaDouble.POSINF;
                    default:
                        return error("signum");
                }
            }

            switch (Integer.signum(lhs)) {
                case -1:
                    if (lhs == -1) {
                        return LuaDouble.NAN;
                    }
                    return coercion.coerce(BigInteger.ZERO);
                case 0:
                    return LuaDouble.NAN;
                case 1:
                    if (lhs == 1) {
                        return LuaDouble.NAN;
                    }
                    return coercion.coerce(BigInteger.ZERO);
                default:
                    return error("signum");
            }
        }

        if (jnumber.signum() == -1) {
            return coercion.coerce(helper.pow(helper.toBigDecimal(lhs),tobigdecimal(),32));
        }

        return coercion.coerce(helper.toBigInteger(lhs).pow(toint()));
    }

    @Override
    protected LuaValue powWith(BigInteger lhs) {
        if (lhs.bitLength() <= 32) {
            return powWith(lhs.intValue());
        }

        return coercion.coerce(helper.pow(helper.toBigDecimal(lhs), tobigdecimal(), jnumber.signum() == -1 ? 32 : 0));
    }

    @Override
    protected LuaValue powWith(BigDecimal lhs) {
        return coercion.coerce(helper.pow(lhs, tobigdecimal(), lhs.scale()));
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
                BigDecimal dividend = helper.toBigDecimal(rhs);
                BigDecimal divisor = tobigdecimal();
                BigDecimal result = divisor.divide(dividend, Math.max(32, dividend.scale()), RoundingMode.HALF_EVEN).stripTrailingZeros();
                if (result.scale() <= 0) {
                    return coercion.coerce(helper.toBigInteger(result));
                }

                return coercion.coerce(result);
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
                BigDecimal dividend = helper.toBigDecimal(rhs);
                BigDecimal divisor = tobigdecimal();
                BigDecimal result = divisor.divide(dividend, Math.max(32, dividend.scale()), RoundingMode.HALF_EVEN).stripTrailingZeros();
                if (result.scale() <= 0) {
                    return coercion.coerce(helper.toBigInteger(result));
                }

                return coercion.coerce(result);
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    public LuaValue div(int rhs) {
        BigDecimal dividend = helper.toBigDecimal(rhs);
        BigDecimal divisor = tobigdecimal();
        BigDecimal result = divisor.divide(dividend, 32, RoundingMode.HALF_EVEN).stripTrailingZeros();
        if (result.scale() <= 0) {
            return coercion.coerce(helper.toBigInteger(result));
        }

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
                BigDecimal dividend = helper.toBigDecimal(lhs);
                BigDecimal divisor = tobigdecimal();
                BigDecimal result = dividend.divide(divisor, Math.max(32, dividend.scale()), RoundingMode.HALF_EVEN).stripTrailingZeros();
                if (result.scale() <= 0) {
                    return coercion.coerce(helper.toBigInteger(result));
                }

                return coercion.coerce(result);
            default:
                return LuaDouble.NAN;
        }
    }

    @Override
    protected LuaValue divInto(BigInteger lhs) {
        BigDecimal divident = helper.toBigDecimal(lhs);
        BigDecimal divisor = tobigdecimal();
        BigDecimal result = divident.divide(divisor, 32, RoundingMode.HALF_EVEN).stripTrailingZeros();

        if (result.scale() <= 0) {
            return coercion.coerce(helper.toBigInteger(result));
        }

        return coercion.coerce(result);
    }

    @Override
    protected LuaValue divInto(BigDecimal lhs) {
        BigDecimal divisor = tobigdecimal();
        BigDecimal result = lhs.divide(divisor, Math.max(lhs.scale(), 32), RoundingMode.HALF_EVEN).stripTrailingZeros();

        if (result.scale() <= 0) {
            return coercion.coerce(helper.toBigInteger(result));
        }

        return coercion.coerce(result);
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

        if (helper.hasFraction(rhs)) {
            BigDecimal decimal = helper.toBigDecimal(rhs);
            if (decimal.signum() == 0) {
                return LuaDouble.NAN;
            }

            return coercion.coerce(tobigdecimal().remainder(decimal).abs());
        }

        BigInteger integer = helper.toBigInteger(rhs);
        return coercion.coerce(jnumber.remainder(integer).abs());
    }

    @Override
    public LuaValue mod(double rhs) {
        if (helper.investigate(rhs) != LuaMathHelper.T_NUMBER) {
            return LuaDouble.NAN;
        }

        if (jnumber.signum() == 0) {
            return LuaDouble.NAN;
        }

        if (helper.hasFraction(rhs)) {
            BigDecimal decimal = helper.toBigDecimal(rhs);
            if (decimal.signum() == 0) {
                return LuaDouble.NAN;
            }

            return coercion.coerce(tobigdecimal().remainder(decimal).abs());
        }

        BigInteger integer = helper.toBigInteger(rhs);
        return coercion.coerce(jnumber.remainder(integer).abs());
    }

    @Override
    public LuaValue mod(int rhs) {
        if (rhs == 0 || jnumber.signum() == 0) {
            return LuaDouble.NAN;
        }

        BigInteger integer = helper.toBigInteger(rhs);
        return coercion.coerce(jnumber.remainder(integer).abs());
    }

    @Override
    public LuaValue modFrom(double lhs) {
        if (Double.isNaN(lhs) || Double.isInfinite(lhs)) {
            return LuaDouble.NAN;
        }

        if (jnumber.signum() == 0) {
            return LuaDouble.NAN;
        }

        if (helper.hasFraction(lhs)) {
            BigDecimal decimal = helper.toBigDecimal(lhs);
            BigDecimal result = decimal.remainder(tobigdecimal()).abs().stripTrailingZeros();

            if (result.scale() <= 0) {
                return coercion.coerce(result.toBigIntegerExact());
            }

            return coercion.coerce(result);
        }

        return coercion.coerce(helper.toBigInteger(lhs).remainder(jnumber).abs());
    }

    @Override
    public LuaValue modFrom(BigDecimal lhs) {
        if (signum() == 0 || lhs.signum() == 0) {
            return LuaDouble.NAN;
        }

        BigDecimal result = lhs.remainder(tobigdecimal()).abs().stripTrailingZeros();
        if (result.scale() <= 0) {
            return coercion.coerce(result.toBigIntegerExact());
        }

        return coercion.coerce(result);
    }

    @Override
    public LuaValue modFrom(BigInteger lhs) {
        if (signum() == 0 || lhs.signum() == 0) {
            return LuaDouble.NAN;
        }
        return coercion.coerce(lhs.remainder(jnumber).abs());
    }
}
