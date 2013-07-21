/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dBox.Client;

import dBox.ClientDetails;
import dBox.IAuthentication;
import dBox.utils.ConfigManager;
import dBox.utils.CustomLogger;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
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
        System.out.println("             Use help for options                     ");
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

    /**
     * operates post authentication procedures
     * <p>
     * <p/>
     * @param client </p>
     */
    private void postAuthentication(ClientDetails client)
    {
        config.setPropertyValue("hash", client.getUserhash());
        config.setPropertyValue("user", client.getUsername());
        config.setPropertyValue("clusterId", Integer.toString(client.getClusterId()));

    }

    /**
     * Checks the existence of the folder
     * <p/>
     * @param path <p/>
     * @return folder existence in boolean format
     */
    private boolean folderExists(String path)
    {
        File file = new File(path);
        return file.isDirectory();
    }

    /**
     * Starts directory watcher thread
     */
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

    /**
     * Stops the directory watcher thread
     */
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
                        if (!isLoggedIn())
                        {
                            if (commandString.length == 3)
                            {
                                login(commandString[1], commandString[2]);
                                printMessage("Please set the directory using the dir command.");
                            }
                            else
                            {
                                printWarning(commandString[0]);
                            }
                        }
                        else
                        {
                            printMessage("Already logged in");
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
                            if (isLoggedIn())
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
                                printWarning("Please login before setting the directory");
                            }
                        }
                        else
                        {
                            System.out.println("Wrong input.");
                            printPrompt();
                        }
                        break;
                    case "logout":
                        client = null;
                        stopFolderMonitor();
                        config.setPropertyValue("hash", "none");
                        config.setPropertyValue("user", "none");
                        config.setPropertyValue("folder", "none");
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
            System.out.println("| Eg.              dir D:\\somePath             |");
            System.out.println("|                                               |");

            System.out.println("|                  logout                       |");
            System.out.println("|                                               |");

            System.out.println("|                                              |");
            System.out.println("|                    quit                      |");

        }
        System.out.println("-----------------------------------------------");

    }

    /**
     * prepares command prompt for the user
     * <p/>
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
        String input = scanIn.nextLine().trim();
        return input;
    }
}
