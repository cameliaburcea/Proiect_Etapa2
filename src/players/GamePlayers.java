package players;

public abstract class GamePlayers {
    private int id;
    private boolean isBankrupt;
    private int initialBudget;

    /**
     * @return
     */
    public int getInitialBudget() {
        return initialBudget;
    }

    /**
     * @param initialBudget
     */
    public void setInitialBudget(final int initialBudget) {
        this.initialBudget = initialBudget;
    }

    /**
     * getter for id
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * setter for id
     * @param id
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * getter for isBankrupt
     * @return
     */
    public boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * setter for isBankrupt
     * @return
     */
    public void setBankrupt(final boolean bankrupt) {
        isBankrupt = bankrupt;
    }

    /**
     * computes current budget for every entity
     * @return the current budget
     */
    abstract double computeCurrentBudget();
}
