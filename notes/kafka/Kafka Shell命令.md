#### 创建kafka topic
```bin/kafka-topics.sh --zookeeper node01:2181 --create --topic t_cdr --partitions 30  --replication-factor 2```
注： partitions指定topic分区数，replication-factor指定topic每个分区的副本数

+ partitions分区数:
  + partitions ：分区数，控制 topic 将分片成多少个 log。可以显示指定，如果不指定则会使用broker(`server.properties`)中的 num.partitions 配置的数量
  + 虽然增加分区数可以提供 kafka 集群的吞吐量、但是过多的分区数或者或是单台服务器上的分区数过多，会增加不可用及延迟的风险。因为多的分区数，意味着需要打开更多的文件句柄、增加点到点的延时、增加客户端的内存消耗。
  +分区数也限制了 consumer 的并行度，即限制了并行 consumer 消息的线程数不能大于分区数
  + 分区数也限制了 producer发送消息是指定的分区。如创建 topic 时分区设置为1，producer 发送消息时通过自定义的分区方法指定分区为2或以上的数都会出错的；这种情况可以通过`alter –partitions`来增加分区数。
+ replication-factor副本

  + replication factor 控制消息保存在几个 broker (服务器)上，一般情况下等于 broker 的个数。
  + 如果没有在创建时显示指定或通过API向一个不存在的 topic 生产消息时会使用 broker(`server.properties`)中的`default.replication.factor`配置的数量

#### 查看所有topic列表
```bin/kafka-topics.sh --zookeeper node01:2181 --list```

#### 查看指定topic信息
```bin/kafka-topics.sh --zookeeper node01:2181 --describe --topic t_cdr```

#### 控制台向topic生产数据
```bin/kafka-console-producer.sh --broker-list node86:9092 --topic t_cdr```

#### 控制台消费topic的数据
```bin/kafka-console-consumer.sh  --zookeeper node01:2181  --topic t_cdr --from-beginning```

#### 查看topic某分区偏移量最大（小）值
```bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic hive-mdatabase-hostsltable  --time -1 --broker-list node86:9092 --partitions 0```
注： time为-1时表示最大值，time为-2时表示最小值


#### 增加topic分区数
为topic t_cdr 增加10个分区

```bin/kafka-topics.sh --zookeeper node01:2181  --alter --topic t_cdr --partitions 10```

#### 删除topic
```bin/kafka-run-class.sh kafka.admin.DeleteTopicCommand --zookeeper node01:2181 --topic t_cdr```
注：慎用，只会删除zookeeper中的元数据，消息文件须手动删除

#### 查看topic消费进度
这个会显示出 consumer group 的 offset 情况， 必须参数为 --group， 不指定 --topic，默认为所有 topic

Displays the: Consumer Group, Topic, Partitions, Offset, logSize, Lag, Owner for the specified set of Topics and Consumer Group
```
bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker

required argument: [group] 
Option Description 
------ ----------- 
--broker-info Print broker info 
--group Consumer group. 
--help Print this message. 
--topic Comma-separated list of consumer 
   topics (all topics if absent). 
--zkconnect ZooKeeper connect string. (default: localhost:2181)

Example,

bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --group pv

Group           Topic              Pid Offset   logSize    Lag    Owner 
pv              page_visits        0   21       21         0      none 
pv              page_visits        1   19       19         0      none 
pv              page_visits        2   20       20         0      none
```

以上图中参数含义解释如下：
topic：创建时 topic 名称
pid：分区编号
offset：表示该 parition 已经消费了多少条 message
logSize：表示该 partition 已经写了多少条 message
Lag：表示有多少条 message 没有被消费。
Owner：表示消费者

细看 kafka-run-class.sh 脚本，它是调用 了 ConsumerOffsetChecker 的 main 方法，所以，我们也可以通过 java 代码来访问 scala 的 ConsumerOffsetChecker 类，代码如下：

```
import kafka.tools.ConsumerOffsetChecker;  
  
/** 
 * kafka自带很多工具类，其中ConsumerOffsetChecker能查看到消费者消费的情况, 
 * ConsumerOffsetChecker只是将信息打印到标准的输出流中 
 * 
 */  
public class RunClass  {  
    public static void main(String[] args)  {  
        //group-1是消费者的group名称,可以在zk中  
        String[] arr = new String[]{"--zookeeper=192.168.199.129:2181,192.168.199.130:2181,192.168.199.131:2181/kafka","--group=group-1"};  
        ConsumerOffsetChecker.main(arr);  
    }  
}
```

#### 查看consumer group列表

查看 consumer group 列表有新、旧两种命令，分别查看新版(信息保存在 broker 中)consumer 列表和老版(信息保存在 zookeeper 中)consumer 列表，因而需要区分指定 --bootstrap-server 和 zookeeper 参数：
```
bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9292 --list
lx_test
```
```
bin/kafka-consumer-groups.sh --zookeeper 127.0.0.1:2181 --list
console-consumer-86845
console-consumer-11967
```

#### 查看特定consumer group 详情

同样根据新/旧版本的consumer，分别指定 --bootstrap-server 与z ookeeper 参数:
```
bin/kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9292 --group lx_test --describe
GROUP                          TOPIC                          PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             OWNER
lx_test                        lx_test_topic             0          465             465             0               kafka-python-1.3.1_/127.0.0.1
```
```
bin/kafka-consumer-groups.sh --zookeeper 127.0.0.1:2181 --group console-consumer-11967 --describe
GROUP                          TOPIC                          PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             OWNER
Could not fetch offset from zookeeper for group console-consumer-11967 partition [lx_test_topic,0] due to missing offset data in zookeeper.
console-consumer-11967         lx_test_topic             0          unknown         465             unknown         console-consumer-11967_aws-lx-1513787888172-d3a91f05-0
```
其中依次展示 group 名称、消费的topic名称、partition id、consumer group最后一次提交的offset、最后提交的生产消息offset、消费offset与生产offset之间的差值、当前消费topic-partition的group成员id(不一定包含hostname)

上面示例中console-consumer-11967是为了测试临时起的一个console consumer，缺少在zookeeper中保存的current_offset信息。