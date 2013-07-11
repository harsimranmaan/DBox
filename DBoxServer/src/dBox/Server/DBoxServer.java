/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Server;

import dBox.IAuthentication;

/**
 *
 * @author harsimran.maan
 */
public class DBoxServer implements IAuthentication
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println(new DBoxServer().authenticate("a", "b"));
    }

    @Override
    public boolean authenticate(String username, String password)
    {
        return true; //To change body of generated methods, choose Tools | Templates.
    }
}
