import java.util.Arrays;
import java.util.List;

public class Application {

  private static final String WORKER_ADDRESS_1 = "http://localhost:8081/task";
  private static final String WORKER_ADDRESS_2 = "http://localhost:8082/task";
  public static void main(String[] args) {
    Aggregator aggregator = new Aggregator();
    String task1 = "10,200";
    String task2 = "1234,12341234,123412341234";

    String task3 = "1234,4321";

    List<String> results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1,
            WORKER_ADDRESS_2), Arrays.asList(task1, task2));

    for (String result : results) {
      System.out.println(result);
    }
  }
}
