/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.IAuthentication;
import dBox.utils.ConfigManager;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class DBoxClient
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NotBoundException, IOException
    {
        ConfigManager context = ConfigManager.getInstance();
        System.setProperty("java.security.policy", context.getPropertyValue("security"));
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        try
        {

            int port = Integer.parseInt(context.getPropertyValue("port"));
            System.out.println(port);
            Registry registry = LocateRegistry.getRegistry(context.getPropertyValue("server"), port);
            //           Registry registry = LocateRegistry.getRegistry("localhost", port);
            IAuthentication auth = (IAuthentication) registry.lookup(IAuthentication.class.getSimpleName());
            InteractionManager interact = new InteractionManager(auth);
            //Start
            interact.init();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DBoxClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
