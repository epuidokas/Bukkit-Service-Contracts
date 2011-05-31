package com.bukkit.epuidokas.ServiceContracts;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;

/**
 *
 * @author ep
 */
public class ServiceContractsBlockListener extends BlockListener {

    public ServiceContractsBlockListener() {
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign))
            return;
        String blockId = ServiceContractsContract.createId(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
        ServiceContractsContract contract = ServiceContractsPlugin.getPlugin().getContracts().getContract(blockId);
        if (contract instanceof ServiceContractsContract) {
            String contractId = contract.getId();
            String employer = contract.getEmployer();
            ServiceContractsPlugin.getPlugin().getContracts().removeContract(blockId);
            ServiceContractsPlugin.getPlugin().sendPlayerMessage(employer, String.format(ServiceContractsPlugin.getPlugin().getString("REMOVE_CONTRACT"), contractId));
        }
    }
}
