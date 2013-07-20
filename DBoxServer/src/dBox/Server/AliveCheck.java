/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ServerUtils.DataAccess;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class AliveCheck extends Thread
{

    private String server;
    private final int clusterId;

    public AliveCheck(String server, int port, int clusterId)
    {
        this.server = server;
        this.clusterId = clusterId;
        try
        {
            DataAccess.updateOrInsertSingle("INSERT INTO ServerDetails VALUES('" + server + "'," + port + ",(SELECT m FROM (SELECT IFNULL(MAX(serverIndex),0)+1 AS m FROM ServerDetails WHERE clusterId = " + clusterId + " ) AS M), now()," + clusterId + ",(SELECT m FROM (SELECT MAX(serverIndex)+1 AS m FROM ServerDetails WHERE clusterId = " + clusterId + ") AS M))");
        }
        catch (SQLException ex)
        {
            Logger.getLogger(AliveCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void startHeartbeat()
    {
        while (true)
        {
            try
            {
                DataAccess.updateOrInsertSingle("UPDATE ServerDetails SET lastCheck=now() WHERE servername='" + server + "'");
            }
            catch (SQLException ex)
            {
                Logger.getLogger(AliveCheck.class.getName()).log(Level.SEVERE, null, ex);
            }
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(AliveCheck.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void run()
    {
        this.startHeartbeat();
    }
}
