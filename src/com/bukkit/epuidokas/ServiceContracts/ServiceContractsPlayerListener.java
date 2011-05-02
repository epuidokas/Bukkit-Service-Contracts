package com.bukkit.epuidokas.ServiceContracts;

import java.io.*;
import java.util.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import com.nijiko.coelho.iConomy.iConomy;

/**
 *
 * @author ep
 */
public class ServiceContractsPlayerListener extends PlayerListener {

    private final ServiceContractsPlugin plugin;

    public ServiceContractsPlayerListener(ServiceContractsPlugin instance) {
        plugin = instance;
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        if (event.isCancelled() || !event.getMessage().startsWith("/sc"))
            return;
        else
            event.setCancelled(true);

        try{
            ServiceContractsCommand command = new ServiceContractsCommand(plugin, event.getMessage());
            switch(command.getAction()) {
                case 0:
                    String commands[] = command.getCommandFormats(true);
                    for(int i =0; i < commands.length; i++) {
                        event.getPlayer().sendMessage(commands[i]);
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
        }
        catch(Exception e) {
            event.getPlayer().sendMessage(e.getMessage());
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.isCancelled() || event.getClickedBlock().getTypeId() != 68)
            return;


    }
}
