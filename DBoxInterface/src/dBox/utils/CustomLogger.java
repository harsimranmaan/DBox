/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.utils;

/**
 *
 * @author harsimran.maan
 */
public class CustomLogger
{

    private static boolean shouldLog = true;

    public static void disableLogging(boolean disable)
    {
        shouldLog = !disable;
    }

    public static void log(String message)
    {
        if (shouldLog)
        {
            System.out.println(message);
        }
    }
}
