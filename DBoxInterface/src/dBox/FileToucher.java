/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.io.File;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author harsimran.maan
 */
public class FileToucher
{

    public static void touchAllFiles(Path path, HashMap<Path, String> fileEvent, ArrayList<Path> ignorePath, boolean getHash)
    {
        CustomLogger.log("FileToucher > touchAllFiles : path " + path.toString() + " fileEvent " + fileEvent + " ignorePath " + ignorePath);
        File folder = new File(path.toString());
        File[] filelist = folder.listFiles();
        Path filepath;
        for (int i = 0; i < filelist.length; i++)
        {
            filepath = filelist[i].toPath();
            if (Files.isDirectory(filepath, NOFOLLOW_LINKS))
            {
                touchAllFiles(filepath, fileEvent, ignorePath, getHash);
            }
            else
            {

                //ignore certain paths
                if (ignorePath.contains(filepath))
                {
                    continue;
                }
                //Mark any offline changes
                if (getHash)
                {
                    fileEvent.put(filepath, Hashing.getSHAChecksum(filepath.toString()));

                }
                else
                {
                    fileEvent.put(filepath, "ENTRY_MODIFY");
                }
            }
        }
    }

    public static void touchAll(Path path, HashMap<String, String> fileEvent, boolean getHash)
    {
        CustomLogger.log("FileToucher > touchAll : path " + path.toString() + " fileEvent " + fileEvent);
        System.out.println(path.toString());

        File folder = new File(path.toString());
        File[] filelist = folder.listFiles();
        Path filepath;
        for (int i = 0; i < filelist.length; i++)
        {
            filepath = filelist[i].toPath();
            if (Files.isDirectory(filepath, NOFOLLOW_LINKS))
            {
                touchAll(filepath, fileEvent, getHash);
            }
            else
            {

                //Mark any offline changes
                if (getHash)
                {
                    fileEvent.put(filepath.toString(), Hashing.getSHAChecksum(filepath.toString()));

                }
                else
                {
                    fileEvent.put(filepath.toString(), "ENTRY_MODIFY");
                }
            }
        }
    }
}
