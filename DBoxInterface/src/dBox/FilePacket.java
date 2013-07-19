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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to represent a File object that can be sent and recreated on another
 * system Will actually do the creating for you
 * <p/>
 */
public class FilePacket implements Serializable
{

    // the file name I represent
    private String path;
    private String fileName;

    /**
     * Make a file packet that represents a given filename
     * <p/>
     * @param name The filename this represents
     * <p/>
     */
    public FilePacket(String path, String fileName)
    {
        this.path = path;
        this.fileName = fileName;
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
            File file = new File(path);

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096]; // To hold file contents
            int bytes_read; // How many bytes in buffer

            // Read a chunk of bytes into the buffer, then write them out,
            // looping until we reach the end of the file (when read() returns
            // -1). Note the combination of assignment and comparison in this
            // while loop. This is a common I/O programming idiom.
            while ((bytes_read = fileInputStream.read(buffer)) != -1)
            // Read until EOF
            {
                out.write(buffer, 0, bytes_read);
            }

            //  fileInputStream.read(data);
            fileInputStream.close();
            out.close();

        }
        catch (IOException ex)
        {
            Logger.getLogger(FilePacket.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//    /**
//     * Have the file packet recreate itself, used after sending it to another
//     * location file will have same name and contents
//     * <p/>
//     * @param out The outputStream to write itself to
//     * <p/>
//     */
//    public void writeTo(OutputStream out)
//    {
////        try
////        {
////            out.write(data);
////        }
////        catch (Exception e)
////        {
////            e.printStackTrace();
////        }
//    }
}
