import cluster.management.OnElectionCallback;
import cluster.management.ServiceRegistry;
import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OnElectionAction implements OnElectionCallback {
  private final ServiceRegistry serviceRegistry;
  private final int port;

  public OnElectionAction(ServiceRegistry serviceRegistry, int port) {
    this.serviceRegistry = serviceRegistry;
    this.port = port;
  }

  @Override
  public void onElectedToBeLeader() {
    try {
      serviceRegistry.unregisterFromCluster();
      serviceRegistry.registerForUpdates();
    } catch (InterruptedException | KeeperException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onWorker() {
    try {
      String currentServerAddress = String.format("http://%s:%d",
              InetAddress.getLocalHost().getCanonicalHostName(), port);
      serviceRegistry.registerToCluster(currentServerAddress);

    } catch (UnknownHostException | InterruptedException | KeeperException e) {
      throw new RuntimeException(e);
    }
  }
}
