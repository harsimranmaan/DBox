/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

/**
 *
 * @author harsimran.maan
 */
import java.io.*;

/**
 * Class to represent a File object that can be sent and recreated on another
 * system Will actually do the creating for you
 * <p/>
 */
public class FilePacket implements Serializable
{

    // the file name I represent
    private String name;
    // the data in my file
    private byte[] data;

    /**
     * Make a file packet that represents a given filename
     * <p/>
     * @param name The filename this represents
     * <p/>
     */
    public FilePacket(String name)
    {
        this.name = name;
    }

    /**
     * Get the name associated with this file
     * <p/>
     * @return The name
     * <p/>
     */
    public String getName()
    {
        return name;
    }

    /**
     * Have the filepacket read iteself in from the file it represents in name
     * <p/>
     */
    public void readIn()
    {
        try
        {
            File file = new File(name);
            data = new byte[(int) (file.length())];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(data);
            fileInputStream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Have the file packet recreate itself, used after sending it to another
     * location file will have same name and contents
     * <p/>
     * @param out The outputStream to write itself to
     * <p/>
     */
    public void writeTo(OutputStream out)
    {
        try
        {
            out.write(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
