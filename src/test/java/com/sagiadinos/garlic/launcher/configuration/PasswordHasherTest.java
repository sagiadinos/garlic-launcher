package com.sagiadinos.garlic.launcher.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest
{

    @Test
    void generateSalt()
    {
        PasswordHasher MyClass = new PasswordHasher();
        String result = MyClass.generateSalt();

        assertTrue(result.length() > 16);

    }

    @Test
    void hashClearTextWithSalt()
    {
        PasswordHasher MyClass = new PasswordHasher();
        String result = MyClass.hashClearTextWithSalt("Abracadabra", "Salem Aleikum");

        assertEquals("26d724ca38d9ef9b2e3ac16c96c781a987b93d33", result);
    }
}