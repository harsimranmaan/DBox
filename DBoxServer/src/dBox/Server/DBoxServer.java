/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.IAuthentication;
import dBox.IFileServer;
import dBox.ServerUtils.DataAccess;
import dBox.ServerUtils.IServerChecker;
import dBox.ServerUtils.IServersync;
import dBox.ServerUtils.MetaData;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.File;
import java.rmi.AccessException;
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
public class DBoxServer
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws AccessException, NotBoundException
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
            String broker;
            int brokerPort = Integer.parseInt(context.getPropertyValue("brokerPort"));
            if (isDebug)
            {
                server = context.getPropertyValue("localserver");
                broker = context.getPropertyValue("brokerServerLocal");

            }
            else
            {
                broker = context.getPropertyValue("brokerServer");
                server = MetaData.get(context.getPropertyValue("meta") + context.getPropertyValue("host"));
            }
            System.setProperty("java.rmi.server.hostname", server);
            System.setProperty("java.net.preferIPv4Stack", "true");
            // Bind the remote object in the registry
            int port = Integer.parseInt(context.getPropertyValue("port"));
            int clusterId = Integer.parseInt(context.getPropertyValue("clusterId"));
            CustomLogger.log("Starting server " + server + " on port " + port + " on cluster " + clusterId);
            Registry registryBrok = LocateRegistry.getRegistry(broker, brokerPort);
            IServerChecker checker = (IServerChecker) registryBrok.lookup(IServerChecker.class.getSimpleName());

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(IFileServer.class.getSimpleName(), new FileServer(new PeerDetailsGetter(server, checker, clusterId)));
            CustomLogger.log("Bound " + IFileServer.class.getSimpleName());



            DataAccess.init(context.getPropertyValue("dbConnection"), context.getPropertyValue("dbUserId"), context.getPropertyValue("dbUserToken"));
            new AliveCheck(checker, context, server, port, clusterId).start();
            System.out.println("Server started");
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DBoxServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
