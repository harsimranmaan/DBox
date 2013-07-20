/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.ServerDetails;
import dBox.ServerUtils.DataAccess;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
public class PeerDetailsGetter
{

    public ServerDetails getServerDetails(String myServerName) throws Exception
    {
        ServerDetails sDetails;
        try
        {
            ResultSet set = DataAccess.getResultSet("SELECT S.* FROM ServerDetails S, ServerDetails my WHERE my.servername='" + myServerName + "' AND S.servername=my.monitoring AND S.clusterId=my.clusterId ORDER BY serverIndex LIMIT 1");
            if (set != null && set.next())
            {
                sDetails = new ServerDetails(set.getString("servername"), set.getInt("portNumber"), set.getInt("clusterId"), set.getInt("serverIndex"));
                return sDetails;
            }
            else
            {
                throw new Exception("No monitor");
            }
        }
        catch (SQLException ex)
        {
            Logger.getLogger(PeerDetailsGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("No monitor");
        }
    }
}
