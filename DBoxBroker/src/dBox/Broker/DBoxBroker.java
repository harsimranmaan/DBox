/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Broker;

import dBox.IAuthentication;
import dBox.ServerUtils.DataAccess;
import dBox.ServerUtils.IServerChecker;
import dBox.ServerUtils.MetaData;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ConfigManager context = ConfigManager.getInstance();
        CustomLogger.disableLogging(context.getPropertyValue("logging").equals("false"));
        System.setProperty("java.security.policy", context.getPropertyValue("security"));
        System.setProperty("java.rmi.useLocalHostname", "false");
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
                server = MetaData.get(context.getPropertyValue("meta") + context.getPropertyValue("host"));
            }
            System.setProperty("java.rmi.server.hostname", server);
            System.setProperty("java.net.preferIPv4Stack", "true");
            // Bind the remote object in the registry
            String port = context.getPropertyValue("port");
            CustomLogger.log("Starting server " + server + " on port " + port);
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(port));
            ServerChecker checker = new ServerChecker();
            registry.rebind(IAuthentication.class.getSimpleName(), new Authenticator(checker, context));
            CustomLogger.log("Bound " + IAuthentication.class.getSimpleName());

            registry.rebind(IServerChecker.class.getSimpleName(), checker);
            CustomLogger.log("Bound " + IServerChecker.class.getSimpleName());
            DataAccess.init(context.getPropertyValue("dbConnection"), context.getPropertyValue("dbUserId"), context.getPropertyValue("dbUserToken"));
            new FileServerMonitor(checker, context).start();
            System.out.println("Broker started");
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DBoxBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
