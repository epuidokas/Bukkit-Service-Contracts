package com.bukkit.epuidokas.ServiceContracts;
import org.bukkit.event.server.ServerListener;
import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.server.PluginEnableEvent;

/**
 *
 * @author ep
 */
public class ServiceContractsPluginListener extends ServerListener {
    private ServiceContractsPlugin plugin;

    public ServiceContractsPluginListener(ServiceContractsPlugin instance) {
        plugin = instance;
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        
        // Link iConomy
        if(plugin.getIConomy() == null) {
            Plugin iconomy = plugin.getServer().getPluginManager().getPlugin("iConomy");
            if (iconomy != null) {
                plugin.setIConomy((iConomy)iconomy);
            }
        }

        // Link Permissions
        if(plugin.getPermissions() == null) {
            Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (permissions != null) {
                plugin.setPermissions((Permissions)permissions);
            }
        }
    }
}
