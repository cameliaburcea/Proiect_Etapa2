package players;

import java.util.List;

public interface Observer {
    /**
     * update the producers list and their distributors
     * @param producersList
     */
    void update(List<Producer> producersList);
}
