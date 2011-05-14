package com.bukkit.epuidokas.ServiceContracts;

import java.util.*;
import java.lang.*;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import com.nijiko.coelho.iConomy.system.Account;

/**
 *
 * @author ep
 */
public class ServiceContractsContract {

    private final int PAY_INTERVAL = 5;

    private final ServiceContractsPlugin plugin;
    private String id = null;
    private int action = 0;
    private int type = 0;
    private int openings = 0;
    private int payPeriods = 0;
    private int length = 0;
    private int payPerPeriod = 0;
    private int payment = 0;
    private int x = 0;
    private int z = 0;
    private String landmark = "";
    private int signX = 0;
    private int signY = 0;
    private int signZ = 0;
    private int money = 0;
    private int potentialCost = 0;
    private String employer = "";
    private HashMap<String,ServiceContractsContractor> contractors = new HashMap();
    private ArrayList<String> applicants = new ArrayList();

    public ServiceContractsContract(ServiceContractsPlugin instance, Player player, ServiceContractsCommand command) throws Exception{
        plugin = instance;
        type = command.getType();
        openings = command.getOpenings();
        payPeriods = (int)command.getLength()/PAY_INTERVAL;
        length = payPeriods*PAY_INTERVAL;
        payPerPeriod = (int)command.getPayment()/payPeriods;
        payment = payPerPeriod*payPeriods;
        x = command.getX();
        z = command.getZ();
        employer = player.getName();
        
        Account account = plugin.getIConomy().getBank().getAccount(employer);

        if(!account.hasEnough(money)) {
            openings = (int)account.getBalance()/payment;
            if (openings > 0) {
                player.sendMessage(String.format(plugin.getString("MONEY_WARNING"), openings));
            }
            else {
                throw new Exception(plugin.getString("MONEY_ERROR"));
            }
        }

    }

    public boolean setId(Sign sign) {
        return setId(sign.getX(),sign.getY(),sign.getZ());
    }

    public boolean setId(int x, int y, int z) {
        signX = x;
        signY = y;
        signZ = z;
        id = createId(x,y,z);
        return true;
    }

    private boolean pay(String contractorName) {
        return pay(plugin.getServer().getPlayer(contractorName));
    }

    private boolean pay(Player contractor) {
        String contractorName = contractor.getName();
        if (!contractors.containsKey(contractorName))
            return false;

        Account employerAccount = plugin.getIConomy().getBank().getAccount(employer);
        if(!employerAccount.hasEnough(payPerPeriod)) {
            return false;
        }

        employerAccount.subtract(payPerPeriod);

        Account contractorAccount = plugin.getIConomy().getBank().getAccount(contractorName);
        contractorAccount.add(payPerPeriod);
        contractor.sendMessage(String.format(plugin.getString("PAID"), payPerPeriod));
        return true;
    }

    public String getId() {
        return id;
    }

    public static String createId(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    public boolean drawSign() {
        final Block block = plugin.getServer().getWorld("world").getBlockAt(signX, signY, signZ);
        final Sign sign = (Sign)block.getState();
        // @todo l10n
        sign.setLine(0, plugin.getString("TYPE_" + type));
        sign.setLine(1, (landmark.isEmpty()) ? x + "," + z : "-near " + landmark + "-");
        sign.setLine(2, payment + "c/" + length + "min");
        sign.setLine(3, openings + " opening(s)");
        
        updateSign();
        
        return true;
    }

    private boolean updateSign(){
        // @todo support multiple worlds
        final Block block = plugin.getServer().getWorld("world").getBlockAt(signX, signY, signZ);
        final Sign sign = (Sign)block.getState();
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                sign.update();
            }
        });
        return true;
    }

    public String getEmployer() {
        return employer;
    }

    public int getType() {
        return type;
    }

    public boolean addContractor(String contractorName){
        if (openings < 1) {
            plugin.getServer().getPlayer(employer).sendMessage(plugin.getString("NOT_ENOUGH_OPENINGS"));
            return false;
        }

        if (!applicants.contains(contractorName)) {
            plugin.getServer().getPlayer(employer).sendMessage(String.format(plugin.getString("NOT_AN_APPLICANT"), contractorName));
            return false;
        }
        
        Account account = plugin.getIConomy().getBank().getAccount(employer);
        if (!account.hasEnough(payment)){
            plugin.getServer().getPlayer(employer).sendMessage(plugin.getString("INSUFFICIENT_FUNDS"));
            setOpenings(0);
            return false;
        }
        contractors.put(contractorName, new ServiceContractsContractor(plugin,contractorName,id));
        plugin.setContractByContractor(contractorName, id);
        removeApplicant(contractorName);
        setOpenings(getOpenings()-1);
        return true;
    }

    public boolean submitTimecard(String contractorName, Integer time){
        if (time % PAY_INTERVAL == 0)
            return pay(contractorName);
        return true;
    }

    public boolean removeContractor(String contractorName){
        if (!contractors.containsKey(contractorName))
            return false;
        contractors.get(contractorName).pause();
        contractors.remove(contractorName);
        plugin.removeContractByContractor(contractorName);
        return true;
    }

    public boolean removeAllContractors(){
        ServiceContractsContractor contractor;
        for(String contractorId : contractors.keySet()) {
            contractor = contractors.remove(contractorId);
            contractor.pause();
            plugin.removeContractByContractor(contractorId);
            plugin.getServer().getPlayer(contractorId).sendMessage(String.format(plugin.getString("REMOVE_CONTRACT"), id));
        }

        return true;
    }

    public boolean pauseContractor(String contractorName){
        if(!contractors.containsKey(contractorName))
            return false;
        contractors.get(contractorName).pause();
        return true;
    }

    public boolean startContractor(String contractorName){
        if(!contractors.containsKey(contractorName))
            return false;
        contractors.get(contractorName).start();
        return true;
    }

    public boolean setOpenings(int num) {
        openings = num;
        return drawSign();
    }

    public int getOpenings() {
        return openings;
    }

    public boolean sendInfoMessage(Player player){
        // @todo l10n
        player.sendMessage(type + " contract offerd by " + employer);
        player.sendMessage( payment + "c for " + length + "min of work");
        player.sendMessage("Contract is located at " + x + "," + z);
        player.sendMessage(openings + " opening(s) left");
        return true;
    }

    public boolean addApplicant(String applicant) {
        return (applicants.add(applicant) && plugin.addContractByApplicant(applicant, id));
    }

    public boolean removeApplicant(String applicant) {
        return (applicants.remove(applicant) && plugin.removeApplicant(applicant));
    }

    public boolean hasApplicant(String applicant) {
        return applicants.contains(applicant);
    }
}
