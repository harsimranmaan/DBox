/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.ServerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class MetaData
{

    public static String get(String url)
    {
        String inputLine = "";
        try
        {
            URL meta = new URL(url);
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(meta.openStream())))
            {
                inputLine = in.readLine();
            }

        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MetaData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inputLine;
    }
}
