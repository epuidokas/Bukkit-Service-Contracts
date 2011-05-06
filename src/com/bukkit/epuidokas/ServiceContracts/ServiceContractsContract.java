package com.bukkit.epuidokas.ServiceContracts;

import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;
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
    private ArrayList<String> contractors;

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
        money = payment*openings;
        
        Account account = plugin.getIConomy().getBank().getAccount(employer);

        if(!account.hasEnough(money)) {
            openings = (int)account.getBalance()/payment;
            if (openings > 0) {
                player.sendMessage(String.format(plugin.getString("MONEY_WARNING"), openings));
                money = payment*openings;
            }
            else {
                throw new Exception(String.format(plugin.getString("MONEY_ERROR")));
            }
        }
        
        account.subtract(money);

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

    public boolean pay(String contractorName) {
        return pay(plugin.getServer().getPlayer(contractorName));
    }

    public boolean pay(Player contractor) {
        String contractorName = contractor.getName();
        if (!contractors.contains(contractorName))
            return false;
        Account account = plugin.getIConomy().getBank().getAccount(contractorName);
        account.add(payPerPeriod);
        money = money - payPerPeriod;
        return true;
    }

    public String getId() {
        return id;
    }

    public static String createId(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    public boolean drawSign(final Sign sign) {
        // @todo proper l10n
        sign.setLine(0, plugin.getString("TYPE_" + this.type));
        sign.setLine(1, (this.landmark.isEmpty()) ? this.x + "," + this.z : "-near " + this.landmark + "-");
        sign.setLine(2, this.payment + "c/" + this.length + "min");
        sign.setLine(3, this.openings + " opening(s)");

        // Force update of sign text
        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
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
}
