/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

/**
 *
 * @author harsimran.maan
 */
public interface IServerDetailsGetter
{

    /**
     * gets the server detail
     * <p/>
     * @return server details
     * <p/>
     * @throws Exception
     */
    ServerDetails getServerDetails() throws Exception;
}
