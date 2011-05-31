package com.bukkit.epuidokas.ServiceContracts;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import org.bukkit.event.server.ServerEvent;
import org.bukkit.event.server.ServerListener;
import com.iConomy.*;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.server.PluginEnableEvent;

/**
 *
 * @author ep
 */
public class ServiceContractsPluginListener extends ServerListener {

    public ServiceContractsPluginListener() {
    }

    @Override
    public void onPluginEnable(PluginEnableEvent event) {
        
        // Link iConomy
        if(ServiceContractsPlugin.getPlugin().getIConomy() == null) {
            Plugin iconomy = ServiceContractsPlugin.getPlugin().getServer().getPluginManager().getPlugin("iConomy");
            if (iconomy != null) {
                if (iconomy.isEnabled() && iconomy.getClass().getName().equals("com.iConomy.iConomy")) {
                    ServiceContractsPlugin.getPlugin().setIConomy((iConomy) iconomy);
                }
            }
        }

        // Link Permissions
        if(ServiceContractsPlugin.getPlugin().getPermissions() == null) {
            Plugin permissions = ServiceContractsPlugin.getPlugin().getServer().getPluginManager().getPlugin("Permissions");
            if (permissions != null) {
                ServiceContractsPlugin.getPlugin().setPermissions((Permissions) permissions);
            }
        }
    }

}
