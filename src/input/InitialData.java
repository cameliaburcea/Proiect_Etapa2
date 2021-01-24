package input;

import players.Consumer;
import players.Distributor;
import players.Producer;

import java.util.List;

public final class InitialData {
    private List<Consumer> consumers;
    private List<Distributor> distributors;
    private List<Producer> producers;

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public List<Distributor> getDistributors() {
        return distributors;
    }

    public void setConsumers(final List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public void setDistributors(final List<Distributor> distributors) {
        this.distributors = distributors;
    }
}
