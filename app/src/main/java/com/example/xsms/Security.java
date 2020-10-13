package com.example.xsms;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class Security {

    // utility function: convert hex array to byte array
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("hello");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    //convertion from string to hex


    //Generating the key
    private static Key generateKey(String secretKeyString) throws Exception {
        // generate AES secret key from a String
        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");
        return key;
    }

    // decryption function
    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg)
            throws Exception {
        // generate AES secret key from the user input secret key
        Key key = generateKey(secretKeyString);
        // get the cipher algorithm for AES
        Cipher c = Cipher.getInstance("AES");
        // specify the decryption mode
        c.init(Cipher.DECRYPT_MODE, key);
        // decrypt the message
        byte[] decValue = c.doFinal(encryptedMsg);
        return decValue;
    }

    // utility function
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs += ("0" + stmp);
            else
                hs += stmp;
        }
        return hs.toUpperCase();
    }

    // encryption function
    public static byte[] encryptSMS(String secretKeyString,String msgContentString) {
        try {
            byte[] returnArray;
            // generate AES secret key from user input
            Key key = generateKey(secretKeyString);
            // specify the cipher algorithm using AES
            Cipher c = Cipher.getInstance("AES");
            // specify the encryption mode
            c.init(Cipher.ENCRYPT_MODE, key);
            // encrypt
            returnArray = c.doFinal(msgContentString.getBytes());
            return returnArray;
        } catch (Exception e) {
            e.printStackTrace();
            byte[] returnArray = null;
            return returnArray;
        }

    }

    //generate the secretKey
    public String secretKeyGen(int seed){
        int nb=0; String keyword="";
        //using the seed to generate same key
        Random rand = new Random(seed);
        for(int i=0;i<16;i++){
            nb=rand.nextInt(10);
            keyword=keyword+1;
        }
        return keyword;
    }

    public static int generateRandomData(){
        SecureRandom random = new SecureRandom();
        int randomData = random.nextInt(10_000);
        return randomData;
    }

    public static String generateKey(int randomdata){
        Random rand1 = new Random(randomdata);
        String keyword="";
        int nb=0;
        for(int i=0;i<16;i++)
        {
            nb= rand1.nextInt(10);
            //System.out.print(nb);
            keyword = keyword+nb;
        }
        return keyword;
    }

    public static String ConvertFlag(String arg){
        return String.format("%040x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_16LE)));
    }


}
