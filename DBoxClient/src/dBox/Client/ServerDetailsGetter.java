/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.IAuthentication;
import dBox.IServerDetailsGetter;
import dBox.ServerDetails;
import java.rmi.RemoteException;

/**
 *
 * @author harsimran.maan
 */
public class ServerDetailsGetter implements IServerDetailsGetter
{

    private IAuthentication auth;

    public ServerDetailsGetter(IAuthentication auth)
    {
        this.auth = auth;
    }

    @Override
    public ServerDetails getServerDetails() throws Exception
    {
        return auth.getServerDetails();
    }
}
