/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class ConfigManager
{

    private static ConfigManager configManager = new ConfigManager();
    private Properties properties;

    /**
     * The default constructor for ConfigManager class.
     */
    private ConfigManager()
    {
        properties = new Properties();
        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File("config.properties"));
            properties.load(fileInputStream);
            fileInputStream.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(ConfigManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The getter function for configManager variable.
     * <p/>
     * @return the configManager variable
     */
    public static ConfigManager getInstance()
    {
        return configManager;
    }

    /**
     * The getter function for properties variable.
     * <p/>
     * @return the properties variable
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * The function to get String of property by given String keyword.
     * <p/>
     * @param key the String keyword to get property value for
     * <p/>
     * @return the String value of the properties
     */
    public String getPropertyValue(String key)
    {
        return properties.getProperty(key);
    }

    public void setPropertyValue(String key, String value)
    {
        try
        {
            properties.setProperty(key, value);
            File file = new File("config.properties");
            OutputStream out = new FileOutputStream(file);
            properties.store(out, "Saved on " + new Date().toString());
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
