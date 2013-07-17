/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.Serializable;

/**
 *
 * @author harsimran.maan
 */
public class ServerDetails implements Serializable
{

    private static final long serialVersionUID = 11211L;
    private String serverName;
    private int port;

    public ServerDetails(String serverName, int port)
    {
        this.serverName = serverName;
        this.port = port;
    }

    /**
     * @return the serverName
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return port;
    }
}
