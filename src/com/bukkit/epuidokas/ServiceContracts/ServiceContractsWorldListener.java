package com.bukkit.epuidokas.ServiceContracts;

import org.bukkit.event.world.WorldEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ep
 * Date: 5/30/11
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceContractsWorldListener extends WorldListener {

    public ServiceContractsWorldListener(){

    }

    public void onWorldSave(WorldSaveEvent event){
        ServiceContractsPlugin.getPlugin().backupAllData();
    }
}
