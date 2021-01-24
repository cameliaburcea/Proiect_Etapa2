import files.Contract;
import files.DistributorChanges;
import files.MonthlyUpdates;
import files.ProducerChanges;
import input.Input;
import output.ConsumerOutput;
import output.ContractOutput;
import output.DistributorOutput;
import output.MonthlyStat;
import output.Output;
import output.OutputFactory;
import output.ProducerOutput;
import players.Consumer;
import players.Distributor;
import players.Producer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class GameSimulation {
    /**
     * the method where all the game logic is implemented
     * @param input
     * @return the output to be written in the output files
     */
    public Output Simulation(final Input input) {
        Output outputData = new Output();
        OutputFactory factory = new OutputFactory();

        int monthNumber = 1;

        List<Consumer> consumers = input.getInitialData().getConsumers();
        List<Distributor> distributors = input.getInitialData().getDistributors();
        List<Producer> producers = input.getInitialData().getProducers();
        List<MonthlyUpdates> monthlyUpdates = input.getMonthlyUpdates();
        List<Consumer> bankruptConsumers = new ArrayList<>();
        List<Distributor> bankruptDistributors = new ArrayList<>();
        List<ConsumerOutput> outputConsumers = new ArrayList<>();
        List<DistributorOutput> outputDistributors = new ArrayList<>();
        List<ProducerOutput> outputProducers = new ArrayList<>();

        /* initial round: there are no cost changes nor new consumers */

        producers.sort(Comparator.comparing(Producer::getId));

        for (Producer p : producers) {
            ProducerOutput producerOutput = new ProducerOutput();
            producerOutput.setId(p.getId());
            producerOutput.setEnergyPerDistributor(p.getEnergyPerDistributor());
            producerOutput.setEnergyType(p.getEnergyType().getLabel());
            producerOutput.setPriceKW(p.getPriceKW());
            producerOutput.setMaxDistributors(p.getMaxDistributors());
            outputProducers.add(producerOutput);
        }

        /* first step: add contracts to the distributor's contracts list */
        for (Distributor d : distributors) {
            d.chooseStrategy(producers, d.getProducerStrategy());
            d.computeProductionCost();
            /* set the initial price of the contract for every distributor */
            d.setPrice(d.computeContractPrice());
        }

        /* step 2: consumers receive their monthly income */
        for (Consumer c : consumers) {
            c.receiveMonthlyPayment();
            /* step 3: consumers choose their contract */
            c.setNewDistributor(c.getBestDistributor(distributors));
            /* step 4: consumers pay their contract */
            c.payContractRate(distributors);
        }

        for (Distributor d : distributors) {
            /* step 5: pay monthly costs */
            d.payMonthlyCosts();
        }

        /* actions that will take place for every turn of the game */
        for (int i = 0; i < input.getNumberOfTurns(); i++) {
            MonthlyUpdates updates = monthlyUpdates.get(i);
            /* add the new consumers for every turn */
            consumers.addAll(monthlyUpdates.get(i).getNewConsumers());
            distributors.sort(Comparator.comparing(Distributor::getId));

            for (Distributor d : distributors) {
                d.setNotified(false);

                for (DistributorChanges distributorChanges : updates.getDistributorChanges()) {
                    /* update the costs */
                    if (d.getId() == distributorChanges.getId()) {
                        d.updateCosts(distributorChanges.getInfrastructureCost());
                    }
                }

                /* compute the current offer of every distributor */
                d.setPrice(d.computeContractPrice());
                d.removeOutdatedConsumers();
            }


            /* then consumers pay their monthly payment */
            for (Consumer consumer : consumers) {
                /* every consumer must receive his monthly payment */
                consumer.receiveMonthlyPayment();

                /* if there are consumers who doesn't have a contract
                 * or their contract expires they must choose another one
                 */
                if (consumer.getCurrentDistributor() == null
                        || consumer.getRemainingMonths() == 0) {
                    consumer.setNewDistributor(consumer.getBestDistributor(distributors));
                }

                if (consumer.getRemainingMonths() == 0) {
                    consumer.removeOutdatedConsumer(distributors);
                }

                consumer.payContractRate(distributors);
            }

            for (Distributor distributor : distributors) {
                distributor.payMonthlyCosts();
            }

            /* middle of the month */
            /* update the producers costs */
            for (Producer producer : producers) {
                for (ProducerChanges producerChanges : updates.getProducerChanges()) {
                    if (producer.getId() == producerChanges.getId()) {
                        producer.updateProducerCosts(producerChanges.getEnergyPerDistributor());
                        outputProducers.get(producer.getId()).updateOutputProducerCosts(
                                producerChanges.getEnergyPerDistributor());

                        for (Distributor d : producer.getDistributors()) {
                            d.setNotified(true);
                        }
                    }
                }
            }

            for (Consumer c : consumers) {
                c.removeBankruptConsumer(distributors);
            }

            Distributor.filterBankruptDistributors(distributors, bankruptDistributors);
            distributors.sort(Comparator.comparing(Distributor::getId));

            /* if a consumer is bankrupt, remove them from the distributor
             * and contracts' lists of consumers
             */
            Consumer.filterBankruptConsumers(consumers, bankruptConsumers);
            Distributor.filterBankruptDistributors(distributors, bankruptDistributors);

            for (Distributor d : distributors) {
                if (d.isNotified()) {
                    d.update(producers);
                }
            }

            for (Producer p : producers) {
                int j = 0;
                int[] distributorsIds = new int[p.getDistributors().size()];
                p.getDistributors().sort(Comparator.comparing(Distributor::getId));

                for (Distributor d : p.getDistributors()) {
                    distributorsIds[j] = d.getId();
                    j++;
                }

                MonthlyStat monthlyStat = new MonthlyStat();
                monthlyStat.setMonth(monthNumber);
                monthlyStat.setDistributorsIds(distributorsIds);

                for (ProducerOutput producerOutput : outputProducers) {
                    if (p.getId() == producerOutput.getId()) {
                        producerOutput.getMonthlyStats().add(monthlyStat);
                    }
                }
            }

            monthNumber++;
        }

        consumers.addAll(bankruptConsumers);
        distributors.addAll(bankruptDistributors);
        consumers = consumers.stream()
                .sorted(Comparator.comparing(Consumer::getId))
                .collect(Collectors.toList());
        distributors = distributors.stream()
                .sorted(Comparator.comparing(Distributor::getId))
                .collect(Collectors.toList());

        for (Consumer c : consumers) {
            ConsumerOutput outConsumer = (ConsumerOutput) factory.createEntity("consumer");
            outConsumer.setId(c.getId());
            outConsumer.setBankrupt(c.isBankrupt());
            outConsumer.setBudget(c.getInitialBudget());
            outputConsumers.add(outConsumer);
        }

        for (Distributor d : distributors) {
            List<ContractOutput> outContracts = new ArrayList<>();
            DistributorOutput outDistributor =
                    (DistributorOutput) factory.createEntity("distributor");
            outDistributor.setId(d.getId());
            outDistributor.setEnergyNeededKW(d.getEnergyNeededKW());
            outDistributor.setContractCost((int) d.getPrice());
            outDistributor.setBudget(d.getInitialBudget());
            outDistributor.setProducerStrategy(d.getProducerStrategy());
            outDistributor.setIsBankrupt(d.isBankrupt());

            for (Contract contract : d.getContracts()) {
                for (Consumer consumer : contract.getConsumers()) {
                    ContractOutput outContract = new ContractOutput();
                    outContract.setConsumerId(consumer.getId());
                    outContract.setPrice((int) contract.getPrice());
                    outContract.setRemainedContractMonths(consumer.getRemainingMonths());
                    outContracts.add(outContract);
                }
            }
            outDistributor.setContracts(outContracts);
            outputDistributors.add(outDistributor);
        }

        outputData.setConsumers(outputConsumers);
        outputData.setDistributors(outputDistributors);
        outputData.setEnergyProducers(outputProducers);

        return outputData;
    }
}
