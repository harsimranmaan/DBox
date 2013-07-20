/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.FilePacket;
import dBox.FileToucher;
import dBox.ServerUtils.IServersync;
import dBox.utils.Hashing;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author harsimran.maan
 */
public class ServerSyncProvider implements IServersync, Serializable
{

    private final String rootPath;

    public ServerSyncProvider(String rootPath)
    {
        this.rootPath = rootPath;
    }

    @Override
    public HashMap<String, String> getAllFileNames() throws RemoteException
    {
        HashMap<String, String> list = new HashMap<String, String>();

        Path rootPath = Paths.get(this.rootPath);
        FileToucher.touchAll(rootPath, list, true);
        return list;
    }

    @Override
    public String getHash(String path) throws RemoteException
    {
        return Hashing.getSHAChecksum(path.toString());
    }

    @Override
    public FilePacket getFile(String path) throws RemoteException
    {

        Path get = Paths.get(path);
        FilePacket packet = new FilePacket(get);
        return packet;
    }
}
