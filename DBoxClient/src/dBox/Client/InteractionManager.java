/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.ClientDetails;
import dBox.IAuthentication;
import dBox.ServerDetails;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsimran.maan
 */
class InteractionManager
{

    private Scanner scanIn;
    private IAuthentication authentication;
    private ClientDetails client;
    private DirectoryManager directoryWatch;
    private ConfigManager config;
//    private IFileReceiver receiver;

    /**
     * Handles Interaction with the User
     * <p/>
     * <
     * p/>
     */
    public InteractionManager(IAuthentication auth, ConfigManager config)
    {
        this.authentication = auth;
        this.config = config;
        System.out.println("------------------------------------------------------");
        System.out.println("|             Welcome to DbLike                      |");
        System.out.println("------------------------------------------------------");
        System.out.println("");
        //try authentication
        try
        {
            String hash = config.getPropertyValue("hash");
            if (!hash.equals("none"))
            {
                client = auth.authenticate(hash);
                postAuthentication(client);
                String path = config.getPropertyValue("folder");
                if (!path.equals("none"))
                {
                    if (folderExists(path))
                    {
                        setUpDirectoryMonitor();
                    }
                }
            }
        }
        catch (Exception ex)
        {
            CustomLogger.log(ex.getCause().getMessage());
        }
    }

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
     * Handles the user login
     * <p/>
     * @param username
     * @param password
     */
    private void login(String username, String password)
    {
        try
        {
            client = authentication.authenticate(username, password);
            postAuthentication(client);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getCause().getMessage());
        }
    }

    private void postAuthentication(ClientDetails client)
    {
        config.setPropertyValue("hash", client.getUserhash());
        config.setPropertyValue("user", client.getUsername());
        try
        {
            ServerDetails serverDetails = authentication.getServerDetails();
            CustomLogger.log("Server " + serverDetails.getServerName() + " Port " + serverDetails.getPort());
            Registry registry = LocateRegistry.getRegistry(serverDetails.getServerName(), serverDetails.getPort());
            //          receiver = (IFileReceiver) registry.lookup(IFileReceiver.class.getSimpleName());
            //        receiver.setDirectory(client.getUserhash());
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(InteractionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
//        catch (NotBoundException ex)
//        {
//            Logger.getLogger(InteractionManager.class.getName()).log(Level.SEVERE, null, ex);
//        }



    }

    private boolean folderExists(String path)
    {
        File file = new File(path);
        return file.isDirectory();
    }

    private void setUpDirectoryMonitor()
    {
        try
        {
            stopFolderMonitor();
            directoryWatch = new DirectoryManager(config.getPropertyValue("hash"), new ServerDetailsGetter(authentication), config);
            // Start reading the given path directory
            directoryWatch.start();
        }
        catch (IOException ex)
        {
            Logger.getLogger(InteractionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void stopFolderMonitor()
    {
        // Kill the thread
        if (directoryWatch != null)
        {
            directoryWatch.stopMonitor();
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
        String[] commandString;
        boolean isExit = false;
        do
        {
            command = getInput();
            commandString = command.split(" ");
            try
            {
                switch (commandString[0])
                {
                    case "login":
                        if (commandString.length == 3)
                        {
                            login(commandString[1], commandString[2]);
                        }
                        else
                        {
                            printWarning(commandString[0]);
                        }

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
                            String path = commandString[1];
                            if (folderExists(path))
                            {
                                CustomLogger.log("Monitoring " + commandString[1]);
                                config.setPropertyValue("folder", commandString[1]);
                                setUpDirectoryMonitor();

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
                        stopFolderMonitor();
                        isExit = true;
                        break;
                    default:
                        printWarning(commandString[0]);

                        break;
                }
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
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
        System.out.println("|               login <user name> < pwd >      |");
        System.out.println("| Eg.           login johnsmith  secret        |");


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
        String prompt = "$ ";
        if (client != null)
        {
            prompt = client.getUsername() + prompt;
        }
        System.out.print(prompt);
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
