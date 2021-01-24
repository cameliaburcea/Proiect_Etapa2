package output;

import java.util.List;

public final class DistributorOutput implements Entity {
    private int id;
    private int energyNeededKW;
    private int contractCost;
    private int budget;
    private String producerStrategy;
    private boolean isBankrupt;
    private List<ContractOutput> contracts;

    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    public void setEnergyNeededKW(int energyNeededKW) {
        this.energyNeededKW = energyNeededKW;
    }

    public int getContractCost() {
        return contractCost;
    }

    public void setContractCost(int contractCost) {
        this.contractCost = contractCost;
    }

    public String getProducerStrategy() {
        return producerStrategy;
    }

    public void setProducerStrategy(String producerStrategy) {
        this.producerStrategy = producerStrategy;
    }

    public void setBankrupt(boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    public int getId() {
        return id;
    }

    public int getBudget() {
        return budget;
    }

    public boolean getIsBankrupt() {
        return isBankrupt;
    }

    public List<ContractOutput> getContracts() {
        return contracts;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setBudget(final int newBudget) {
        budget = newBudget;
    }

    public void setIsBankrupt(final boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    public void setContracts(final List<ContractOutput> contracts) {
        this.contracts = contracts;
    }
}
