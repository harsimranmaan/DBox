/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 *
 * @author harsimran.maan
 */
public interface IFileServer extends Remote
{

    /**
     * Set the directory to upload files to
     * <p/>
     * @param directory The directory to place files in
     * <p/>
     * @exception RemoteException to remote object
     * <p/>
     */
    public void setDirectory(String directory) throws RemoteException;

    /**
     * Receive a file from a remote source
     * <p/>
     * @param packet The file packet to receive
     * <p/>
     * @exception RemoteException if something bad happens
     * <p/>
     */
    public void receiveFile(String path, FilePacket packet) throws RemoteException;

    /**
     * Get the server path separator
     * <p/>
     * @return The string literal for path
     * <p/>
     * @throws RemoteException
     */
    public String pathSeperator() throws RemoteException;

    /**
     * Checks if the file or folder at a path has changed
     * <p/>
     * @param path <p/>
     * @return <p/>
     * @throws RemoteException
     */
    public ClientAction hasChanged(String path, HashMap<String, String> fileHashes) throws RemoteException;
}
