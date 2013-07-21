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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class Authenticator extends UnicastRemoteObject implements IAuthentication, Serializable
{

    private static final long serialVersionUID = 2222L;

    /**
     *
     * @throws RemoteException
     */
    public Authenticator() throws RemoteException
    {
        super();
    }

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

    @Override
    public ServerDetails getServerDetails(int clusterId) throws RemoteException
    {
        ConfigManager config = ConfigManager.getInstance();
        String server = config.getPropertyValue("localserver");
        int port = Integer.parseInt(config.getPropertyValue("serverPort"));
        ServerDetails primaryServer = getPrimaryServer(server, port, clusterId);
        CustomLogger.log("Authenticator > getServerDetails: Primary server " + primaryServer.getServerName());
        return primaryServer;

    }

    private ServerDetails getPrimaryServer(String defaultServer, int port, int clusterId)
    {
        ServerDetails sDetails;
        try
        {
            ResultSet set = DataAccess.getResultSet("SELECT * FROM ServerDetails WHERE clusterId = " + clusterId + " ORDER BY serverIndex LIMIT 1");
            if (set != null && set.next())
            {
                sDetails = new ServerDetails(set.getString("servername"), set.getInt("portNumber"), set.getInt("clusterId"), 0);
            }
            else
            {
                sDetails = new ServerDetails(defaultServer, port, clusterId, 0);
            }
            return sDetails;
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
            sDetails = new ServerDetails(defaultServer, port, clusterId, 0);
        }
        return sDetails;
    }
}
