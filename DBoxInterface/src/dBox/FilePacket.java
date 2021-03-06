/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

/**
 *
 * @author harsimran.maan
 * <p/>
 */
import dBox.utils.CustomLogger;
import java.io.*;
import java.nio.file.Path;

/**
 * Class to represent a File object that can be sent and recreated on another
 * system Will actually do the creating for you
 * <p/>
 */
public class FilePacket implements Serializable
{

    private String fileName;
    // the data in my file
    private byte[] data;

    /**
     * Make a file packet that represents a given filename
     * <p/>
     * @param name The filename this represents
     * <p/>
     */
    public FilePacket(Path fileName)
    {
        try
        {
            CustomLogger.log("FilePacket > FilePacket : fileName " + fileName.toString());
            File file = fileName.toFile();
            data = new byte[(int) (file.length())];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(data);
            fileInputStream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.fileName = fileName.getFileName().toString();
    }

    /**
     * Get the name associated with this file
     * <p/>
     * @return The name
     * <p/>
     */
    public String getName()
    {
        return fileName;
    }

    /**
     * Have the filepacket read itself in from the file it represents in name
     * <p/>
     */
    public void copy(OutputStream out)
    {
        try
        {
            if (data != null)
            {
                out.write(data);
            }
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
