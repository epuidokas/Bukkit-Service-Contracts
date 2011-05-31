package com.bukkit.epuidokas.ServiceContracts;
import java.util.*;
import java.io.*;
import org.bukkit.entity.Player;

/**
 *
 * @author ep
 */
public class ServiceContractsContractor implements Serializable {

    private final int NUM_OF_SEC_PER_MIN = 5;

    private final String playerName;
    private final String contractId;
    private Integer time = 0;
    private Integer taskId = null;

    public ServiceContractsContractor(String player, String contract){
        playerName=player;
        contractId=contract;
    }

    public boolean start (){
        taskId = ServiceContractsPlugin.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(ServiceContractsPlugin.getPlugin(), new Runnable() {
            public void run() {
                logWork();
            }
        }, 20*NUM_OF_SEC_PER_MIN, 20*NUM_OF_SEC_PER_MIN);
        return true;
    }

    public boolean pause (){
        if (taskId != null)
            ServiceContractsPlugin.getPlugin().getServer().getScheduler().cancelTask(taskId);
        return true;
    }

    public boolean logWork (){
        Player player = ServiceContractsPlugin.getPlugin().getServer().getPlayer(playerName);
        if (!(player instanceof Player) || !player.isOnline()) {
            pause();
            Player employer = ServiceContractsPlugin.getPlugin().getServer().getPlayer(ServiceContractsPlugin.getPlugin().getContracts().getContract(contractId).getEmployer());
            if (employer instanceof Player)
                ServiceContractsPlugin.getPlugin().sendPlayerMessage(employer, String.format(ServiceContractsPlugin.getPlugin().getString("CONTRACTOR_OFFLINE"), playerName));
            return false;
        }
        time++;
        ServiceContractsContract contract = ServiceContractsPlugin.getPlugin().getContracts().getContract(contractId);
        if (contract==null) {
            ServiceContractsPlugin.getPlugin().log("Couldn't find contract '" +contractId+ "' for contractor '" +playerName+ "'");
            return false;
        }
        boolean log = ServiceContractsPlugin.getPlugin().getContracts().getContract(contractId).submitTimecard(playerName, time);

        if(!log) {
            ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("CONTRACTOR_PAY_FAILED"));
            ServiceContractsPlugin.getPlugin().sendPlayerMessage(ServiceContractsPlugin.getPlugin().getContracts().getContract(contractId).getEmployer(), String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_PAY_FAILED"), playerName));
            return false;
        }

        return true;
    }

}
