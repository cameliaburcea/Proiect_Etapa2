package output;

import java.util.ArrayList;
import java.util.List;

public final class ProducerOutput {
    private int id;
    private int maxDistributors;
    private double priceKW;
    private String energyType;
    private int energyPerDistributor;
    private List<MonthlyStat> monthlyStats = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxDistributors() {
        return maxDistributors;
    }

    public void setMaxDistributors(int maxDistributors) {
        this.maxDistributors = maxDistributors;
    }

    public double getPriceKW() {
        return priceKW;
    }

    public void setPriceKW(double priceKW) {
        this.priceKW = priceKW;
    }

    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    public void setEnergyPerDistributor(int newEnergyPerDistributor) {
        this.energyPerDistributor = newEnergyPerDistributor;
    }

    public List<MonthlyStat> getMonthlyStats() {
        return monthlyStats;
    }

    public void setMonthlyStats(List<MonthlyStat> newMonthlyStats) {
        monthlyStats = newMonthlyStats;
    }

    /**
     * update the producer cost for energy per distributor
     * @param newEnergyPerDistributor
     */
    public void updateOutputProducerCosts(int newEnergyPerDistributor) {
        setEnergyPerDistributor(newEnergyPerDistributor);
    }
}
