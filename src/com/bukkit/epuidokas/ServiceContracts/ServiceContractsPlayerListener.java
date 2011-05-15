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
    private final String PERMISSIONS_QUIT = "servicecontracts.quit";
    private final String PERMISSIONS_QUIT_ANY = "servicecontracts.quit.any";
    private final String PERMISSIONS_MODIFY = "servicecontracts.modify";
    private final String PERMISSIONS_MODIFY_ANY = "servicecontracts.modify.any";
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
                // Close
                case 2:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_CLOSE) && !plugin.getPermissions().has(player, PERMISSIONS_CLOSE_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_CLOSE));
                        break;
                    }
                    player.sendMessage(plugin.getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),3);
                    break;
                // Open
                case 3:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_OPEN) && !plugin.getPermissions().has(player, PERMISSIONS_OPEN_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_OPEN));
                        break;
                    }
                    player.sendMessage(plugin.getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),4);
                    break;
                // Remove
                case 4:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_REMOVE) && !plugin.getPermissions().has(player, PERMISSIONS_REMOVE_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_REMOVE));
                        break;
                    }
                    player.sendMessage(plugin.getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),5);
                    break;
                // Apply
                case 5:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_APPLY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_APPLY));
                        break;
                    }
                    String playerStateData = playerStatesData.remove(player.getName());
                    if (playerStateData == null) {
                        player.sendMessage(plugin.getString("SELECT_CONTRACT_FIRST"));
                        break;
                    }
                    ServiceContractsContract applyContract = plugin.getContracts().getContract(playerStateData);
                    Player employer = plugin.getServer().getPlayer(applyContract.getEmployer());
                    if (!plugin.inDebugMode() && employer.getName().contentEquals(player.getName())) {
                        player.sendMessage(plugin.getString("EMPLOYER_APPLY"));
                        break;
                    }
                    if (plugin.getContractByContractor(player.getName()) != null) {
                        // @todo l10n
                        player.sendMessage("You're already employed!");
                        break;
                    }
                    // @todo l10n
                    // @todo actually do something with the applicant's message
                    if (applyContract.getOpenings() > 0) {
                        if (applyContract.hasApplicant(player.getName())) {
                            // already applied
                            player.sendMessage("You've already applied for this contract!");
                        }
                        else {
                            applyContract.addApplicant(player.getName());
                            player.sendMessage("Application submitted!");
                            employer.sendMessage(player.getName() + " has applied for your " + plugin.getString("TYPE_" + applyContract.getType()) + " contract.");
                            employer.sendMessage("To accept, type '/sc -e " + applyContract.getId() + " " + player.getName() + "'");
                        }
                    }
                    else {
                        player.sendMessage(plugin.getString("NO_OPENINGS"));
                    }
                    break;
                // Employ
                case 6:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_EMPLOY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_EMPLOY));
                        break;
                    }
                    ServiceContractsContract employContract = plugin.getContracts().getContract(command.getContract());
                    if (employContract == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!employContract.getEmployer().contentEquals(player.getName()) && !plugin.getPermissions().has(player, PERMISSIONS_EMPLOY_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_EMPLOY_ANY));
                        break;  
                    }
                    String contractorName = command.getPlayer();
                    if (contractorName == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    Player employEmployer = plugin.getServer().getPlayer(employContract.getEmployer());
                    if (!employContract.addContractor(contractorName)) {
                        player.sendMessage(String.format(plugin.getString("EMPLOYER_EMPLOY_ERROR"), contractorName));
                        break;
                    }

                    // Remove the contractor's applications for all other contracts
                    ArrayList<String> applicantContracts = plugin.getContractsByApplicant(contractorName);
                    ServiceContractsContracts allContracts = plugin.getContracts();
                    if (applicantContracts instanceof ArrayList) {
                        for(int i=0;i < applicantContracts.size();i++){
                            player.sendMessage("foo" + i);
                            ServiceContractsContract applicantContract = allContracts.getContract(applicantContracts.get(i));
                            if (applicantContract instanceof ServiceContractsContract)
                                applicantContract.removeApplicant(contractorName);
                        }
                    }
                    // @todo l10n
                    employEmployer.sendMessage("Type `/sc -s " + employContract.getId() + " " + contractorName + "` to start paying them.");
                    break;
                // Fire
                case 7:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_FIRE)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_FIRE));
                        break;
                    }
                    ServiceContractsContract fireContract = plugin.getContracts().getContract(command.getContract());
                    if (fireContract == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!fireContract.getEmployer().contentEquals(player.getName()) && !plugin.getPermissions().has(player, PERMISSIONS_FIRE_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_FIRE_ANY));
                        break;  
                    }
                    String fireContractorName = command.getPlayer();
                    if (fireContractorName == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    if (!fireContract.removeContractor(fireContractorName)) {
                        player.sendMessage(String.format(plugin.getString("EMPLOYER_FIRED_ERROR"), fireContractorName));
                        break;
                    }
                    player.sendMessage(String.format(plugin.getString("EMPLOYER_FIRED"), fireContractorName));
                    plugin.getServer().getPlayer(fireContractorName).sendMessage(String.format(plugin.getString("CONTRACTOR_FIRED"), fireContractorName));
                    break;
                // Start
                case 8:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_START_PAUSE)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE));
                        break;
                    }
                    ServiceContractsContract startContract = plugin.getContracts().getContract(command.getContract());
                    if (startContract == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!startContract.getEmployer().contentEquals(player.getName()) && !plugin.getPermissions().has(player, PERMISSIONS_START_PAUSE_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE_ANY));
                        break;  
                    }
                    String startContractorName = command.getPlayer();
                    if (startContractorName == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    if (!startContract.startContractor(startContractorName)) {
                        player.sendMessage(String.format(plugin.getString("EMPLOYER_START_ERROR"), startContractorName));
                        break;
                    }
                    player.sendMessage(String.format(plugin.getString("EMPLOYER_START_PAY"),startContractorName));
                    plugin.getServer().getPlayer(startContractorName).sendMessage(plugin.getString("CONTRACTOR_START_PAY"));
                    break;
                // Pause
                case 9:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_START_PAUSE)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE));
                        break;
                    }
                    ServiceContractsContract pauseContract = plugin.getContracts().getContract(command.getContract());
                    if (pauseContract == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!pauseContract.getEmployer().contentEquals(player.getName()) && !plugin.getPermissions().has(player, PERMISSIONS_START_PAUSE_ANY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE_ANY));
                        break;  
                    }
                    String pauseContractorName = command.getPlayer();
                    if (pauseContractorName == null) {
                        player.sendMessage(plugin.getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    if (!pauseContract.pauseContractor(pauseContractorName)) {
                        player.sendMessage(String.format(plugin.getString("EMPLOYER_PAUSE_ERROR"), pauseContractorName));
                        break;
                    }
                    player.sendMessage(String.format(plugin.getString("EMPLOYER_PAUSE_PAY"),pauseContractorName));
                    plugin.getServer().getPlayer(pauseContractorName).sendMessage(plugin.getString("CONTRACTOR_PAUSE_PAY"));

                    break;
                // Quit
                case 10:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_QUIT)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_QUIT));
                        break;
                    }
                    String quitContractorId = player.getName();
                    String quitContractId = plugin.getContractByContractor(quitContractorId);
                    ServiceContractsContract quitContract = plugin.getContracts().getContract(quitContractId);
                    if (quitContract == null || !quitContract.removeContractor(quitContractorId)){
                        player.sendMessage(plugin.getString("QUIT_FAILED"));
                        break;
                    }
                    player.sendMessage(plugin.getString("CONTRACTOR_QUIT"));
                    plugin.getServer().getPlayer(quitContract.getEmployer()).sendMessage(String.format(plugin.getString("EMPLOYER_QUIT"),quitContractorId));
                    break;
                // Modify
                case 11:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_MODIFY)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_MODIFY));
                        break;
                    }
                    
                    
                    // @todo implement modify
                    player.sendMessage("This command is not yet implemented. Please remove and create a new contract to make any modifications.");
                    break;
                // Info
                case 12:
                    if (!plugin.getPermissions().has(player, PERMISSIONS_INFO)) {
                        player.sendMessage(String.format(plugin.getString("NO_PERMISSIONS"), PERMISSIONS_INFO));
                        break;
                    }
                    player.sendMessage(plugin.getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),2);
                    break;
            }
        }
        catch(Exception e) {
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
                event.getPlayer().sendMessage(errorString);
            }
            else{
                plugin.log("An error has occured. Player:" + event.getPlayer().getName() + " Command:" + event.getMessage());
                plugin.log(e.toString());
                e.printStackTrace();
                if (plugin.inDebugMode()){
                    Writer result = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(result);
                    e.printStackTrace(printWriter);
                    String[] stackTrace = result.toString().split(System.getProperty("line.separator"));
                    for (int i = 0; i<stackTrace.length && i<4;i++){
                        plugin.getServer().broadcastMessage(stackTrace[i]);
                    }
                }
            }
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
                // @todo check if they have permissions for the block
                String newId = ServiceContractsContract.createId(sign.getX(),sign.getY(),sign.getZ());
                if (plugin.getContracts().contains(newId)) {
                    newContracts.remove(playerName);
                    playerStates.remove(playerName);
                    player.sendMessage(plugin.getString("CONTRACT_ALREADY_THERE"));
                    break;
                }
                ServiceContractsContract newContract = newContracts.get(playerName);
                newContract.setId(sign);
                newContract.drawSign();
                plugin.getContracts().addContract(newContract);
                newContracts.remove(playerName);
                playerStates.remove(playerName);
                player.sendMessage(plugin.getString("CONTRACT_CREATED"));
                break;
            // Info
            case 2:
                String infoId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract infoContract = plugin.getContracts().getContract(infoId);
                if(infoContract instanceof ServiceContractsContract) {
                    infoContract.sendInfoMessage(player);
                }
                else {
                    // @todo l10n
                    player.sendMessage("That is not a service contract sign");
                }
                playerStates.remove(playerName);
                break;
            // Close
            case 3:
                String closeId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract closeContract = plugin.getContracts().getContract(closeId);
                if(closeContract instanceof ServiceContractsContract && (closeContract.getEmployer().contentEquals(playerName) || plugin.getPermissions().has(player, PERMISSIONS_CLOSE_ANY))) {
                    closeContract.disable();
                }
                else {
                    // @todo l10n
                    player.sendMessage("Close failed.");
                }
                playerStates.remove(playerName);
                break;
            // Open
            case 4:
                String openId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract openContract = plugin.getContracts().getContract(openId);
                if(openContract instanceof ServiceContractsContract && (openContract.getEmployer().contentEquals(playerName)||plugin.getPermissions().has(player, PERMISSIONS_OPEN_ANY))) {
                    openContract.enable();
                }
                else {
                    // @todo l10n
                    player.sendMessage("Open failed.");
                }
                playerStates.remove(playerName);
                break;
            // Remove
            case 5:
                String removeId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract removeContract = plugin.getContracts().getContract(removeId);
                if(removeContract instanceof ServiceContractsContract && (removeContract.getEmployer().contentEquals(playerName)||plugin.getPermissions().has(player, PERMISSIONS_REMOVE_ANY))) {
                    plugin.getContracts().removeContract(removeId);
                }
                else {
                    // @todo l10n
                    player.sendMessage("Remove failed.");
                }
                playerStates.remove(playerName);
                break;
            // No state (applying)
            default:
                String id = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract contract = plugin.getContracts().getContract(id);
                if(contract instanceof ServiceContractsContract) {
                    if (!plugin.inDebugMode() && contract.getEmployer() == player.getName()) {
                        contract.sendInfoMessage(player);
                    }
                    else if(contract.getOpenings() > 0) {
                        // @todo get the '/sc -a' string from the ServiceContractsCommand class
                        contract.sendInfoMessage(player);
                        player.sendMessage(String.format(plugin.getString("APPLY"),"/sc -a"));
                        playerStates.remove(playerName);
                        playerStatesData.put(playerName, id);
                    }
                    else {
                        player.sendMessage(plugin.getString("NO_OPENINGS"));
                    }
                }
        }

    }
}
