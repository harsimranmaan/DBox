/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.IAuthentication;
import dBox.IServerDetailsGetter;
import dBox.ServerDetails;

/**
 * gives the server details to the client upon request
 * <p/>
 * @author harsimran.maan
 */
public class ServerDetailsGetter implements IServerDetailsGetter
{

    private IAuthentication auth;

    /**
     * initiates authenticate to receive server detail
     * <p/>
     * @param auth
     */
    public ServerDetailsGetter(IAuthentication auth)
    {
        this.auth = auth;
    }

    /**
     * gives server detail to the user
     * <p/>
     * @return server details
     * <p/>
     * @throws Exception
     */
    @Override
    public ServerDetails getServerDetails(int clusterId) throws Exception
    {
        return auth.getServerDetails(clusterId);
    }
}
