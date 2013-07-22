/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.ServerUtils;

import dBox.FilePacket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * The interface for server synch
 * <p/>
 * @author harsimran.maan
 */
public interface IServersync extends Remote
{

    /**
     * To get the path(name) of all the files
     * <p/>
     * @return hash map
     * <p/>
     * @throws RemoteException
     */
    public HashMap<String, String> getAllFileNames() throws RemoteException;

    /**
     * Get the hash value of the file
     * <p/>
     * @param path
     * <p/>
     * @return hash value
     * <p/>
     * @throws RemoteException
     */
    public String getHash(String path) throws RemoteException;

    /**
     * Get the packet of the file
     * <p/>
     * @param path
     * <p/>
     * @return the packet of the file
     * <p/>
     * @throws RemoteException
     */
    FilePacket getFile(String path) throws RemoteException;
}
