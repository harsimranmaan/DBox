/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.ClientDetails;
import dBox.IAuthentication;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author harsimran.maan
 */
class InteractionManager
{

    private Scanner scanIn;
    private IAuthentication authentication;
    private ClientDetails client;
    private WatchDir directoryWatch;

    /**
     * Prints the login message to console
     */
    private void printloginMessage()
    {
        System.out.println("Please log in before you send the request.");
    }

    /**
     * Prints the message to console
     * <p/>
     * @param message
     */
    private void printMessage(String message)
    {
        System.out.println(message);
    }

    /**
     * Checks if the user is loggedIn
     * <p/>
     */
    private boolean isLoggedIn()
    {
        return client != null;
    }

    /**
     * Prints a warning to console
     * <p/>
     * @param commandName
     */
    private void printWarning(String commandName)
    {
        System.out.print("Invalid parameters or Invalid command '" + commandName);
        System.out.println("'. Use help for syntax.");
    }

    /**
     * Handles Interation with the User
     * <p/>
     * @param stockQuery
     * @param auth
     * @param isAdmin
     */
    public InteractionManager(IAuthentication auth)
    {
        this.authentication = auth;
        System.out.println("------------------------------------------------------");
        System.out.println("|             Welcome to DbLike                      |");
        System.out.println("------------------------------------------------------");
        System.out.println("");
    }

    /**
     * Handles the user command
     * <p/>
     * @param commandString <p/>
     * @throws RemoteException
     */
    private void login(String[] commandString) throws RemoteException
    {
        if (commandString.length == 3)
        {
            client = authentication.authenticate(commandString[1], commandString[2]);
            System.out.println(client.getUserhash());
        }
        else
        {
            printWarning(commandString[0]);
        }
    }

    /**
     * Initializes the Interaction
     * <p/>
     * @throws RemoteException
     */
    public void init() throws RemoteException, IOException
    {

        String command;
        int quantity = 0;
        String[] commandString;
        boolean isExit = false;

        do
        {

            command = getInput();
            commandString = command.split(" ");
            switch (commandString[0])
            {


                case "user":
                    login(commandString);
                    break;
                case "help":
                    if (commandString.length == 1)
                    {
                        printPrompt();
                    }
                    break;
                case "dir":
                    if (commandString.length == 2)
                    {

                        File file = new File(commandString[1]);
                        if (file.isDirectory())
                        {
                            System.out.println(" " + commandString[1]);
                            Path dirpath = Paths.get(commandString[1]);
                            // Instantiate the object
                            directoryWatch = new WatchDir(dirpath, true);
                            // Start reading the given path directory
                            directoryWatch.start();
                        }
                    }
                    else
                    {
                        System.out.println("Wrong input.");
                        printPrompt();
                    }
                    break;
                case "quit":
                    client = null;
                    // Kill the thread
                    if (directoryWatch != null)
                    {
                        directoryWatch.interrupt();
                    }
                    isExit = true;
                    break;
                default:
                    printWarning(commandString[0]);

                    break;
            }
        }
        while (!isExit);
    }

    /**
     * Print command options
     */
    public void printPrompt()
    {
        System.out.println("-----------------------------------------------");
        System.out.println("|                   COMMANDS                   |");
        System.out.println("-----------------------------------------------");
        System.out.println("|               user <user name>               |");
        System.out.println("| Eg.           user johnsmith                 |");


        System.out.println("|                                              |");
        if (isLoggedIn())
        {
            System.out.println("|              dir <directory path>             |");
            System.out.println("| Eg.              dir D:\\bla                  |");
            System.out.println("|                                              |");

            System.out.println("|                                              |");
            System.out.println("|                    quit                      |");

        }
        System.out.println("-----------------------------------------------");

    }

    /**
     *
     * @return command string
     */
    private String getInput()
    {
        scanIn = new Scanner(System.in);
        String input = scanIn.nextLine().trim();//.toLowerCase();
        return input;
    }

    /**
     *
     * @param bal <p/>
     * @return balance in double format
     * <p/>
     * @throws NumberFormatException
     */
    private double getInputAmount(String bal) throws NumberFormatException
    {

        //check if a  valid decimal
        if (!bal.matches("^\\d+$|^[.]?\\d{1,2}$|^\\d+[.]?\\d{1,2}$"))
        {
            throw new NumberFormatException("Invalid entry");
        }
        return Double.valueOf(bal);
    }

    /**
     *
     * @param strInt <p/>
     * @return string in integer format
     * <p/>
     * @throws NumberFormatException
     */
    private int getInteger(String strInt) throws NumberFormatException
    {

        //check if a  valid decimal
        if (!strInt.matches("^[0-9]+$"))
        {
            throw new NumberFormatException("Invalid entry");
        }
        return Integer.valueOf(strInt);
    }
}
