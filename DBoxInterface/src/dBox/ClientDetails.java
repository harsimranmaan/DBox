/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.io.Serializable;

/**
 * Interface for the Client details
 * <p/>
 * @author harsimran.maan
 */
public class ClientDetails implements Serializable
{

    private static final long serialVersionUID = 1111L;
    private String username;
    private String userhash;
    private int quota;
    private int clusterId;

    /**
     * Setting up the client details
     * <p/>
     * @param username
     * @param hash
     * @param quota
     * @param clusterId
     */
    public ClientDetails(String username, String hash, int quota, int clusterId)
    {
        this.username = username;
        this.userhash = hash;
        this.quota = quota;
        this.clusterId = clusterId;
    }

    /**
     * @return the userHash
     */
    public String getUserhash()
    {
        return userhash;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @return the quota
     */
    public int getQuota()
    {
        return quota;
    }

    /**
     * @return the clusterId
     */
    public int getClusterId()
    {
        return clusterId;
    }
}
