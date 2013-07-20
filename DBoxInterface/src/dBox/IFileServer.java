/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.rmi.Remote;
import java.rmi.RemoteException;

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

    public FilePacket download(String path) throws RemoteException;

    /**
     * Get the server path separator
     * <p/>
     * @return The string literal for path
     * <p/>
     * @throws RemoteException
     */
    public String pathSeperator() throws RemoteException;

    /**
     * Tells what action to perform
     * <p/>
     * @param path
     * @param fileHash
     * @param oldHash  <p/>
     * @return <p/>
     * @throws RemoteException
     */
    public ClientAction actionOnModify(String path, String fileHash, String oldHash) throws RemoteException;

    /**
     * Tells what action to perform if a file on client is deleted
     * <p/>
     * @param path
     * @param oldHash <p/>
     * @return <p/>
     * @throws RemoteException
     */
    public ClientAction actionOnDelete(String path, String oldHash) throws RemoteException;
}
