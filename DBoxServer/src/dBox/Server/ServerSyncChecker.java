/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.FilePacket;
import dBox.ServerDetails;
import dBox.ServerUtils.IServersync;
import dBox.utils.Hashing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class ServerSyncChecker extends Thread
{

    private final String myServername;
    private final PeerDetailsGetter peerDetails;
    private IServersync syncProvider;
    private ServerDetails monitorDetails;
    private final int port;

    public ServerSyncChecker(String myServername, int port)
    {
        this.myServername = myServername;
        this.port = port;
        this.peerDetails = new PeerDetailsGetter();
    }

    private boolean firstTimeSync() throws Exception
    {
        this.monitorDetails = peerDetails.getServerDetails(myServername);
        setProvider();
        HashMap<String, String> allFileNames = this.syncProvider.getAllFileNames();
        for (String path : allFileNames.keySet())
        {
            if (!Hashing.getSHAChecksum(path).equals(allFileNames.get(path)))
            {
                copyFile(path);
            }
        }
        return true;
    }

    private void startSync()
    {
        try
        {
            firstTimeSync();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ServerSyncChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
//        while (true)
//        {
//        }
    }

    private void copyFile(String path) throws RemoteException
    {
        syncProvider.getFile(path, myServername, port);
    }

    @Override
    public void run()
    {
        startSync();
    }

    private void setProvider() throws Exception
    {
        Registry registry = LocateRegistry.getRegistry(monitorDetails.getServerName(), monitorDetails.getPort());
        syncProvider = (IServersync) registry.lookup(IServersync.class.getSimpleName());
    }
}
