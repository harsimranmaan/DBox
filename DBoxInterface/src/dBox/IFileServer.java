/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

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
    public void setDirectory(String directory, String clientBase, String clientSeperator) throws RemoteException;

    /**
     * Receive a file from a remote source
     * <p/>
     * @param packet The file packet to receive
     * <p/>
     * @exception RemoteException if something bad happens
     * <p/>
     */
    public void receiveFile(String path, FilePacket packet) throws RemoteException;

    FilePacket download(String path) throws RemoteException;

    /**
     * Get the server path separator
     * <p/>
     * @return The string literal for path
     * <p/>
     * @throws RemoteException
     */
    String pathSeperator() throws RemoteException;

    /**
     *
     * @param path
     * @param upto
     * <p/>
     * @throws RemoteException
     */
    void delete(String path, String upto) throws RemoteException;

    /**
     *
     * @param currentFiles
     * @param deletedFiles
     * <p/>
     * @return
     * <p/>
     * @throws RemoteException
     */
    HashMap<String, ClientAction> getClientActions(HashMap<String, FileDetail> currentFiles, HashMap<String, FileDetail> deletedFiles) throws RemoteException;
}
