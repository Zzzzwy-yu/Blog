package com.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {

    @Test
    void testBcrypt() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(10);
        String dbHash = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2";
        boolean matches = enc.matches("123456", dbHash);
        System.out.println("matches(123456) = " + matches);
        System.out.println("new hash for 123456 = " + enc.encode("123456"));
    }
}
