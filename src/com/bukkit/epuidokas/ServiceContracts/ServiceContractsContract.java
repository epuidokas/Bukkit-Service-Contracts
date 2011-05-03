package com.bukkit.epuidokas.ServiceContracts;

import org.bukkit.entity.Player;
import com.nijiko.coelho.iConomy.system.Account;

/**
 *
 * @author ep
 */
public class ServiceContractsContract {
    
    private String id = null;
    private int action = 0;
    private int type = 0;
    private int openings = 0;
    private int length = 0;
    private int payment = 0;
    private int x = 0;
    private int z = 0;
    private String landmark = "";
    private int signX = 0;
    private int signY = 0;
    private int signZ = 0;

    public ServiceContractsContract(ServiceContractsPlugin plugin, Player player, ServiceContractsCommand command) throws Exception{
        type = command.getType();
        openings = command.getOpenings();
        length = command.getLength();
        payment = command.getPayment();
        x = command.getX();
        z = command.getZ();

        Account account = plugin.getIConomy().getBank().getAccount(player.getName());

        if(!account.hasEnough(payment*openings)) {
            openings = (int)account.getBalance()/payment;
            if (openings > 0) {
                player.sendMessage(String.format(plugin.getString("MONEY_WARNING"), openings));
            }
            else {
                throw new Exception(String.format(plugin.getString("MONEY_ERROR")));
            }
        }

        // @todo put their money somewhere safe
    }

    public boolean setId(int x, int y, int z) {

        return true;
    }
}
