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
 *
 * @author harsimran.maan
 */
public interface IServersync extends Remote
{

    public HashMap<String, String> getAllFileNames() throws RemoteException;

    public String getHash(String path) throws RemoteException;

    void getFile(String path, String serverName, int port) throws RemoteException;
}
