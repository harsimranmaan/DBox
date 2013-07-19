/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author harsimran.maan
 */
public class FileDeleter
{

    /**
     * Recursively delete empty directory
     * <p/>
     * @param path
     * @param upto
     * <p/>
     * @throws IOException
     */
    public static void delete(Path path, Path upto) throws IOException
    {
        if (path == upto)
        {
            return;
        }
        Path parent = path.getParent();
        boolean isParentEmpty = new File(parent.toString()).listFiles().length == 0;
        Files.delete(path);
        if (isParentEmpty)
        {
            delete(parent, upto);
        }
    }
}
