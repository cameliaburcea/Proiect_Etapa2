package output;

public final class ContractOutput {
    private int consumerId;
    private int price;
    private int remainedContractMonths;

    public void setConsumerId(final int consumerId) {
        this.consumerId = consumerId;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public void setRemainedContractMonths(final int remainedContractMonths) {
        this.remainedContractMonths = remainedContractMonths;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public int getPrice() {
        return price;
    }

    public int getRemainedContractMonths() {
        return remainedContractMonths;
    }

    @Override
    public String toString() {
        return "ConsumerId: " + consumerId + " | "
                + "Price: " + price +  " | "
                + "RemaninedContractMOnths: " + remainedContractMonths;

    }
}
