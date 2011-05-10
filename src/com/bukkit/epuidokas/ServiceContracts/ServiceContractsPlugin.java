package com.bukkit.epuidokas.ServiceContracts;

import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.*;
import java.util.*;

/**
 *
 * @author ep
 */
public class ServiceContractsPlugin extends JavaPlugin {

    private iConomy iConomy = null;
    private PermissionHandler permissions = null;
    private final ServiceContractsContracts contracts = new ServiceContractsContracts(this);
    private final ServiceContractsPlayerListener playerListener = new ServiceContractsPlayerListener(this);
    private final ServiceContractsPluginListener pluginListener = new ServiceContractsPluginListener(this);
    private final Properties strings = new Properties();
    private final boolean debugMode = true;

    public void onEnable() {

        // Set up plugin directory
        getDataFolder().mkdir();
        getDataFolder().setWritable(true);
        getDataFolder().setExecutable(true);

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
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Event.Priority.Low, this);

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
        if (!actual.exists()) {
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

                    log("Extracted file: " + name);
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

    public iConomy getIConomy() {
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

    public PermissionHandler getPermissions() {
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

}
