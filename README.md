# Distributed System Udemy Course
https://www.udemy.com/course/distributed-systems-cloud-computing-with-java/learn/lecture/12936914?start=0#overview

## Install zookeeper
Download: https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.8.1/apache-zookeeper-3.8.1-bin.tar.gz
```shell
cd
mkdir zookeeper && cd zookeeper\
mv ~/Downloads/apache-zookeeper-3.8.1-bin.tar.gz .
mkdir logs
tar xzvf apache-zookeeper-3.8.1-bin.tar.gz
rm apache-zookeeper-3.8.1-bin.tar.gz 

# Change log temp folder pointing to the last logs path
vi apache-zookeeper-3.8.1-bin/conf/zoo-sample.conf
mv ~/zookeeper/apache-zookeeper-3.8.1-bin/conf/zoo-sample.conf ~/zookeeper/apache-zookeeper-3.8.1-bin/conf/zoo.conf

cd ~/zookeeper/apache-zookeeper-3.8.1-bin/bin
./zkServer.sh start


```