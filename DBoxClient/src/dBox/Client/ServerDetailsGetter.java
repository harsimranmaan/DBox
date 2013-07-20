/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.IAuthentication;
import dBox.IServerDetailsGetter;
import dBox.ServerDetails;
import dBox.utils.ConfigManager;
import java.rmi.RemoteException;

/**
 * gives the server detail to the client upon request
 * <p/>
 * @author harsimran.maan
 */
public class ServerDetailsGetter implements IServerDetailsGetter
{

    private IAuthentication auth;
    private final ConfigManager config;

    /**
     * initiates authenticate to receive server detail
     * <p/>
     * @param auth
     */
    public ServerDetailsGetter(IAuthentication auth, ConfigManager config)
    {
        this.auth = auth;
        this.config = config;
    }

    /**
     * gives server detail for the authentic user
     * <p/>
     * @return server details
     * <p/>
     * @throws Exception
     */
    @Override
    public ServerDetails getServerDetails() throws Exception
    {
        return auth.getServerDetails(Integer.parseInt(config.getPropertyValue("clusterId")));
    }
}
