package com.sagiadinos.garlic.launcher.configuration;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher
{

    public String generateSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return Arrays.toString(salt);
    }

    public String hashClearTextWithSalt(String clear_text, String salt)
    {
        try
        {
            PBEKeySpec spec      = new PBEKeySpec(clear_text.toCharArray(), salt.getBytes(),1000,160);
            SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashed        = key.generateSecret(spec).getEncoded();
            return String.format("%x", new BigInteger(hashed));
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }


}
