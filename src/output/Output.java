package output;

import java.util.List;

public final class Output {
    private List<ConsumerOutput> consumers;
    private List<DistributorOutput> distributors;
    private List<ProducerOutput> energyProducers;

    public List<ProducerOutput> getEnergyProducers() {
        return energyProducers;
    }

    public void setEnergyProducers(List<ProducerOutput> energyProducers) {
        this.energyProducers = energyProducers;
    }

    public List<ConsumerOutput> getConsumers() {
        return consumers;
    }

    public void setConsumers(final List<ConsumerOutput> consumers) {
        this.consumers = consumers;
    }

    public List<DistributorOutput> getDistributors() {
        return distributors;
    }

    public void setDistributors(final List<DistributorOutput> distributors) {
        this.distributors = distributors;
    }
}
