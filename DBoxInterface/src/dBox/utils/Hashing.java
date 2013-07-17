/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Kuntal
 */
public class Hashing
{

    public static byte[] createChecksum(String filename) throws Exception
    {
        InputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead;

        do
        {
            numRead = fis.read(buffer);
            if (numRead > 0)
            {
                complete.update(buffer, 0, numRead);
            }
        }
        while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    public static String getSHAChecksum(String filename) throws Exception
    {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i = 0; i < b.length; i++)
        {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    /**
     * The function encrypts SHA to the input String password.
     * <p/>
     * @param password the String password to be encrypted
     * <p/>
     * @return the SHA encrypted String
     */
    public static String encryptSHA(String password)
    {
        String hash = password;
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes());
            hash = getString(md.digest());

        }
        catch (NoSuchAlgorithmException ex)
        {
        }
        return hash;
    }

    /**
     * The functions to convert input byte array to String.
     * <p/>
     * @param digest the input digest
     * <p/>
     * @return converted String
     */
    private static String getString(byte[] digest)
    {
        StringBuilder hexString = new StringBuilder();

        for (int looper = 0; looper < digest.length; looper++)
        {
            String hex = Integer.toHexString(0xff & digest[looper]);
            if (hex.length() == 1)
            {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
