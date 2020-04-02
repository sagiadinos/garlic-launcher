package com.sagiadinos.garlic.launcher.helper;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher
{
    private static final Random RANDOM = new SecureRandom();

    public String generateSalt()
    {
        byte[] salt = new byte[32];
        RANDOM.nextBytes(salt);
        return salt.toString();
    }

    public String hashClearTextWithSalt(String clear_text, String salt)
    {
        try
        {
            PBEKeySpec spec      = new PBEKeySpec(clear_text.toCharArray(), salt.getBytes(),1000,160);
            SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashed        = new byte[0];
            hashed               = key.generateSecret(spec).getEncoded();
            return String.format("%x", new BigInteger(hashed));
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
