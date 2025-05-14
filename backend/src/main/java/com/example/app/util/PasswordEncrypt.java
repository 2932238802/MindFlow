package com.example.app.util;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncrypt {
    // 加密密码 //
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // 验证密码 //
    public static boolean verify(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
