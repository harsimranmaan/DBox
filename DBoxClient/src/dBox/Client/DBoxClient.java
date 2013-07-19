/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.IAuthentication;
import dBox.IFileServer;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;
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
        CustomLogger.disableLogging(context.getPropertyValue("logging").equals("false"));
        System.setProperty("java.security.policy", context.getPropertyValue("security"));
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        try
        {
            boolean isDebug = "true".equals(context.getPropertyValue("debug"));
            String server;
            if (isDebug)
            {
                server = context.getPropertyValue("localserver");
            }
            else
            {
                server = context.getPropertyValue("server");
            }
            int port = Integer.parseInt(context.getPropertyValue("port"));
            CustomLogger.log("Connecting to server " + server + " on port " + port);
            Registry registry = LocateRegistry.getRegistry(server, port);
            IAuthentication auth = (IAuthentication) registry.lookup(IAuthentication.class.getSimpleName());
            InteractionManager interact = new InteractionManager(auth, context);
            //Start
            interact.init();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DBoxClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
