package whitewerx.com.trapos.translators;

/**
 * Maps tokens from parsed trades into a multiplier.
 * 
 * For example: 't' means factor up the trade by 1000.
 * 
 * @author ewhite
 */
public enum TradeMultiplier {
    ONE("", 1), THOUSANDS("t", 1000), MILLIONS("m", 1000000);

    /** Supported tokens: t, m */
    private String token;

    /** The amount to factor up a trade by */
    private int multiplier;

    private TradeMultiplier(String token, int multiplier) {
        this.token = token;
        this.multiplier = multiplier;
    }

    /**
     * @param token
     *            parsed from a trade message.
     * @return
     */
    public static TradeMultiplier multiplierFor(String token) {
        if (THOUSANDS.token.equals(token))
            return THOUSANDS;
        
        if (MILLIONS.token.equals(token))
            return MILLIONS;

        if (ONE.token.equals(token))
            return ONE;

        throw new IllegalArgumentException("Invalid token: " + token);
    }

    public int factor() {
        return multiplier;
    }
}
