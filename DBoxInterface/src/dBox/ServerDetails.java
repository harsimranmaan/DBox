/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.Serializable;

/**
 * Class represents the server property
 * <p/>
 * @author harsimran.maan
 */
public class ServerDetails implements Serializable
{

    private static final long serialVersionUID = 11211L;
    private String serverName;
    private int port;

    /**
     * initiates the server name and port
     * <p/>
     * @param serverName
     * @param port
     */
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
