package output;

public class OutputFactory {
    /**
     * factory design pattern implementation
     * @param type
     * @return
     */
    public Entity createEntity(final String type) {
        if (type.equals("consumer")) {
            return new ConsumerOutput();
        } else {
            return new DistributorOutput();
        }
    }
}
