package com.bukkit.epuidokas.ServiceContracts;

import java.util.*;
import java.lang.*;

import com.iConomy.system.Account;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import com.iConomy.*;

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
    private int potentialCost = 0;
    private String employer = "";
    private HashMap<String,ServiceContractsContractor> contractors = new HashMap();
    private ArrayList<String> applicants = new ArrayList();
    private boolean enabled = true;

    public ServiceContractsContract(ServiceContractsPlugin instance, Player player, ServiceContractsCommand command) throws Exception{
        plugin = instance;
        type = command.getType();
        payPeriods = (int)(command.getLength()/PAY_INTERVAL);
        length = payPeriods*PAY_INTERVAL;
        setPayment(command.getPayment());
        x = command.getX();
        z = command.getZ();
        employer = player.getName();
        setOpenings(command.getOpenings());
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

        Account employerAccount = plugin.getIConomy().getAccount(employer);
        if (!employerAccount.getMainBankAccount().getHoldings().hasEnough(payPerPeriod)){
            return false;
        }

        employerAccount.getMainBankAccount().getHoldings().subtract(payPerPeriod);

        Account contractorAccount = plugin.getIConomy().getAccount(contractorName);
        contractorAccount.getMainBankAccount().getHoldings().add(payPerPeriod);
        plugin.sendPlayerMessage(contractor, String.format(plugin.getString("PAID"), payPerPeriod));
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
        sign.setLine(0, String.format(plugin.getString("SIGN_LINE1"), plugin.getString("TYPE_" + type + "_READABLE")));
        sign.setLine(1, (landmark.isEmpty()) ? String.format(plugin.getString("SIGN_LINE2"), x, z) : String.format(plugin.getString("SIGN_LINE2_LANDMARK"), landmark));
        sign.setLine(2, String.format(plugin.getString("SIGN_LINE3"), payment, length));
        if (enabled)
            sign.setLine(3, String.format(plugin.getString("SIGN_LINE4"), getOpenings()));
        else
            sign.setLine(3, plugin.getString("SIGN_LINE4_CLOSED"));

        updateSign();

        return true;
    }

    public boolean removeSign() {
        final Block block = plugin.getServer().getWorld("world").getBlockAt(signX, signY, signZ);
        final Sign sign = (Sign)block.getState();
        sign.setLine(0, "");
        sign.setLine(1, "");
        sign.setLine(2, "");
        sign.setLine(3, "");
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

    public boolean addContractor(String contractorName) throws Exception{
        if (getOpenings() < 1) {
            plugin.sendPlayerMessage(employer, plugin.getString("NOT_ENOUGH_OPENINGS"));
            return false;
        }

        if (!applicants.contains(contractorName)) {
            plugin.sendPlayerMessage(employer, String.format(plugin.getString("NOT_AN_APPLICANT"), contractorName));
            return false;
        }

        Account account = plugin.getIConomy().getAccount(employer);
        if (!account.getMainBankAccount().getHoldings().hasEnough(payment)){
            plugin.sendPlayerMessage(employer, plugin.getString("INSUFFICIENT_FUNDS"));
            setOpenings(0);
            return false;
        }
        contractors.put(contractorName, new ServiceContractsContractor(plugin,contractorName,id));
        plugin.setContractByContractor(contractorName, id);
        removeApplicant(contractorName);
        setOpenings(getOpenings()-1);
        drawSign();
        return true;
    }

    public boolean submitTimecard(String contractorName, Integer time){
        if (time >= length) {
            plugin.sendPlayerMessage(contractorName, "Your contract is complete!");
            plugin.getContracts().removeContract(id);
            return pay(contractorName);
        }
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
            plugin.sendPlayerMessage(contractorId, String.format(plugin.getString("REMOVE_CONTRACT"), id));
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

    public boolean setPayment(Integer amount) {
        if (amount == null)
            return false;
        payPerPeriod = (int)(amount/payPeriods);
        payment = payPerPeriod*payPeriods;
        return true;
    }

    public boolean setOpenings(int num) throws Exception {
        Account account = plugin.getIConomy().getAccount(employer);
        if(!account.getMainBankAccount().getHoldings().hasEnough(payment*num)) {
            num = (int)(account.getMainBankAccount().getHoldings().balance()/payment);
            if (num > 0) {
                plugin.sendPlayerMessage(employer, String.format(plugin.getString("MONEY_WARNING"), num));
            }
            else {
                throw new Exception(plugin.getString("MONEY_ERROR"));
            }
        }
        openings = num;
        return true;
    }

    public int getOpenings() {
        if (enabled)
            return openings;
        else
            return 0;
    }

    public boolean sendInfoMessage(Player player){
        // @todo l10n
        plugin.sendPlayerMessage(player, plugin.getString("TYPE_" + type + "_READABLE") + " contract offerd by " + employer);
        plugin.sendPlayerMessage(player,  payment + "c for " + length + "min of work");
        plugin.sendPlayerMessage(player, "Contract is located at " + x + ", " + z);
        plugin.sendPlayerMessage(player, getOpenings() + " opening(s) left");
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

    public boolean enable() {
        enabled = true;
        return drawSign();
    }

    public boolean disable() {
        enabled = false;
        return drawSign();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean modify(Integer o, Integer p) throws Exception {
        if (p != null && p < payment)
            throw new Exception(plugin.getString("MODIFY_BAD_PAYMENT"));
        setOpenings(o);
        setPayment(p);
        return drawSign();
    }
}
