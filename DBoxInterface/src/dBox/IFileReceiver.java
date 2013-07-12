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
public interface IFileReceiver extends Remote
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
    public void receiveFile(FilePacket packet) throws RemoteException;
}
