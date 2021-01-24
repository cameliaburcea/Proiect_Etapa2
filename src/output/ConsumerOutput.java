package output;

public final class ConsumerOutput implements Entity {
    private int id;
    private boolean isBankrupt;
    private int budget;

    public void setId(final int id) {
        this.id = id;
    }

    public void setBankrupt(final boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    public void setBudget(final int budget) {
        this.budget = budget;
    }

    public int getId() {
        return id;
    }

    public boolean getIsBankrupt() {
        return isBankrupt;
    }

    public int getBudget() {
        return budget;
    }

    @Override
    public String toString() {
        return "Id: " + id + " | "
                + "IsBankrupt: " + isBankrupt + " | "
                + "Budget: " + budget;
    }
}
