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
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import com.nijiko.coelho.iConomy.iConomy;

/**
 *
 * @author ep
 */
public class ServiceContractsPlayerListener extends PlayerListener {

    private final String PERMISSIONS_HELP = "servicecontracts.help";
    private final String PERMISSIONS_NEW = "servicecontracts.new";
    private final String PERMISSIONS_CLOSE = "servicecontracts.close";
    private final String PERMISSIONS_CLOSE_ANY = "servicecontracts.close.any";
    private final String PERMISSIONS_OPEN = "servicecontracts.open";
    private final String PERMISSIONS_OPEN_ANY = "servicecontracts.open.any";
    private final String PERMISSIONS_REMOVE = "servicecontracts.remove";
    private final String PERMISSIONS_REMOVE_ANY = "servicecontracts.remove.any";
    private final String PERMISSIONS_APPLY = "servicecontracts.apply";
    private final String PERMISSIONS_EMPLOY = "servicecontracts.employ";
    private final String PERMISSIONS_EMPLOY_ANY = "servicecontracts.employ.any";
    private final String PERMISSIONS_START_PAUSE = "servicecontracts.start_pause";
    private final String PERMISSIONS_START_PAUSE_ANY = "servicecontracts.start_pause.any";
    private final String PERMISSIONS_FIRE = "servicecontracts.fire";
    private final String PERMISSIONS_FIRE_ANY = "servicecontracts.fire.any";
    private final String PERMISSIONS_INFO = "servicecontracts.info";
    private final String PERMISSIONS_INFO_ANY = "servicecontracts.info.any";

    private final ServiceContractsPlugin plugin;
    private HashMap<String,Integer> playerStates = new HashMap<String,Integer>();
    private HashMap<String,String> playerStatesData = new HashMap<String,String>();
    private HashMap<String,ServiceContractsContract> newContracts = new HashMap<String,ServiceContractsContract>();


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
            Player player = event.getPlayer();
            switch(command.getAction()) {
                // Help
                case 0:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_HELP)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_HELP));
                        break;
                    }
                    ArrayList<String> commands = command.getCommandFormats(true);
                    for(int i =0; i < commands.size(); i++) {
                        player.sendMessage(commands.get(i));
                    }
                    break;
                // New
                case 1:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_NEW)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_NEW));
                        break;
                    }
                    ServiceContractsContract contract = new ServiceContractsContract(plugin, player, command);
                    newContracts.put(player.getName(), contract);
                    playerStates.put(player.getName(), 1);
                    player.sendMessage(plugin.getString("SELECT_SIGN"));
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                // Apply
                case 5:
                    ServiceContractsContract applyContract = plugin.getContracts().getContract(playerStatesData.get(player.getName()));
                    Player employer = plugin.getServer().getPlayer(applyContract.getEmployer());
                    // @todo l10n
                    player.sendMessage("Application submitted!");
                    employer.sendMessage(player.getName() + " has applied for your " + plugin.getString("TYPE_" + applyContract.getType()) + " contract.");
                    employer.sendMessage("To accept, type '/sc -e " + player.getName() + " " + applyContract.getId() + "'");
                    break;
            }
        }
        catch(Exception e) {
            //event.getPlayer().sendMessage(e.toString());
            event.getPlayer().sendMessage(e.getMessage());
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block.getTypeId() != 68 || event.isCancelled())
            return;
        Player player = event.getPlayer();
        String playerName = player.getName();
        Sign sign = (Sign)block.getState();
        int playerState = 0;
        if (playerStates.containsKey(playerName))
            playerState = playerStates.get(playerName);
        switch(playerState) {
            // New
            case 1:
                // @todo verify this clicked block is a valid place to add the contract
                ServiceContractsContract newContract = newContracts.get(playerName);
                newContract.setId(sign);
                newContract.drawSign(sign);
                plugin.getContracts().addContract(newContract);
                newContracts.remove(playerName);
                playerStates.remove(playerName);
                player.sendMessage(plugin.getString("CONTRACT_CREATED"));
                break;
            // No state (applying)
            default:
                String id = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                plugin.log(id);
                ServiceContractsContract contract = plugin.getContracts().getContract(id);
                if(contract instanceof ServiceContractsContract) {
                    // @todo get the '/sc -a' string from the ServiceContractsCommand class
                    player.sendMessage(String.format(plugin.getString("APPLY"),"/sc -a"));
                    playerStates.put(playerName, 2);
                    playerStatesData.put(playerName, id);
                }
        }

    }
}
