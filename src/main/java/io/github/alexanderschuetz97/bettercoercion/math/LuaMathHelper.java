package io.github.alexanderschuetz97.bettercoercion.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Internal interface used to do some math on lua values such as BigDecimal or BigInteger.
 * Extracted to an interface to be user replaceable. The Default implementation uses "good" approximations, however
 * I would not rule it out that it has bugs. The default implementation should NOT be used for any mission critical
 * computations.
 */
public interface LuaMathHelper {

    LuaMathHelper DEFAULT = DefaultLuaMathHelper.INSTANCE;
    int T_NUMBER = 0;
    int T_NAN = 1;
    int T_POSINF = 2;
    int T_NEGINF = 3;

    /**
     * Check for special number cases such as nan, inf, etc.
     */
    int investigate(Number number);

    /**
     * Check for special number cases such as nan, inf, etc.
     */
    int investigate(double number);

    /**
     * Does this double have a fraction?
     */
    boolean hasFraction(double number);

    /**
     * Does this "Number" have a fraction?
     */
    boolean hasFraction(Number number);


    //CONVERSION METHODS

    BigInteger toBigInteger(double number);

    BigInteger toBigInteger(int number);

    BigInteger toBigInteger(long number);

    BigInteger toBigInteger(Number number);

    BigDecimal toBigDecimal(double number);

    BigDecimal toBigDecimal(int number);

    BigDecimal toBigDecimal(BigInteger integer);

    BigDecimal toBigDecimal(Number number);

    //POW methods (power of exponent)

    /**
     *  base^exponent
     *  scale parameter is result scale.
     */
    Number pow(BigDecimal base, BigDecimal exponent, int scale);

    /**
     * Maximum exponent we can handle for pow before we just resort to infinity since the calculation would take too long.
     */
    int getMaxPowExponent();
}
