import cluster.management.OnElectionCallback;
import cluster.management.ServiceRegistry;
import networking.WebClient;
import networking.WebServer;
import org.apache.zookeeper.KeeperException;
import search.SearchCoordinator;
import search.SearchWorker;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OnElectionAction implements OnElectionCallback {
  private final ServiceRegistry workersServiceRegistry;
  private final ServiceRegistry coordinatorsServiceRegistry;
  private final int port;
  private WebServer webServer;

  public OnElectionAction(ServiceRegistry serviceRegistry, ServiceRegistry coordinatorsServiceRegistry, int port) {
    this.workersServiceRegistry = serviceRegistry;
    this.coordinatorsServiceRegistry = coordinatorsServiceRegistry;
    this.port = port;
  }

  @Override
  public void onElectedToBeLeader() {
    try {
      workersServiceRegistry.unregisterFromCluster();
      workersServiceRegistry.registerForUpdates();

      if (webServer != null) {
        webServer.stop();
      }

      SearchCoordinator searchCoordinator = new SearchCoordinator(workersServiceRegistry, new WebClient());
      webServer = new WebServer(port, searchCoordinator);
      webServer.startServer();

      String currentServerAddress =
          String.format("http://%s:%d%s", InetAddress.getLocalHost().getCanonicalHostName(), port, searchCoordinator.getEndpoint());
      coordinatorsServiceRegistry.registerToCluster(currentServerAddress);

    } catch (InterruptedException | KeeperException | UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onWorker() {
    SearchWorker searchWorker = new SearchWorker();
    webServer = new WebServer(port, searchWorker);
    webServer.startServer();
    try {
      String currentServerAddress = String.format("http://%s:%d",
              InetAddress.getLocalHost().getCanonicalHostName(), port, searchWorker.getEndpoint());
      workersServiceRegistry.registerToCluster(currentServerAddress);

    } catch (UnknownHostException | InterruptedException | KeeperException e) {
      e.printStackTrace();
      return;
    }
  }
}
