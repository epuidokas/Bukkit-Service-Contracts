package com.bukkit.epuidokas.ServiceContracts;
import java.util.*;
import java.io.*;
import org.bukkit.entity.Player;

/**
 *
 * @author ep
 */
public class ServiceContractsContractor {
    private final String playerName;
    private final String contractId;
    private final ServiceContractsPlugin plugin;
    private Integer time = 0;
    private Integer taskId;

    public ServiceContractsContractor(ServiceContractsPlugin instance, String player, String contract){
        plugin = instance;
        playerName=player;
        contractId=contract;
    }

    public boolean start (){
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                logWork();
            }
        }, 20*10, 20*10);
        return true;
    }

    public boolean pause (){
        plugin.getServer().getScheduler().cancelTask(taskId);
        return true;
    }

    public boolean logWork (){
        Player player = plugin.getServer().getPlayer(playerName);
        if (!(player instanceof Player)) {
            plugin.log("Couldn't find player '" +playerName+ "' for contract '" +contractId+ "'; logWork failed");

        }
        if (!player.isOnline()){
            pause();
            plugin.getServer().getPlayer(plugin.getContracts().getContract(contractId).getEmployer()).sendMessage(String.format(plugin.getString("CONTRACTOR_OFFLINE"), playerName));
            return false;
        }
        time++;
        ServiceContractsContract contract = plugin.getContracts().getContract(contractId);
        if (contract==null) {
            plugin.log("Couldn't find contract '" +contractId+ "' for contractor '" +playerName+ "'");
            return false;
        }
        plugin.getContracts().getContract(contractId).submitTimecard(playerName, time);
        return true;
    }

}
