package com.bukkit.epuidokas.ServiceContracts;

import java.util.*;

/**
 *
 * @author ep
 */
public class ServiceContractsContracts {

    private final HashMap<String,ServiceContractsContract> contracts = new HashMap();           // contractId : contract
    private final HashMap<String,String> contractsByOwner = new HashMap();                      // ownerId : contactId
    private final HashMap<String,String> currentContract = new HashMap();                       // contractorId : contractId
    private final HashMap<String,ArrayList<String>> applicationsByContractor = new HashMap();   // contractorId : contactIds
    
    public ServiceContractsContracts() {
    }

    public boolean addContract(ServiceContractsContract contract) {
        contracts.put(contract.getId(), contract);
        return true;
    }

    public boolean removeContract(String contractId) {
        ServiceContractsContract contract = getContract(contractId);
        contract.removeAllContractors();
        contract.removeSign();
        contracts.remove(contractId);
        return true;
    }

    public ServiceContractsContract getContract(String id) {
        return contracts.get(id);
    }

    public ServiceContractsContract getContract(Integer id) {
        return getContract(ServiceContractsPlugin.getPlugin().contractIdIntToStr(id));
    }

    public boolean contains(String id) {
        return contracts.containsKey(id);
    }
}
