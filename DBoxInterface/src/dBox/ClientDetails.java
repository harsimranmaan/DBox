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
public class ClientDetails implements Serializable
{

    private static final long serialVersionUID = 1111L;
    private String userhash;
    private int quota;

    public ClientDetails(String hash, int quota)
    {
        this.userhash = hash;
        this.quota = quota;
    }

    /**
     * @return the userHash
     */
    public String getUserhash()
    {
        return userhash;
    }

    /**
     * @return the quota
     */
    public double getQuota()
    {
        return quota;
    }
}
