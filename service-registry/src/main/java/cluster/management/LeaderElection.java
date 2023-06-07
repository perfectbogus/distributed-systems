package cluster.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {
  private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
  private static final String ELECTION_NAMESPACE = "/election";
  private static final int SESSION_TIMEOUT = 3000;
  private ZooKeeper zooKeeper;
  private String currentZnodeName;

  public LeaderElection() {}
  public LeaderElection(ZooKeeper zooKeeper) {
    this.zooKeeper = zooKeeper;
  }

  public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    LeaderElection leaderElection = new LeaderElection();
    leaderElection.connectToZookeeper();
    leaderElection.volunteerForLeadership();
    leaderElection.reelectLeader();
    leaderElection.run();
    leaderElection.close();
    System.out.println("Disconnected from Zookeeper, exiting application ");
  }

  public void volunteerForLeadership() throws KeeperException, InterruptedException {
    String znodePrefix = ELECTION_NAMESPACE + "/c_";
    String znodeFullPath = zooKeeper.create(znodePrefix, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    System.out.println("znode name: " + znodeFullPath);
    this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
  }

  public void reelectLeader() throws KeeperException, InterruptedException {
    Stat predecessorsStat = null;
    String predecessorZnodeName = "";
    while (predecessorsStat == null) {
      List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
      Collections.sort(children);
      String smallestChild = children.get(0);
      if (smallestChild.equals(currentZnodeName)) {
        System.out.println("I am the leader");
        return;
      } else {
        System.out.println("I am not the leader");
        int predecessorIndex = Collections.binarySearch(children, currentZnodeName)-1;
        predecessorZnodeName = children.get(predecessorIndex);
        predecessorsStat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + predecessorZnodeName, this);
      }
    }
    System.out.println("Watching znode " + predecessorZnodeName);
  }

  public void run() throws InterruptedException {
    synchronized (zooKeeper) {
      zooKeeper.wait();
    }
  }

  public void connectToZookeeper() throws IOException {
    this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
  }

  public void close() throws InterruptedException {
    zooKeeper.close();
  }

  @Override
  public void process(WatchedEvent watchedEvent) {
    switch (watchedEvent.getType()) {
      case None:
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
          System.out.println("Successfully Connected to ZooKeeper");
        } else {
          synchronized (zooKeeper) {
            System.out.println("Disconnected from Zookeeper event");
            zooKeeper.notifyAll();
          }
        }
        break;
      case NodeDeleted:
        try {
          reelectLeader();
        } catch (InterruptedException | KeeperException e) {
          throw new RuntimeException(e);
        }
        break;
    }
  }
}
