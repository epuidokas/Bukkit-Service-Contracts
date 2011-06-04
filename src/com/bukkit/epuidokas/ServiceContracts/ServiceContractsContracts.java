package com.bukkit.epuidokas.ServiceContracts;

import java.util.*;
import java.io.*;

/**
 *
 * @author ep
 */
public class ServiceContractsContracts implements Serializable {

    private final HashMap<String,ServiceContractsContract> contracts = new HashMap();           // contractId : contract
    private final HashMap<String,ArrayList<String>> contractsByOwner = new HashMap();           // ownerId : contractId
    private final HashMap<String,String> currentContract = new HashMap();                       // contractorId : contractId
    private final HashMap<String,ArrayList<String>> applicationsByContractor = new HashMap();   // contractorId : contactIds
    
    public ServiceContractsContracts() {
    }

    public boolean addContract(ServiceContractsContract contract) {
        contracts.put(contract.getId(), contract);
        addContractsByOwnerMapping(contract.getId(), contract.getEmployer());
        return true;
    }

    public boolean removeContract(String contractId) {
        ServiceContractsContract contract = getContract(contractId);
        contract.removeAllContractors();
        contract.removeSign();
        contracts.remove(contractId);
        removeContractsByOwnerMapping(contract.getId(), contract.getEmployer());
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

    private boolean addContractsByOwnerMapping (String contract, String employer) {
        ArrayList<String> contracts = contractsByOwner.get(employer);
        if (contracts == null)
            contracts = new ArrayList();
        contracts.add(contract);
        contractsByOwner.put(employer, contracts);
        return true;
    }

    private boolean removeContractsByOwnerMapping (String contract, String employer) {
        ArrayList<String> contracts = contractsByOwner.get(employer);
        if (contracts != null)
            contracts.remove(contract);
        contractsByOwner.put(employer, contracts);
        return true;
    }

    public ArrayList<String> getContractsByEmployer(String employerName) {
        ArrayList<String> contracts = contractsByOwner.get(employerName);
        if (contracts == null)
            return new ArrayList<String>();
        return contracts;
    }

    public ArrayList<String> getAllContracts() {
        ArrayList<String> contracts = new ArrayList();
        Collection c = contractsByOwner.values();
        Iterator itr = c.iterator();
        while(itr.hasNext()){
            ArrayList<String> contractsArr = (ArrayList<String>) itr.next();
            Iterator<String> contractsItr = contractsArr.iterator();
            while ( contractsItr.hasNext() ){
                contracts.add(contractsItr.next());
            }
        }
        return contracts;
    }
}
