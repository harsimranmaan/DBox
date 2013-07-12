/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Broker;

import dBox.IAuthentication;
import dBox.ServerUtils.DataAccess;
import dBox.utils.ConfigManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class DBoxBroker
{

    private static void printMessage(String simpleName)
    {
        System.out.println("Bound " + simpleName);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            ConfigManager context = ConfigManager.getInstance();
            // Bind the remote object in the registry
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(context.getPropertyValue("port")));
            registry.rebind(IAuthentication.class.getSimpleName(), new Authenticator());
            printMessage(IAuthentication.class.getSimpleName());

            registry.rebind(IAuthentication.class.getSimpleName(), new Authenticator());

            printMessage(IAuthentication.class.getSimpleName());

            DataAccess.init(context.getPropertyValue("dbConnection"), context.getPropertyValue("dbUserId"), context.getPropertyValue("dbUserToken"));
            System.out.println("Server started");
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DBoxBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
