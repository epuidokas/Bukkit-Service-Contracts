package com.bukkit.epuidokas.ServiceContracts;

import com.iConomy.system.Account;
import com.iConomy.system.BankAccount;
import com.iConomy.system.Holdings;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import com.iConomy.*;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.*;
import java.util.*;

/**
 *
 * @author ep
 */
public class ServiceContractsPlugin extends JavaPlugin {

    private static iConomy iConomy = null;
    private static PermissionHandler permissions = null;
    private ServiceContractsContracts contracts = new ServiceContractsContracts();
    private HashMap<String,String> contractors = new HashMap();
    private HashMap<String,ArrayList<String>> applicants = new HashMap();
    private final ServiceContractsPlayerListener playerListener = new ServiceContractsPlayerListener();
    private final ServiceContractsPluginListener pluginListener = new ServiceContractsPluginListener();
    private final ServiceContractsBlockListener blockListener = new ServiceContractsBlockListener();
    private final ServiceContractsWorldListener worldListener = new ServiceContractsWorldListener();
    private HashMap<Integer,String> contractIdMapping = new HashMap();
    private Integer lastContractId = 0;
    private final Properties strings = new Properties();
    private boolean debugMode = false;
    private static ServiceContractsPlugin plugin;

    public void onEnable() {

        plugin = this;

        // Set up plugin directory
        getDataFolder().mkdir();
        getDataFolder().setWritable(true);
        getDataFolder().setExecutable(true);

        // Extract software license
        extractFile("/README");

        // Load string translations
        // @TODO make en-US configurable and support other languages
        extractFile("/strings_en-US.properties");
        try {
            strings.load(new FileInputStream(this.getDataFolder() + File.separator +"strings_en-US.properties"));
        }
        catch(IOException e) {
            e.printStackTrace();
            log("loading FAILED");
            return;
        }

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.WORLD_SAVE, worldListener, Event.Priority.Highest, this);

        restoreAllData();

        // Load successful
        log("loaded");
    }

    public boolean inDebugMode(){
        return debugMode;
    }

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        log("disabled");
    }

    public String getString(String key) {
        return strings.getProperty(key, "");
    }

    private void extractFile(String name) {
        File actual = new File(getDataFolder(), name);
        if (!actual.exists()  || debugMode) {
            InputStream input = this.getClass().getResourceAsStream( name);
            if (input != null) {
                FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;

                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

                    log("Extracted file: " + actual.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (Exception e) { }
                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e) { }
                }
            }
        }
    }

    public void log(String message) {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("["+pdfFile.getName()+"]["+pdfFile.getVersion()+"] " + message);
    }

    public static iConomy getIConomy() {
        return iConomy;
    }

    public boolean setIConomy(iConomy instance) {
        if (instance.isEnabled()) {
            iConomy = instance;
            log("successfully linked with iConomy");
            return true;
        }
        else {
            return false;
        }
    }

    public static PermissionHandler getPermissions() {
        return permissions;
    }

    public boolean setPermissions(Permissions instance) {
        if (instance.isEnabled()) {
            permissions = instance.getHandler();
            log("successfully linked with Permissions");
            return true;
        }
        else {
            return false;
        }

    }

    public ServiceContractsContracts getContracts(){
        return contracts;
    }

    public String getContractByContractor(String contractor) {
        return contractors.get(contractor);
    }

    public String setContractByContractor(String contractor, String contract) {
        return contractors.put(contractor, contract);
    }

    public String removeContractByContractor(String contractor) {
        return contractors.remove(contractor);
    }

    public ArrayList<String> getContractsByApplicant(String contractor) {
        return applicants.get(contractor);
    }

    public boolean addContractByApplicant(String contractor, String contract) {
        ArrayList<String> contracts = getContractsByApplicant(contractor);
        if (contracts != null) {
            contracts.add(contract);
        }
        else {
            contracts = new ArrayList();
        }
        applicants.put(contractor, contracts);
        return true;
    }

    public boolean removeApplicant(String contractor) {
        applicants.remove(contractor);
        return true;
    }

    public void sendPlayerMessage(String playerId, String message) {
        Player player = getServer().getPlayer(playerId);
        if (player instanceof Player)
            player.sendMessage(message);
    }

    public void sendPlayerMessage(Player player, String message) {
        if (player instanceof Player)
            player.sendMessage(message);
    }

    public void handleException (Exception e, String playerName) {
        boolean actualError = true;
        String errorString = e.getMessage();

        // If the errorString only contains numbers, it's an actual error
        if (errorString != null && errorString.length() != 0) {
            for (int i = 0; i < errorString.length(); i++) {
                if (!Character.isDigit(errorString.charAt(i))) {
                    actualError = false;
                    break;
                }
            }
        }

        if (!actualError) {
            sendPlayerMessage(playerName, errorString);
        }
        else{
            log(e.toString());
            e.printStackTrace();
            if (inDebugMode()){
                Writer result = new StringWriter();
                PrintWriter printWriter = new PrintWriter(result);
                e.printStackTrace(printWriter);
                String[] stackTrace = result.toString().split(System.getProperty("line.separator"));
                for (int i = 0; i<stackTrace.length && i<4;i++){
                    getServer().broadcastMessage(stackTrace[i]);
                }
            }
        }
    }

    public String contractIdIntToStr (Integer contractId) {
        return contractIdMapping.get(contractId);
    }

    public Integer addContractId (String contractId) {
        lastContractId++;
        contractIdMapping.put(lastContractId, contractId);
        return lastContractId;
    }

    public static Holdings getPlayerHoldings (String playerId) {
        Account account = getIConomy().getAccount(playerId);
        if(account.getMainBankAccount() != null) {
            return account.getMainBankAccount().getHoldings();
        }
        return account.getHoldings();
    }

    public static ServiceContractsPlugin getPlugin(){
        return plugin;
    }

    public boolean backupAllData() {
        String filepath = this.getDataFolder() + File.separator;
        backupData(filepath +"contracts.bak", contracts);
        backupData(filepath +"contractors.bak", contractors);
        backupData(filepath +"applicants.bak", applicants);
        backupData(filepath +"contractIdMapping.bak", contractIdMapping);
        backupData(filepath +"lastContractId.bak", lastContractId);
        log("back-up saved!");
        return true;
    }

    private boolean backupData(String filename, Object data){
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try
        {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(data);
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public boolean restoreAllData() {
        String filepath = this.getDataFolder() + File.separator;
        if((new File(filepath +"contracts.bak")).exists()){
            contracts = (ServiceContractsContracts) restoreData(filepath +"contracts.bak");
            contractors = (HashMap<String,String>) restoreData(filepath +"contractors.bak");
            applicants = (HashMap<String,ArrayList<String>>) restoreData(filepath +"applicants.bak");
            contractIdMapping = (HashMap<Integer,String>) restoreData(filepath +"contractIdMapping.bak");
            lastContractId = (Integer) restoreData(filepath +"lastContractId.bak");
            log("back-up data restored!");
        }
        else
        {
            log("no back-up to restore.");
        }
        return true;
    }

    private Object restoreData(String filename){
        if((new File(filename)).exists()){
            Object data = null;
            FileInputStream fis = null;
            ObjectInputStream in = null;
            try
            {
                fis = new FileInputStream(filename);
                in = new ObjectInputStream(fis);
                data = in.readObject();
                in.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            catch(ClassNotFoundException ex)
            {
                ex.printStackTrace();
            }
            return data;
        }
        else
        {
            return null;
        }
    }
}
