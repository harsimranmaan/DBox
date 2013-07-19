/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Authenticate the user to the broker
 * <p/>
 * @author harsimran.maan
 */
public interface IAuthentication extends Remote
{

    /**
     * authenticate the client for the first time
     * <p/>
     * @param username of the client
     * @param password of the client
     * <p/>
     * @return client details
     * <p/>
     * @throws RemoteException
     */
    ClientDetails authenticate(String username, String password) throws RemoteException;

    /**
     * authenticates user with hash if property file has updated it
     * <p/>
     * @param hash
     * <p/>
     * @return client details
     * <p/>
     * @throws RemoteException
     */
    ClientDetails authenticate(String hash) throws RemoteException;

    /**
     * calls to get the information about the server
     * <p/>
     * @return server detail
     * <p/>
     * @throws RemoteException
     */
    ServerDetails getServerDetails() throws RemoteException;
}
