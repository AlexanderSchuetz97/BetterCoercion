package io.github.alexanderschuetz97.bettercoercion.math;

import org.luaj.vm2.LuaError;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * It does math.
 * Depending on the uses the math it does is highly questionable and should be replaced.
 */
class DefaultLuaMathHelper implements LuaMathHelper {

    static DefaultLuaMathHelper INSTANCE = new DefaultLuaMathHelper();

    protected static final BigDecimal MINUS_ONE = new BigDecimal(-1);
    protected static final int MAX_EXPONENT_INT = 999999999;
    protected static final BigDecimal MAX_EXPONENT_DECI = new BigDecimal(MAX_EXPONENT_INT);
    protected static final BigDecimal E200 = new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663035354759457138217852516642742746639193200305992181741359662904357290033429526059563073813232862794349076323382988075319525101901");
    protected static final BigDecimal[] E = new BigDecimal[200];
    static {
        for (int i = 0; i < E.length; i++) {
            E[i] = E200.setScale(i, RoundingMode.HALF_EVEN);
        }
    }

    protected static final int POW_SCALE_PRECISION_MULTIPLICATOR = 2;
    /**
     * If we go lower then 1 != 1 which would not be ideal...
     */
    protected static final int MIN_POW_PRECISION = 4;





    protected static final BigDecimal[] FACS = new BigDecimal[128];
    protected static final BigInteger FAC_127;
    static {
        FACS[0] = BigDecimal.ONE;
        FACS[1] = BigDecimal.ONE;
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i < FACS.length; i++) {
            result = result.multiply(BigInteger.valueOf(i));
            FACS[i] = new BigDecimal(result);
        }
        FAC_127 = result;
    }

    /**
     * !n
     * (faculty)
     */
    public static BigDecimal fac(int n) {
        int abs = Math.abs(n);
        if (FACS.length > abs) {
            return n < 0 ? FACS[abs].negate() : FACS[abs];
        }

        BigInteger result = FAC_127;
        for (int i = 128; i <= abs; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }

        return new BigDecimal(n < 0 ? result.negate() : result);
    }

    /**
     * Tailor series approximation for e^exponent
     */
    public static BigDecimal exp(BigDecimal exponent, int scale) {
        BigDecimal exponentAbs = exponent.abs();

        //split into left and right where left.scale()=0 and right < 1
        BigDecimal left = exponentAbs.setScale(0, RoundingMode.DOWN);
        if (MAX_EXPONENT_DECI.compareTo(left) < 0) {
            throw new ArithmeticException("exponent " + exponent.toString() + " is to big.");
        }

        //Yes this is just an approximation but what can you do...
        MathContext context = new MathContext(POW_SCALE_PRECISION_MULTIPLICATOR * scale, RoundingMode.HALF_EVEN);

        BigDecimal right = exponentAbs.subtract(left, context);

        BigDecimal rightResult = BigDecimal.ONE;

        //if right != 0 cus e^0=1
        if (right.compareTo(BigDecimal.ZERO) != 0) {
            //e^right
            rightResult = rightResult.add(right, context);
            BigDecimal stopDelta = BigDecimal.ONE.movePointLeft(scale);
            BigDecimal delta;
            int i = 1;
            do {
                i++;
                BigDecimal preIteration = rightResult;
                rightResult = rightResult.add(right.pow(i, context).divide(fac(i), context));
                delta = preIteration.subtract(rightResult, context).abs();
            } while (delta.compareTo(stopDelta) > 0);
        }

        BigDecimal e = E200;
        if (context.getPrecision() < E.length) {
            e = E[context.getPrecision()];
        }
        //e^left. since left is a whole number that must fit into an int we can actually use the pow from java....
        BigDecimal leftResult = e.pow(left.intValueExact(), context);


        BigDecimal totalResult = leftResult.multiply(rightResult, context).setScale(scale, RoundingMode.HALF_EVEN);
        if (exponent.signum() == -1) {
            //a^-b = 1/(a^b)
            return BigDecimal.ONE.divide(totalResult, context);
        }

        return totalResult;
    }

    /**
     * Newtons approximation for ln.
     */
    public static BigDecimal ln(BigDecimal input, int scale) {
        int precision = POW_SCALE_PRECISION_MULTIPLICATOR *scale;
        BigDecimal result = input;

        BigDecimal delta;
        do {
            BigDecimal ey = exp(result, scale);
            delta = BigDecimal.ONE.subtract(input.divide(ey, precision, BigDecimal.ROUND_HALF_EVEN));
            result = result.subtract(delta);
        } while(delta.setScale(scale, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) != 0);

        return result.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    @Override
    public int investigate(Number number) {
        if (number instanceof Float) {
            float f = number.floatValue();
            if (Float.isNaN(f)) {
                return T_NAN;
            }

            if (Float.POSITIVE_INFINITY == f) {
                return T_POSINF;
            }

            if (Float.NEGATIVE_INFINITY == f) {
                return T_NEGINF;
            }

            return T_NUMBER;
        }

        if (number instanceof Double) {
            investigate(number.doubleValue());
        }


        return T_NUMBER;
    }

    @Override
    public int investigate(double number) {
        if (Double.isNaN(number)) {
            return T_NAN;
        }

        if (Double.POSITIVE_INFINITY == number) {
            return T_POSINF;
        }

        if (Double.NEGATIVE_INFINITY == number) {
            return T_NEGINF;
        }

        return T_NUMBER;
    }

    @Override
    public BigInteger toBigInteger(double number) {
        return toBigDecimal(number).toBigInteger();
    }

    @Override
    public BigInteger toBigInteger(int number) {
        return BigInteger.valueOf(number);
    }

    @Override
    public BigInteger toBigInteger(long number) {
        return null;
    }

    @Override
    public BigInteger toBigInteger(Number number) {
        if (number instanceof BigInteger) {
            return (BigInteger) number;
        }

        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).toBigInteger();
        }

        if (number instanceof Integer) {
            return toBigInteger(number.intValue());
        }

        if (number instanceof Long) {
            return BigInteger.valueOf(number.longValue());
        }

        return toBigInteger(number.doubleValue());
    }

    @Override
    public BigDecimal toBigDecimal(double number) {
        try {
            return new BigDecimal(String.valueOf(number));
        } catch (NumberFormatException exc) {
            if (Double.isInfinite(number)) {
                throw new LuaError("attempt to convert infinity to big decimal");
            }

            if (Double.isNaN(number)) {
                throw new LuaError("attempt to convert NaN to big decimal");
            }

            throw new LuaError(exc);
        }
    }

    @Override
    public BigDecimal toBigDecimal(int number) {
        return new BigDecimal(number);
    }

    @Override
    public BigDecimal toBigDecimal(BigInteger integer) {
        return new BigDecimal(integer);
    }

    @Override
    public BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }

        if (number instanceof BigInteger) {
            return toBigDecimal((BigInteger) number);
        }

        if (number instanceof Integer) {
            return toBigDecimal(number.intValue());
        }

        if (number instanceof Long) {
            return new BigDecimal(number.longValue());
        }

        return toBigDecimal(number.doubleValue());
    }

    @Override
    public boolean hasFraction(double number) {
        return number % 1d == 0;
    }

    @Override
    public boolean hasFraction(Number number) {
        if (number instanceof BigInteger) {
            return false;
        }

        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).stripTrailingZeros().scale() > 0;
        }


        return number.doubleValue() % 1d == 0;
    }

    @Override
    public Number pow(BigDecimal base, BigDecimal exponent, int scale) {
        int baseSignum = base.signum();
        int exponentSignum = exponent.signum();

        if (baseSignum == 0) {
            //0^0=NaN
            //0^-X=NaN
            if (exponentSignum <= 0) {
                return Double.NaN;
            }

            //0^X=0
            return BigDecimal.ZERO;
        }

        if (exponentSignum == 0) {
            //X^0=1
            //-X^0=-1
            return baseSignum == 1 ? BigDecimal.ONE : MINUS_ONE;
        }

        int precision = Math.max(scale, MIN_POW_PRECISION);
        MathContext context = new MathContext(precision, RoundingMode.HALF_EVEN);
        BigDecimal exponentAbs = exponent.abs();

        BigDecimal left = exponentAbs.setScale(0, RoundingMode.DOWN);
        BigDecimal right = exponentAbs.subtract(left, context);
        if (MAX_EXPONENT_DECI.compareTo(left) < 0) {
            if (exponent.signum() == 1) {
                //X^INF=INF
                //-X^INF=-INF
                return baseSignum == 1 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            }

            //X^-INF=0
            return BigDecimal.ZERO;
        }

        if(MAX_EXPONENT_DECI.compareTo(base.abs()) < 0) {
            //INF^X=INF
            //INF^-X=0
            return exponentSignum == 1 ? Double.POSITIVE_INFINITY : BigDecimal.ZERO;
        }

        BigDecimal leftResult = base.pow(left.intValueExact(), context);

        BigDecimal rightResult = BigDecimal.ONE;
        if (right.compareTo(BigDecimal.ZERO) != 0) {
            // a^b is roughly e^(b*ln(a))
            rightResult = exp(right.multiply(ln(base, precision), context), precision);
        }

        BigDecimal totalResult = leftResult.multiply(rightResult, context);
        if (exponent.signum() == -1) {
            //a^-b = 1/(a^b)
            return BigDecimal.ONE.divide(totalResult, context);
        }

        return totalResult.setScale(scale, RoundingMode.HALF_EVEN);
    }

    @Override
    public int getMaxPowExponent() {
        return MAX_EXPONENT_INT;
    }
}
