package com.bukkit.epuidokas.ServiceContracts;

import java.io.*;
import java.util.*;

/**
 *
 * @author ep
 */
public class ServiceContractsContracts {

    private final ServiceContractsPlugin plugin;
    private final HashMap<String,ServiceContractsContract> contracts = new HashMap();           // contractId : contract
    private final HashMap<String,String> contractsByOwner = new HashMap();                      // ownerId : contactId
    private final HashMap<String,String> currentContract = new HashMap();                       // contractorId : contractId
    private final HashMap<String,ArrayList<String>> applicationsByContractor = new HashMap();   // contractorId : contactIds
    
    public ServiceContractsContracts(ServiceContractsPlugin instance) {
        plugin = instance;
    }

    public boolean addContract(ServiceContractsContract contract) {
        return true;
    }
}
