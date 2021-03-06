package com.bukkit.epuidokas.ServiceContracts;

import java.io.*;
import java.util.*;

import com.avaje.ebeaninternal.server.el.ElSetValue;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import com.iConomy.*;

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
    private final String PERMISSIONS_LIST = "servicecontracts.list";
    private final String PERMISSIONS_LIST_ALL = "servicecontracts.list.all";
    private final String PERMISSIONS_WORKERS = "servicecontracts.workers";
    private final String PERMISSIONS_JOB = "servicecontracts.job";

    private HashMap<String,Integer> playerStates = new HashMap<String,Integer>();
    private HashMap<String,String> playerStatesData = new HashMap<String,String>();
    private HashMap<String,ServiceContractsContract> newContracts = new HashMap<String,ServiceContractsContract>();


    public ServiceContractsPlayerListener() {

    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        
        if (event.isCancelled() || !event.getMessage().startsWith("/sc"))
            return;
        else
            event.setCancelled(true);

        try{
            ServiceContractsCommand command = new ServiceContractsCommand(event.getMessage());
            Player player = event.getPlayer();
            switch(command.getAction()) {
                // Help
                case 0:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_HELP)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_HELP));
                        break;
                    }
                    ArrayList<String> commands = command.getCommandFormats(true);
                    for(int i =0; i < commands.size(); i++) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, commands.get(i));
                    }
                    break;
                // New
                case 1:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_NEW)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_NEW));
                        break;
                    }
                    ServiceContractsContract contract = new ServiceContractsContract(player, command);
                    newContracts.put(player.getName(), contract);
                    playerStates.put(player.getName(), 1);
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_SIGN"));
                    break;
                // Close
                case 2:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_CLOSE) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_CLOSE_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_CLOSE));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),3);
                    break;
                // Open
                case 3:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_OPEN) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_OPEN_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_OPEN));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),4);
                    break;
                // Remove
                case 4:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_REMOVE) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_REMOVE_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_REMOVE));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),5);
                    break;
                // Apply
                case 5:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_APPLY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_APPLY));
                        break;
                    }
                    String playerStateData = playerStatesData.remove(player.getName());
                    if (playerStateData == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_CONTRACT_FIRST"));
                        break;
                    }
                    ServiceContractsContract applyContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(playerStateData);
                    Player employer = ServiceContractsPlugin.getPlugin().getServer().getPlayer(applyContract.getEmployer());
                    if (!ServiceContractsPlugin.getPlugin().inDebugMode() && employer.getName().contentEquals(player.getName())) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("EMPLOYER_APPLY"));
                        break;
                    }
                    if (ServiceContractsPlugin.getPlugin().getContractByContractor(player.getName()) != null) {
                        // @todo l10n
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "You're already employed!");
                        break;
                    }
                    // @todo l10n
                    // @todo actually do something with the applicant's message
                    if (applyContract.getOpenings() > 0) {
                        if (applyContract.hasApplicant(player.getName())) {
                            // already applied
                            ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "You've already applied for this contract!");
                        }
                        else {
                            applyContract.addApplicant(player.getName());
                            ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "Application submitted!");
                            ServiceContractsPlugin.getPlugin().sendPlayerMessage(employer, player.getName() + " has applied for your " + ServiceContractsPlugin.getPlugin().getString("TYPE_" + applyContract.getType()) + " contract.");
                            ServiceContractsPlugin.getPlugin().sendPlayerMessage(employer, "To accept, type '/sc -e " + applyContract.getIntId().toString() + " " + player.getName() + "'");
                        }
                    }
                    else {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("NO_OPENINGS"));
                    }
                    break;
                // Employ
                case 6:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_EMPLOY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_EMPLOY));
                        break;
                    }
                    ServiceContractsContract employContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(command.getContract());
                    if (employContract == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!employContract.getEmployer().contentEquals(player.getName()) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_EMPLOY_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_EMPLOY_ANY));
                        break;  
                    }
                    String contractorName = command.getPlayer();
                    if (contractorName == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    Player employEmployer = ServiceContractsPlugin.getPlugin().getServer().getPlayer(employContract.getEmployer());
                    if (!employContract.addContractor(contractorName)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_EMPLOY_ERROR"), contractorName));
                        break;
                    }

                    // Remove the contractor's applications for all other contracts
                    ArrayList<String> applicantContracts = ServiceContractsPlugin.getPlugin().getContractsByApplicant(contractorName);
                    ServiceContractsContracts allContracts = ServiceContractsPlugin.getPlugin().getContracts();
                    if (applicantContracts instanceof ArrayList) {
                        for(int i=0;i < applicantContracts.size();i++){
                            ServiceContractsContract applicantContract = allContracts.getContract(applicantContracts.get(i));
                            if (applicantContract instanceof ServiceContractsContract)
                                applicantContract.removeApplicant(contractorName);
                        }
                    }
                    // @todo l10n
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(employEmployer, "Type `/sc -s " + employContract.getIntId().toString() + " " + contractorName + "` to start paying them.");
                    break;
                // Fire
                case 7:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_FIRE)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_FIRE));
                        break;
                    }
                    ServiceContractsContract fireContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(command.getContract());
                    if (fireContract == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!fireContract.getEmployer().contentEquals(player.getName()) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_FIRE_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_FIRE_ANY));
                        break;  
                    }
                    String fireContractorName = command.getPlayer();
                    if (fireContractorName == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    if (!fireContract.removeContractor(fireContractorName)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_FIRED_ERROR"), fireContractorName));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_FIRED"), fireContractorName));
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(fireContractorName, String.format(ServiceContractsPlugin.getPlugin().getString("CONTRACTOR_FIRED"), fireContractorName));
                    break;
                // Start
                case 8:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_START_PAUSE)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE));
                        break;
                    }
                    ServiceContractsContract startContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(command.getContract());
                    if (startContract == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!startContract.getEmployer().contentEquals(player.getName()) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_START_PAUSE_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE_ANY));
                        break;  
                    }
                    String startContractorName = command.getPlayer();
                    if (startContractorName == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    if (!startContract.startContractor(startContractorName)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_START_ERROR"), startContractorName));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_START_PAY"), startContractorName));
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(startContractorName, ServiceContractsPlugin.getPlugin().getString("CONTRACTOR_START_PAY"));
                    break;
                // Pause
                case 9:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_START_PAUSE)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE));
                        break;
                    }
                    ServiceContractsContract pauseContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(command.getContract());
                    if (pauseContract == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACT"));
                        break;
                    }
                    if (!pauseContract.getEmployer().contentEquals(player.getName()) && !ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_START_PAUSE_ANY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_START_PAUSE_ANY));
                        break;  
                    }
                    String pauseContractorName = command.getPlayer();
                    if (pauseContractorName == null) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("INVALID_CONTRACTOR"));
                        break;
                    }
                    if (!pauseContract.pauseContractor(pauseContractorName)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_PAUSE_ERROR"), pauseContractorName));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_PAUSE_PAY"), pauseContractorName));
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(pauseContractorName, ServiceContractsPlugin.getPlugin().getString("CONTRACTOR_PAUSE_PAY"));

                    break;
                // Quit
                case 10:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_QUIT)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_QUIT));
                        break;
                    }
                    String quitContractorId = player.getName();
                    String quitContractId = ServiceContractsPlugin.getPlugin().getContractByContractor(quitContractorId);
                    ServiceContractsContract quitContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(quitContractId);
                    if (quitContract == null || !quitContract.removeContractor(quitContractorId)){
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("QUIT_FAILED"));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("CONTRACTOR_QUIT"));
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(quitContract.getEmployer(), String.format(ServiceContractsPlugin.getPlugin().getString("EMPLOYER_QUIT"), quitContractorId));
                    break;
                // Modify
                case 11:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_MODIFY)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_MODIFY));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_SIGN"));
                    playerStatesData.put(player.getName(), event.getMessage());
                    playerStates.put(player.getName(),6);
                    break;
                // Info
                case 12:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_INFO)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_INFO));
                        break;
                    }
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("SELECT_SIGN"));
                    playerStates.put(player.getName(),2);
                    break;
                // List
                case 13:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_LIST)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_LIST));
                        break;
                    }
                    ArrayList<String> contractsList;
                    if (command.isAll()){
                        if(!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_LIST)) {
                            ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_LIST_ALL));
                            break;
                        }
                        // @todo Build ArrayList of all contracts for all players
                        contractsList = ServiceContractsPlugin.getPlugin().getContracts().getAllContracts();
                    } else {
                        contractsList = ServiceContractsPlugin.getPlugin().getContracts().getContractsByEmployer(player.getName());
                    }

                    Iterator<String> iterator = contractsList.iterator();
                    while ( iterator.hasNext() ){
                        ServiceContractsContract listContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(iterator.next());
                        // @todo l10n
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, listContract.getIntId() + " - " + listContract.getReadableType());
                    }

                    break;
                // Workers
                case 14:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_WORKERS)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_WORKERS));
                        break;
                    }
                    ArrayList<String> contractsWorkers = ServiceContractsPlugin.getPlugin().getContracts().getContractsByEmployer(player.getName());
                    Iterator<String> iteratorWorkers = contractsWorkers.iterator();
                    while ( iteratorWorkers.hasNext() ){
                        ServiceContractsContract listContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(iteratorWorkers.next());
                        ArrayList<ServiceContractsContractor> contractors = listContract.getContractors();
                        Iterator<ServiceContractsContractor> iteratorContractors = contractors.iterator();
                        while ( iteratorContractors.hasNext() ){
                            // @todo l10n
                            ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, listContract.getIntId() + " - " + iteratorContractors.next().getName());
                        }
                    }
                    break;
                // Job
                case 15:
                    if (!ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_JOB)) {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("NO_PERMISSIONS"), PERMISSIONS_JOB));
                        break;
                    }
                    String job = ServiceContractsPlugin.getPlugin().getContractByContractor(player.getName());
                    if (job != null)
                        ServiceContractsPlugin.getPlugin().getContracts().getContract(job).sendInfoMessage(player);
                    else
                        // @todo l10n
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "You are currently unemployed.");
                    break;
            }
        }
        catch(Exception e) {
            ServiceContractsPlugin.getPlugin().handleException(e, event.getPlayer().getName());
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        if (!(event.getClickedBlock().getState() instanceof Sign))
            return;
        Sign sign = (Sign)event.getClickedBlock().getState();
        Player player = event.getPlayer();
        String playerName = player.getName();
        int playerState = 0;
        if (playerStates.containsKey(playerName))
            playerState = playerStates.get(playerName);
        switch(playerState) {
            // New
            case 1:
                // @todo check if they have permissions for the block
                String newId = ServiceContractsContract.createId(sign.getX(),sign.getY(),sign.getZ());
                if (ServiceContractsPlugin.getPlugin().getContracts().contains(newId)) {
                    newContracts.remove(playerName);
                    playerStates.remove(playerName);
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("CONTRACT_ALREADY_THERE"));
                    break;
                }
                ServiceContractsContract newContract = newContracts.get(playerName);
                newContract.setId(sign);
                newContract.drawSign();
                ServiceContractsPlugin.getPlugin().getContracts().addContract(newContract);
                newContracts.remove(playerName);
                playerStates.remove(playerName);
                ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("CONTRACT_CREATED"));
                break;
            // Info
            case 2:
                String infoId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract infoContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(infoId);
                if(infoContract instanceof ServiceContractsContract) {
                    infoContract.sendInfoMessage(player);
                }
                else {
                    // @todo l10n
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "That is not a service contract sign");
                }
                playerStates.remove(playerName);
                break;
            // Close
            case 3:
                String closeId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract closeContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(closeId);
                if(closeContract instanceof ServiceContractsContract && (closeContract.getEmployer().contentEquals(playerName) || ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_CLOSE_ANY))) {
                    closeContract.disable();
                }
                else {
                    // @todo l10n
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "Close failed.");
                }
                playerStates.remove(playerName);
                break;
            // Open
            case 4:
                String openId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract openContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(openId);
                if(openContract instanceof ServiceContractsContract && (openContract.getEmployer().contentEquals(playerName)||ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_OPEN_ANY))) {
                    openContract.enable();
                }
                else {
                    // @todo l10n
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "Open failed.");
                }
                playerStates.remove(playerName);
                break;
            // Remove
            case 5:
                String removeId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract removeContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(removeId);
                if(removeContract instanceof ServiceContractsContract && (removeContract.getEmployer().contentEquals(playerName)||ServiceContractsPlugin.getPlugin().getPermissions().has(player, PERMISSIONS_REMOVE_ANY))) {
                    ServiceContractsPlugin.getPlugin().getContracts().removeContract(removeId);
                }
                else {
                    // @todo l10n
                    ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, "Remove failed.");
                }
                playerStates.remove(playerName);
                break;
            // Modify
            case 6:
                String commandString = playerStatesData.get(playerName);
                playerStatesData.remove(playerName);
                playerStates.remove(playerName);
                try {
                    ServiceContractsCommand command = new ServiceContractsCommand(commandString);
                    String modifyId = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                    ServiceContractsContract modifyContract = ServiceContractsPlugin.getPlugin().getContracts().getContract(modifyId);
                    modifyContract.modify((Integer)command.getOpenings(), (Integer)command.getPayment());
                }
                catch(Exception e){
                    ServiceContractsPlugin.getPlugin().handleException(e, playerName);
                }
                break;
            // No state (applying)
            default:
                String id = ServiceContractsContract.createId(sign.getX(), sign.getY(), sign.getZ());
                ServiceContractsContract contract = ServiceContractsPlugin.getPlugin().getContracts().getContract(id);
                if(contract instanceof ServiceContractsContract) {
                    if (!ServiceContractsPlugin.getPlugin().inDebugMode() && contract.getEmployer() == player.getName()) {
                        contract.sendInfoMessage(player);
                    }
                    else if(contract.getOpenings() > 0) {
                        // @todo get the '/sc -a' string from the ServiceContractsCommand class
                        contract.sendInfoMessage(player);
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, String.format(ServiceContractsPlugin.getPlugin().getString("APPLY"),"/sc -a"));
                        playerStates.remove(playerName);
                        playerStatesData.put(playerName, id);
                    }
                    else {
                        ServiceContractsPlugin.getPlugin().sendPlayerMessage(player, ServiceContractsPlugin.getPlugin().getString("NO_OPENINGS"));
                    }
                }
        }

    }
}
