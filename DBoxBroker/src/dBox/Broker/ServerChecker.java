/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Broker;

import dBox.ServerDetails;
import dBox.ServerUtils.IServerChecker;
import dBox.utils.CustomLogger;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The class to check the server and monitor server and add maintain hash map
 * <p/>
 * @author harsimran.maan
 */
public class ServerChecker extends UnicastRemoteObject implements IServerChecker, Serializable
{

    private static final long serialVersionUID = 222142L;
    private HashMap<String, ServerDetails> serverDetails;

    /**
     * The function to check the server state
     * <p/>
     * @throws RemoteException
     */
    public ServerChecker() throws RemoteException
    {
        super();
        this.serverDetails = new HashMap<>();
    }

    /**
     * The function to ping the server
     * <p/>
     * @param server
     * @param port
     * @param clusterId
     */
    public synchronized void pingServer(String server, int port, int clusterId)
    {
        if (getServerDetails().containsKey(server))
        {
            getServerDetails().get(server).ping();

        }
        else
        {
            add(server, port, clusterId);
        }
    }

    /**
     * The function to add the server detail to the hash map
     * <p/>
     * @param servername
     * @param port
     * @param clusterId
     */
    private void add(String servername, int port, int clusterId)
    {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(0);
        for (ServerDetails value : serverDetails.values())
        {
            if (value.getClusterId() == clusterId)
            {
                list.add(value.getServerIndex());
            }
        }
        serverDetails.put(servername, new ServerDetails(servername, port, clusterId, Collections.max(list) + 1));

    }

    /**
     * The override function to ping the server
     * <p/>
     * @param server
     * @param port
     * @param clusterId <p/>
     * @throws RemoteException
     */
    @Override
    public void ping(String server, int port, int clusterId) throws RemoteException
    {
        pingServer(server, port, clusterId);
        CustomLogger.log(serverDetails.toString());
    }

    /**
     * @return the serverDetails
     */
    public HashMap<String, ServerDetails> getServerDetails()
    {
        return serverDetails;
    }

    /**
     * @param serverDetails the serverDetails to set
     */
    public void setServerDetails(HashMap<String, ServerDetails> serverDetails)
    {
        this.serverDetails = serverDetails;
    }

    /**
     * The function to monitor the server
     * <p/>
     * @param server
     * @param clusterId <p/>
     * @return server detail
     * <p/>
     * @throws RemoteException
     */
    @Override
    public ServerDetails getMonitor(String server, int clusterId) throws RemoteException
    {
        return tryGetMonitor(server, clusterId);
    }

    /**
     * The synchronized function to get the monitor server
     * <p/>
     * @param server
     * @param clusterId <p/>
     * @return monitor server
     * <p/>
     * @throws RemoteException
     */
    private synchronized ServerDetails tryGetMonitor(String server, int clusterId) throws RemoteException
    {
        int myServerIndex = serverDetails.get(server).getServerIndex();
        HashMap<Integer, ServerDetails> temp = new HashMap<>();

        for (ServerDetails serverD : serverDetails.values())
        {
            if (!serverD.getServerName().equals(server) && serverD.getClusterId() == clusterId && myServerIndex < serverD.getServerIndex())
            {
                temp.put(serverD.getServerIndex(), serverD);


            }
        }
        if (temp.size() > 0)
        {
            return temp.get(Collections.min(temp.keySet()));
        }
        throw new RemoteException("No monitor");
    }
}
