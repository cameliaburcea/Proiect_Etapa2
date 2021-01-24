package players;

import files.Contract;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Distributor extends GamePlayers implements Observer {
    private int contractLength;
    private int initialInfrastructureCost;
    private int initialProductionCost;
    private int energyNeededKW;
    private String producerStrategy;
    private List<Producer> producers = new ArrayList<>();
    private boolean notified;

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    public void setEnergyNeededKW(int energyNeededKW) {
        this.energyNeededKW = energyNeededKW;
    }

    public String getProducerStrategy() {
        return producerStrategy;
    }

    public void setProducerStrategy(String producerStrategy) {
        this.producerStrategy = producerStrategy;
    }

    private double price;
    private List<Contract> contracts = new ArrayList<>();
    private List<Consumer> consumers = new ArrayList<>();

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public int getContractLength() {
        return contractLength;
    }

    public void setContractLength(final int contractLength) {
        this.contractLength = contractLength;
    }

    public int getInitialInfrastructureCost() {
        return initialInfrastructureCost;
    }

    public void setInitialInfrastructureCost(final int initialInfrastructureCost) {
        this.initialInfrastructureCost = initialInfrastructureCost;
    }

    public int getInitialProductionCost() {
        return initialProductionCost;
    }

    public void setInitialProductionCost(final int initialProductionCost) {
        this.initialProductionCost = initialProductionCost;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(final List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(final List<Contract> contracts) {
        this.contracts = contracts;
    }

    /**
     * implementation of abstract method from GamePlayers
     * @return the current budget
     */
    public double computeCurrentBudget() {
        return getInitialBudget() - computeMonthlyCosts();
    }

    /**
     * method for paying monthly costs
     */
    public void payMonthlyCosts() {
        if (getInitialBudget() < computeMonthlyCosts()) {
            /* the distributor becomes bankrupt */
            setBankrupt(true);
            setInitialBudget(getInitialBudget() - computeMonthlyCosts());

            /* every consumer must find another distributor */
            for (Consumer consumer : consumers) {
                consumer.setCurrentDistributor(null);
            }
        } else {
            setInitialBudget(getInitialBudget() - computeMonthlyCosts());
        }
    }

    /**
     * updates the distributor infrastructure cost from the monthly updates
     * @param newInfrastructureCost
     */
    public void updateCosts(final int newInfrastructureCost) {
        initialInfrastructureCost = newInfrastructureCost;
    }

    /**
     * @return the profit using the formula
     */
    public long getProfit() {
        return Math.round(Math.floor(0.2 * initialProductionCost));
    }

    /**
     * @return the contract price using the formula
     */
    public double computeContractPrice() {
        if (consumers.size() == 0) {
            /* contract price if there are no clients */
            return getInitialInfrastructureCost()
                    + productionCost() + getProfit();
        }

        /* contract price if there are clients */
        return Math.floor(getInitialInfrastructureCost() / consumers.size())
                + productionCost() + getProfit();
    }

    /**
     * @return the monthly cost to be paid
     */
    public int computeMonthlyCosts() {
        return getInitialInfrastructureCost()
                + (initialProductionCost * consumers.size());
    }

    /**
     * @param distributors the distributors that will be filtered
     * @param bankruptDistributors add bankrupt distributors in this list
     */
    public static void filterBankruptDistributors(final List<Distributor> distributors,
                                                  final List<Distributor> bankruptDistributors) {
        List<Distributor> bankrupts;
        bankrupts = distributors.stream()
                .filter(GamePlayers::isBankrupt).collect(Collectors.toList());

        for (Distributor d : bankrupts) {
            for (Producer p : d.getProducers()) {
                p.getDistributors().remove(d);
            }
        }
        bankruptDistributors.addAll(bankrupts);
        distributors.removeIf(GamePlayers::isBankrupt);
    }

    /**
     * remove consumers which finished paying their contract / are bankrupt
     */
    public void removeOutdatedConsumers() {
        /* remove the consumers who finished paying their contract */
        consumers.removeIf(e -> e.getRemainingMonths() == 0);
        /* remove the contracts which don't have clients anymore */
        contracts.removeIf(e -> e.getConsumers().size() == 0);

        /* remove the bankrupt and outdated
         * from the contracts' consumers list
         */
        for (Contract contract : contracts) {
            contract.getConsumers().removeIf(e -> e.getRemainingMonths() == 0);
            contract.getConsumers().removeIf(GamePlayers::isBankrupt);
        }
    }

    /**
     * method which chooses the producers based on the strategy of the distributor
     * @param producersList list of producers
     * @param strategy the strategy of the distributor
     */
    public void chooseStrategy(List<Producer> producersList, String strategy) {
        List<Producer> p = producersList;
        int energyTotal = 0;
        int i = 0;

        Comparator<Producer> compareByPrice = Comparator.comparing(Producer::getPriceKW);
        Comparator<Producer> compareByQuantity =
                Comparator.comparing(Producer::getEnergyPerDistributor);
        Comparator<Producer> compareById = Comparator.comparing(Producer::getId);
        Comparator<Producer> greenStrategyComparator = compareByPrice
                                                .thenComparing(compareByQuantity.reversed())
                                                .thenComparing(compareById);

        if (strategy.equals("GREEN")) {
            p = p.stream()
                .sorted((p1, p2) -> {
                    if (p1.getEnergyType().isRenewable() == p2.getEnergyType().isRenewable()) {
                        if (p1.getPriceKW() == p2.getPriceKW()) {
                            return Math.negateExact(Integer.compare(p1.getEnergyPerDistributor(),
                                p2.getEnergyPerDistributor()));
                    }
                    return Double.compare(p1.getPriceKW(), p2.getPriceKW());
                } else if (p1.getEnergyType().isRenewable()) {
                    return -1;
                }
                return 1;
            }).collect(Collectors.toList());
        } else {
            if (strategy.equals("PRICE")) {
                p = p.stream()
                        .sorted(greenStrategyComparator)
                        .collect(Collectors.toList());
            } else {
                p = p.stream().
                        sorted((compareByQuantity.reversed()).thenComparing(compareById))
                        .collect(Collectors.toList());
            }
        }

        /* add enough producers to ensure the quantity of energy needed */
        while (energyTotal < energyNeededKW) {
            /* if the max number of distributors for a certain producer is reached,
             * skip it and find the next producer
             * */
            if (p.get(i).getDistributors().size() < p.get(i).getMaxDistributors()) {
                producers.add(p.get(i));
                p.get(i).getDistributors().add(this);
                energyTotal += p.get(i).getEnergyPerDistributor();
            }
            i++;
        }
    }

    /**
     * compute the production cost for the second phase of the project
     * @return
     */
    public int productionCost() {
        double cost = 0;

        for (Producer p : producers) {
            cost += p.getEnergyPerDistributor() * p.getPriceKW();
        }

        cost = Math.round(Math.floor(cost / 10));
        return (int) cost;
    }

    /**
     * update the production cost
     */
    public void computeProductionCost() {
        initialProductionCost = productionCost();
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }

    @Override
    public void update(List<Producer> producersList) {
        for (Producer producer : producers) {
            producer.getDistributors().remove(this);
        }

        this.producers.clear();
        chooseStrategy(producersList, producerStrategy);
        computeProductionCost();
    }
}
