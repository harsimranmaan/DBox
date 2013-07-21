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

    public PPeerDetailsGetter(String myServerName)
    {
        this.myServerName = myServerName;
    }
//    public ServerDetails getServerDetails(String myServerName)
//    {
//        ServerDetails sDetails;
//        try
//        {
//            ResultSet set = DataAccess.getResultSet("SELECT S.* FROM ServerDetails S, ServerDetails my WHERE my.servername='" + myServerName + "' AND S.servername=my.monitoring AND S.clusterId=my.clusterId ORDER BY serverIndex LIMIT 1");
//            if (set != null && set.next())
//            {
//                sDetails = new ServerDetails(set.getString("servername"), set.getInt("portNumber"), set.getInt("clusterId"), set.getInt("serverIndex"));
//
//            }
//            else
//            {
//                sDetails = new ServerDetails(myServerName, 0, 0, 0);
//            }
//        }
//        catch (SQLException ex)
//        {
//            Logger.getLogger(PeerDetailsGetter.class.getName()).log(Level.SEVERE, null, ex);
//            sDetails = new ServerDetails(myServerName, 0, 0, 0);
//        }
//        return sDetails;
//    }

    public ServerDetails getMonitorDetails() throws Exception
    {
        ServerDetails sDetails;
        try
        {
            ResultSet set = DataAccess.getResultSet("SELECT S.* FROM ServerDetails S, ServerDetails my WHERE S.serverIndex=(SELECT MIN(I.serverIndex) FROM ServerDetails I WHERE I.serverIndex > my.serverIndex AND I.clusterId=my.clusterId) AND my.clusterId=S.clusterId AND my.servername='" + myServerName + "' LIMIT 1");
            if (set != null && set.next())
            {
                sDetails = new ServerDetails(set.getString("servername"), set.getInt("portNumber"), set.getInt("clusterId"), set.getInt("serverIndex"));

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
        return sDetails;
    }
}
