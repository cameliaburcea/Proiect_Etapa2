package files;

import players.Consumer;
import java.util.List;

public final class MonthlyUpdates {
    private List<Consumer> newConsumers;
    private List<DistributorChanges> distributorChanges;
    private List<ProducerChanges> producerChanges;

    public List<ProducerChanges> getProducerChanges() {
        return producerChanges;
    }

    public void setProducerChanges(List<ProducerChanges> producerChanges) {
        this.producerChanges = producerChanges;
    }

    public List<Consumer> getNewConsumers() {
        return newConsumers;
    }

    public void setNewConsumers(final List<Consumer> newConsumers) {
        this.newConsumers = newConsumers;
    }

    public List<DistributorChanges> getDistributorChanges() {
        return distributorChanges;
    }

    public void setDistributorChanges(List<DistributorChanges> distributorChanges) {
        this.distributorChanges = distributorChanges;
    }
}
