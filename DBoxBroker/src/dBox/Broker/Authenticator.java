/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Broker;

import dBox.ClientDetails;
import dBox.IAuthentication;
import dBox.ServerUtils.DataAccess;
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
        if (userName.matches("[a-zA-Z0-9]+") && password.matches("[a-zA-Z0-9]+"))
        {
            try
            {
                ResultSet set = DataAccess.getResultSet("SELECT * FROM Client where username = '" + userName + "' AND userpassword = '" + password + "'");
                if (set != null && set.next())
                {
                    client = new ClientDetails(set.getString("pairhash"), set.getInt("quota"));
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
}
