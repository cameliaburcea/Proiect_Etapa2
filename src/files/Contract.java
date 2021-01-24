package files;

import players.Consumer;
import players.Distributor;

import java.util.ArrayList;
import java.util.List;

public final class Contract {
    private List<Consumer> consumers = new ArrayList<>();
    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(final List<Consumer> consumers) {
        this.consumers = consumers;
    }

    /**
     * singleton pattern implementation
     * create a contract if there is not one created yet
     * if there is an existing one, return it
     * @param distributor
     * @return
     */
    public static Contract createContract(final Distributor distributor) {
        double price = distributor.getPrice();
        for (Contract contract : distributor.getContracts()) {
            if (contract.getPrice() == price) {
                /* the contract is already created; return the contract */
                return contract;
            }
        }

        /* if the contract doesn't exist, create one */
        Contract newContract = new Contract();
        newContract.setPrice(distributor.getPrice());
        distributor.getContracts().add(newContract);

        return newContract;
    }
}
