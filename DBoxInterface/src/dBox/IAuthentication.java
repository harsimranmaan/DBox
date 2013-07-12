/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.rmi.RemoteException;

/**
 *
 * @author harsimran.maan
 */
public interface IAuthentication
{

    ClientDetails authenticate(String username, String password) throws RemoteException;
}
