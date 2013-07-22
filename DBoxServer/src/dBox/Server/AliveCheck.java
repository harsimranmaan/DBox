/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ServerUtils.DataAccess;
import dBox.ServerUtils.IServerChecker;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class to start the heart beat of the server
 * <p/>
 * @author harsimran.maan
 */
public class AliveCheck extends Thread
{

    private String server;
    //  private final int clusterId;
    //private int serverIndex;
    private final IServerChecker checker;
    private final int port;
    private final int clusterId;
    private final ConfigManager config;

    /**
     * Initiates the AliveCkeck properties
     * <p/>
     * @param checker
     * @param config
     * @param server
     * @param port
     * @param clusterId
     */
    AliveCheck(IServerChecker checker, ConfigManager config, String server, int port, int clusterId)
    {
        this.checker = checker;
        this.server = server;
        this.port = port;
        this.clusterId = clusterId;
        this.config = config;
        CustomLogger.log("AliveCheck > AliveCheck : server " + server + " port " + port + " clusterId " + clusterId);
        try
        {
            checker.ping(server, port, clusterId);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(AliveCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The function is to start sending the heart beat
     */
    private void startHeartbeat()
    {
        while (true)
        {
            try
            {
                checker.ping(server, port, clusterId);
            }
            catch (RemoteException ex)
            {
                Logger.getLogger(AliveCheck.class.getName()).log(Level.SEVERE, null, ex);
            }

            try
            {
                Thread.sleep(Integer.parseInt(config.getPropertyValue("serverPingInterval")));
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(AliveCheck.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Thread to start the heart beat
     */
    @Override
    public void run()
    {
        this.startHeartbeat();
    }
}
