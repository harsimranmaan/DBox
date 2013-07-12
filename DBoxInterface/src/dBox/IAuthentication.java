/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author harsimran.maan
 */
public interface IAuthentication extends Remote
{

    ClientDetails authenticate(String username, String password) throws RemoteException;
}
