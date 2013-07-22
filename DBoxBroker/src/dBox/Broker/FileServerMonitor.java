/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Broker;

import dBox.ServerDetails;
import dBox.ServerUtils.DataAccess;
import dBox.utils.ConfigManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class to monitor the servers
 * <p/>
 * @author harsimran.maan
 */
public class FileServerMonitor extends Thread
{

    private final ServerChecker checker;
    private final ConfigManager config;

    /**
     * Initiates the class property
     * <p/>
     * @param checker
     * @param config
     */
    public FileServerMonitor(ServerChecker checker, ConfigManager config)
    {
        this.checker = checker;
        this.config = config;
    }

    /**
     * Thread to start monitoring the file server in the cluster
     */
    @Override
    public void run()
    {
        int timeout = Integer.parseInt(config.getPropertyValue("serverTimeout"));
        while (true)
        {
            try
            {
                HashMap<String, ServerDetails> serverDetails = checker.getServerDetails();
                for (ServerDetails server : serverDetails.values())
                {
                    if (server.isTimedOut(timeout))
                    {
                        serverDetails.remove(server.getServerName());
                    }
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(FileServerMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            try
            {
                Thread.sleep(Integer.parseInt(config.getPropertyValue("brokerInterval")));
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(FileServerMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
