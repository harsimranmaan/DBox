/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ServerDetails;
import dBox.ServerUtils.IServerChecker;

/**
 *
 * @author harsimran.maan
 */
public class PeerDetailsGetter
{

    private final String myServerName;
    private final IServerChecker checker;
    private final int clusterId;

    /**
     * Initiates the peerDetailsGetter properties
     * <p/>
     * @param server
     * @param checker
     * @param clusterId
     */
    PeerDetailsGetter(String server, IServerChecker checker, int clusterId)
    {
        this.checker = checker;
        this.myServerName = server;
        this.clusterId = clusterId;
    }

    /**
     * To get the monitor server details
     * <p/>
     * @return server details
     * <p/>
     * @throws Exception
     */
    public ServerDetails getMonitorDetails() throws Exception
    {
        return checker.getMonitor(myServerName, clusterId);
    }
}
