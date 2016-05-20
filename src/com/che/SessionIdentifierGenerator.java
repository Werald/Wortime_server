package com.che;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Alexx on 14.05.2016.
 */

public class SessionIdentifierGenerator {
    private SecureRandom random;

    public SessionIdentifierGenerator()
    {
        random = new SecureRandom();
    }
    public String generateString() {
        return new BigInteger(130, random).toString(32);
    }
}
