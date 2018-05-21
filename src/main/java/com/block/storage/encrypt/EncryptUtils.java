package com.block.storage.encrypt;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
    static Cipher desCipher;
    static SecretKey myDesKey;

    static {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            myDesKey = keygenerator.generateKey();
            desCipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] data) {
        byte[] encryptedData = new byte[0];
        try {
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            encryptedData = desCipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    public static byte[] decrypt(byte[] encryptedData) {
        byte[] data = new byte[0];
        try {
            desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
            data = desCipher.doFinal(encryptedData);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return data;
    }
}
