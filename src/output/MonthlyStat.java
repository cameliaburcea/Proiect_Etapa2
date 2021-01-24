package output;

public final class MonthlyStat {
    private int month;
    private int[] distributorsIds;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int[] getDistributorsIds() {
        return distributorsIds;
    }

    public void setDistributorsIds(int[] distributorsIds) {
        this.distributorsIds = distributorsIds;
    }
}
