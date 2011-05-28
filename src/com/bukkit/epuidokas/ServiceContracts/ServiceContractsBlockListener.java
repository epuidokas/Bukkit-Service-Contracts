package com.bukkit.epuidokas.ServiceContracts;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.event.block.*;

/**
 *
 * @author ep
 */
public class ServiceContractsBlockListener extends BlockListener {
    private final ServiceContractsPlugin plugin;

    public ServiceContractsBlockListener(final ServiceContractsPlugin plugin) {
        this.plugin = plugin;
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign))
            return;
        String blockId = ServiceContractsContract.createId(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
        ServiceContractsContract contract = plugin.getContracts().getContract(blockId);
        if (contract instanceof ServiceContractsContract) {
            String contractId = contract.getId();
            String employer = contract.getEmployer();
            plugin.getContracts().removeContract(blockId);
            plugin.sendPlayerMessage(employer, String.format(plugin.getString("REMOVE_CONTRACT"), contractId));
        }
    }
}
