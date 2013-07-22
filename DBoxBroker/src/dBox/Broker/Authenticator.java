/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Broker;

import dBox.ClientDetails;
import dBox.IAuthentication;
import dBox.ServerDetails;
import dBox.ServerUtils.DataAccess;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import dBox.utils.Hashing;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to authenticate the user and get the server details
 * <p/>
 * @author harsimran.maan
 */
public class Authenticator extends UnicastRemoteObject implements IAuthentication, Serializable
{

    private static final long serialVersionUID = 2222L;
    private final ServerChecker checker;
    private final ConfigManager config;

    /**
     *
     * @throws RemoteException
     */
    public Authenticator(ServerChecker checker, ConfigManager config) throws RemoteException
    {
        super();
        this.checker = checker;
        this.config = config;
    }

    /**
     * The function to authenticate the client with user name and password
     * <p/>
     * @param userName
     * @param password <p/>
     * @return the client details
     * <p/>
     * @throws RemoteException
     */
    @Override
    public ClientDetails authenticate(String userName, String password) throws RemoteException
    {
        ClientDetails client;
        CustomLogger.log("Authenticator > authenticate: userName " + userName);
        if (userName.matches("[a-zA-Z0-9]+") && password.matches("[a-zA-Z0-9]+"))
        {

            try
            {
                ResultSet set = DataAccess.getResultSet("SELECT * FROM Client where username = '" + userName + "' AND userpassword = '" + Hashing.encryptSHA(password) + "'");
                if (set != null && set.next())
                {
                    client = new ClientDetails(set.getString("username"), set.getString("pairhash"), set.getInt("quota"), set.getInt("clusterId"));
                }
                else
                {
                    throw new RemoteException("Invalid username/password.");
                }
                return client;
            }
            catch (SQLException ex)
            {
                Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
                throw new RemoteException("Something went wrong while fetching your details. Write to the admin about this issue.");
            }
        }
        else
        {
            throw new RemoteException("Username should be aplhanumeric only.");
        }
    }

    /**
     * The function to authenticate the client with the hash
     * <p/>
     * @param hash <p/>
     * @return the client details
     * <p/>
     * @throws RemoteException
     */
    @Override
    public ClientDetails authenticate(String hash) throws RemoteException
    {
        ClientDetails client;
        CustomLogger.log("Authenticator > authenticate: hash " + hash);
        if (hash.matches("[a-zA-Z0-9]+"))
        {
            try
            {
                ResultSet set = DataAccess.getResultSet("SELECT * FROM Client where pairhash = '" + hash + "'");
                if (set != null && set.next())
                {
                    client = new ClientDetails(set.getString("username"), set.getString("pairhash"), set.getInt("quota"), set.getInt("clusterId"));
                }
                else
                {
                    throw new RemoteException("Not able to login. Please provide login credentials.");
                }
                return client;
            }
            catch (SQLException ex)
            {
                Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
                throw new RemoteException("Something went wrong while fetching your details. Write to the admin about this issue.");
            }
        }
        else
        {
            throw new RemoteException("Not able to login. Please provide login credentials.");
        }
    }

    /**
     * The function to get the server details
     * <p/>
     * @param clusterId <p/>
     * @return the server details
     * <p/>
     * @throws RemoteException
     */
    @Override
    public ServerDetails getServerDetails(int clusterId) throws RemoteException
    {
        String server = config.getPropertyValue("localserver");
        int port = Integer.parseInt(config.getPropertyValue("serverPort"));
        ServerDetails primaryServer = getPrimaryServer(server, port, clusterId);
        CustomLogger.log("Authenticator > getServerDetails: Primary server " + primaryServer.getServerName());
        return primaryServer;

    }

    /**
     * The synchronized function to get the primary server in the cluster
     * <p/>
     * @param defaultServer
     * @param port
     * @param clusterId <p/>
     * @return the primary server details
     */
    private synchronized ServerDetails getPrimaryServer(String defaultServer, int port, int clusterId)
    {
        ServerDetails sDetails = new ServerDetails(defaultServer, port, clusterId, 0);
        HashMap<String, ServerDetails> serverDetails = checker.getServerDetails();
        int minimum = 1000000;
        for (ServerDetails server : serverDetails.values())
        {
            if (server.getClusterId() == clusterId && minimum > server.getServerIndex())
            {
                minimum = server.getServerIndex();
                sDetails = server;
            }
        }
        return sDetails;
    }
}
