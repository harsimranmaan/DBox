/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.IAuthentication;

/**
 *
 * @author harsimran.maan
 */
public class DBoxClient implements IAuthentication
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println(new DBoxClient().authenticate("a", "b"));
    }

    @Override
    public boolean authenticate(String username, String password)
    {
        return false;
    }
}
