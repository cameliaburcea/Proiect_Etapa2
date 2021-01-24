package players;

import files.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Consumer extends GamePlayers {
    private int monthlyIncome;
    private Distributor currentDistributor;
    private Contract currentContract;
    private Contract debt;
    private int remainingMonths;

    public int getRemainingMonths() {
        return remainingMonths;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    public Distributor getCurrentDistributor() {
        return currentDistributor;
    }

    public void setMonthlyIncome(final int monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public void setCurrentDistributor(final Distributor currentDistributor) {
        this.currentDistributor = currentDistributor;
    }

    /**
     * implementation of abstract method from GamePlayers
     * @return the current budget
     */
    public double computeCurrentBudget() {
        return getInitialBudget() + monthlyIncome;
    }

    /**
     * update the current budget after getting paid
     */
    public void receiveMonthlyPayment() {
        setInitialBudget(getInitialBudget() + monthlyIncome);
    }

    /**
     * @return the debt price
     */
    public long computeDebt() {
        if (debt != null) {
            return Math.round(Math.floor(1.2 * debt.getPrice()
                    + currentContract.getPrice()));
        }

        return 0;
    }

    /**
     * pay monthly contracfinal List<Distributor> distributorst rate
     * @param distributors for choosing another distributor if needed
     */
    public void payContractRate(final List<Distributor> distributors) {
        if (debt != null) {
            manageDebt(distributors);
        } else {
            /* the consumer doesn't have debt */
            double contractPrice = currentContract.getPrice();
            remainingMonths--;

            if (getInitialBudget() < contractPrice) {
                /* the consumer can't pay this month */
                debt = currentContract;
            } else {
                /* pay the monthly cost */
                setInitialBudget((int) (getInitialBudget() - contractPrice));
                currentDistributor.setInitialBudget(
                        (int) (currentDistributor.getInitialBudget() + contractPrice));
            }
        }
    }

    /**
     *  auxiliary method to deal with the cases when the consumer has a debt
     * @param distributors
     */
    public void manageDebt(final List<Distributor> distributors) {
        /* the consumer didn't pay last month */
        if (getInitialBudget() < computeDebt()) {
            setBankrupt(true);
        } else {
            List<Distributor> otherDistributors = new ArrayList<>();
            int oldDistributorId = currentDistributor.getId();
            double oldContractPrice = currentContract.getPrice();
            otherDistributors.addAll(distributors);
            otherDistributors.remove(currentDistributor);

            /* set the new distributor */
            setNewDistributor(getBestDistributor(distributors));

            /* if it is the same as the old one , remove the consumer from its old contract */
            if (oldDistributorId != getBestDistributor(distributors).getId())  {
                for (Distributor d : distributors) {
                    if (d.getId() == oldDistributorId) {
                        d.getConsumers().remove(this);
                        for (Contract c : d.getContracts()) {
                            if (c.getPrice() == oldContractPrice) {
                                c.getConsumers().remove(this);
                            }
                        }
                    }
                }
            }

            /* update for phase 2 of the project */
            if (remainingMonths == 0) {
                if (oldDistributorId == getBestDistributor(distributors).getId()) {
                    /* the new contract is at the same distributor */
                    int cost = (int) (debt.getPrice() + currentContract.getPrice()
                            + computeDebt());
                    setInitialBudget(getInitialBudget() - cost);
                    remainingMonths--;
                } else {
                    int cost = (int) (debt.getPrice() + computeDebt());

                    if (getInitialBudget() < cost) {
                        /* the consumer is now bankrupt */
                        setBankrupt(true);
                    } else {
                        remainingMonths--;

                        if (getInitialBudget() < cost + currentContract.getPrice()) {
                            /* if the consumer can't pay everything they can pay
                             * the old contract and the penalty
                             */
                            setInitialBudget(getInitialBudget() - cost);
                            debt =  currentContract;
                        } else {
                            /* they can pay everything */
                            setInitialBudget((int) (getInitialBudget()
                                    - cost - currentContract.getPrice()));
                        }
                    }
                }
            } else {
                /* the consumer can pay and choose another distributor */
                setInitialBudget((int) (getInitialBudget() - computeDebt()));
                currentDistributor.setInitialBudget(
                        (int) (currentDistributor.getInitialBudget() - computeDebt()));
                remainingMonths -= 2;

                if (otherDistributors.size() != 0) {
                    /* check if there is another offer better than the actual one */
                    double bestMarketPrice = getBestDistributor(otherDistributors).getPrice();

                    /* there is a better contract than the existing one */
                    if (currentContract.getPrice() > bestMarketPrice) {
                        /* set the new distributor */
                        setNewDistributor(getBestDistributor(otherDistributors));
                        setInitialBudget((int) (getInitialBudget() - currentContract.getPrice()));
                        currentDistributor.setInitialBudget(
                                (int) (currentDistributor.getInitialBudget()
                                        + currentContract.getPrice()));
                        remainingMonths--;
                    }
                } else {
                    /* pay now the debt and the current month costs */
                    setInitialBudget((int) (getInitialBudget() - computeDebt()));
                    currentDistributor.setInitialBudget(
                            (int) (currentDistributor.getInitialBudget() - computeDebt()));
                    remainingMonths -= 2;
                }
            }
        }
    }

    /**
     * @param distributors list of distributors
     * @return the distributor with the best offer from the distributors list
     */
    public Distributor getBestDistributor(final List<Distributor> distributors) {
        Distributor bestDistributor = distributors.get(0);

        for (Distributor distributor : distributors) {
            if (distributor.getPrice() < bestDistributor.getPrice()) {
                bestDistributor = distributor;
            }
        }

        return bestDistributor;
    }

    /**
     * sets a new distributor
     * @param distributor the distributor which will be set
     */
    public void setNewDistributor(final Distributor distributor) {
        /* the consumer doesn't have a distributor; get one */
        if (remainingMonths == 0 && currentDistributor != null) {
            /* consumer paid all his contract costs; must find a new one */
            for (Contract contract : distributor.getContracts()) {
                if (contract.getPrice() == currentContract.getPrice()) {
                    /* remove the consumer from the old contract consumers' list */
                    contract.getConsumers().remove(this);
                }
            }
            distributor.getConsumers().remove(this);
        }

        currentDistributor = distributor;
        currentContract = Contract.createContract(distributor);

        distributor.getConsumers().add(this);
        remainingMonths = currentDistributor.getContractLength();

        for (Contract contract : distributor.getContracts()) {
            if (contract.getPrice() == currentContract.getPrice()) {
                contract.getConsumers().add(this);
            }
        }
    }

    /**
     * remove a consumer who finished paying their contract
     * @param distributors
     */
    public void removeOutdatedConsumer(final List<Distributor> distributors) {
        /* must remove the consumer from the distributor's lists */
        for (Distributor d : distributors) {
            if (d.getId() == currentDistributor.getId() && d.getConsumers().contains(this)) {
                d.getConsumers().remove(this);
                for (Contract contract : d.getContracts()) {
                    contract.getConsumers().remove(this);
                }
            }
        }
    }

    /**
     * remove a bankrupt consumers from the consumers list of the distributor
     * and the consumers list of the contract
     * @param distributors
     */
    public void removeBankruptConsumer(final List<Distributor> distributors) {
        if (isBankrupt()) {
            for (Distributor d : distributors) {
                d.getConsumers().remove(this);
                for (Contract contract : d.getContracts()) {
                    contract.getConsumers().remove(this);
                }
            }
        }
    }

    /**
     * @param consumers the list that will be filtered
     * @param bankruptConsumers bankrupt consumers will be added in this list
     */
    public static void filterBankruptConsumers(final List<Consumer> consumers,
                                               final List<Consumer> bankruptConsumers) {
        List<Consumer> bankrupts;
        bankrupts = consumers.stream()
                .filter(c -> c.isBankrupt()).collect(Collectors.toList());
        bankruptConsumers.addAll(bankrupts);
        consumers.removeIf(c -> c.isBankrupt());
    }
}
