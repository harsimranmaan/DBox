/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.ServerUtils;

import dBox.ServerDetails;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author harsimran.maan
 */
public interface IServerChecker extends Remote
{

    /**
     *
     * @param server
     * @param port
     * @param clusterId <p/>
     * @throws RemoteException
     */
    public void ping(String server, int port, int clusterId) throws RemoteException;

    /**
     *
     * @param server
     * @param clusterId <p/>
     * @return <p/>
     * @throws RemoteException
     */
    public ServerDetails getMonitor(String server, int clusterId) throws RemoteException;
}
