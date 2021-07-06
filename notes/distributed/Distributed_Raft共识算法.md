## Raft概述
分以下三部分对Raft进行讨论：
[Raft 原理动画](http://thesecretlivesofdata.com/raft/)
+ Leader election——a new leader must be chosen when an existing leader fails. （领导人选举）
+ Log replication——the leader must accept log entries from clients and replicate them across the cluster, forcing the other logs to agree with its own.（日志复制）
+ Safety——the key safety property for Raft. （安全性）
正常工作过程中，Raft分为两部分，首先是leader选举过程，然后在选举出来的leader基础上进行正常操作，比如日志复制操作等。

一个Raft集群通常包含2N+1个服务器，允许系统有N个故障服务器。每个服务器处于3个状态之一：leader、follower或candidate。正常操作状态下，仅有一个leader，其他的服务器均为follower。follower是被动的，不会对自身发出的请求而是对来自leader和candidate的请求做出响应。leader处理所有的client请求（若client联系follower，则该follower将转发给leader)。candidate状态用来选举leader。状态转换如下图所示：

<div align="center"> <img width="600px" src="https://gitee.com/squancher/bigdata_notes/raw/master/pictures/raft.png"/> </div>


为了进行领导人选举和日志复制等，需要服务器节点存储如下状态信息：

|状态|所有服务器上持久存在的|
|-|-|
|currentTerm|服务器最后一次知道的任期号（初始化为 0，持续递增）|
|votedFor|在当前获得选票的候选人的 Id|
|log[]|日志条目集；每一个条目包含一个用户状态机执行的指令，和收到时的任期号|

|状态|所有服务器上经常变的|
|-|-|
|commitIndex|已知的最大的已经被提交的日志条目的索引值|
|lastApplied|最后被应用到状态机的日志条目索引值（初始化为 0，持续递增）|

|状态|在领导人里经常改变的 （选举后重新初始化）|
|-|-|
|nextIndex[]|对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）|
|matchIndex[]|对于每一个服务器，已经复制给他的日志的最高索引值|


Raft在任何时刻都满足如下特性：

+ Election Safety：在一个任期中只能有一个leader；
+ Leader Append-Only：leader不会覆盖或删除日志中的entry，只有添加entry（follower存在依据leader回滚日志的情况）；
+ Log Matching：如果两个日志包含了一条具有相同index和term的entry，那么这两个日志在这个index之前的所有entry都相同；
+ Leader Completeness： 如果在某一任期一条entry被提交committed了，那么在更高任期的leader中这条entry一定存在；
+ State Machine Safety：如果一个节点将一条entry应用到状态机中，那么任何节点也不会再次将该index的entry应用到状态机里；


## Leader选举（Leader election）
一个节点初始状态为follower，当follower在选举超时时间内未收到leader的心跳消息，则转换为candidate状态。为了避免选举冲突，这个超时时间是一个随机数（一般为150~300ms）。超时成为candidate后，向其他节点发出RequestVoteRPC请求，假设有2N+1个节点，收到N+1个节点以上的同意回应，即被选举为leader节点，开始下一阶段的工作。如果在选举期间接收到eader发来的心跳信息，则candidate转为follower状态。

>  在选举期间，可能会出现多个candidate的情况，可能在一轮选举过程中都没有收到多数的同意票，此时再次随机超时，进入第二轮选举过程，直至选出leader或着重新收到leader心跳信息，转为follower状态。

正常状态下，leader会不断的广播心跳信息，follower收到leader的心跳信息后会重置超时。当leader崩溃或者出现异常离线，此时网络中follower节点接收不到心跳信息，超时再次进入选举流程，选举出一个leader。

>  这里还有补充一些细节，每个leader可以理解为都是有自己的任期(term)的，每一期起始于选举阶段，直到因节点失效等原因任期结束。每一期选举期间，每个follower节点只能投票一次。图中t3可能是因为没有获得超半数票等造成选举失败，须进行下一轮选举，此时follower可以再次对最先到达的candidate发出的RequestVote请求投票（先到先得）。
<div align="center"> <img width="600px" src="https://gitee.com/squancher/bigdata_notes/raw/master/pictures/raft2.png"/> </div>
对所有的请求（RequestVote、AppendEntry等请求），如果发现其Term小于当前节点，则拒绝请求，如果是candidate选举期间，收到不小于当前节点任期的leader节点发来的AppendEntry请求，则认可该leader，candidate转换为follower。

## 日志复制（Log replication）

leader选举成功后，将进入有效工作阶段，即日志复制阶段，其中日志复制过程会分记录日志和提交数据两个阶段。

整个过程如下：

+ 首先client向leader发出command指令；（每一次command指令都可以认为是一个entry）
+ leader收到client的command指令后，将这个command entry追加到本地日志中，此时这个command是uncommitted状态，因此并没有更新节点的当前状态；
+ 之后，leader向所有follower发送这条entry，follower接收到后追加到日志中，并回应leader；
+ leader收到大多数follower的确认回应后，此entry在leader节点由uncommitted变为committed状态，此时按这条command更新leader状态；
+ 在下一心跳中，leader会通知所有follower更新确认的entry，follower收到后，更新状态，这样，所有节点都完成client指定command的状态更新。

可以看到client每次提交command指令，服务节点都先将该指令entry追加记录到日志中，等leader确认大多数节点已追加记录此条日志后，在进行提交确认，更新节点状态。如果还对这个过程有些模糊的话，可以参考Raft动画演示，较为直观的演示了领导人选举及日志复制的过程。

## 安全（Safety）

前面描述了Raft算法是如何选举和复制日志的。然而，到目前为止描述的机制并不能充分的保证每一个状态机会按照相同的顺序执行相同的指令。我们需要再继续深入思考以下几个问题：
+ 第一个问题，leader选举时follower收到candidate发起的投票请求，如果同意就进行回应，但具体的规则是什么呢？是所有的follower都有可能被选举为领导人吗？
+ 第二个问题，leader可能在任何时刻挂掉，新任期的leader怎么提交之前任期的日志条目呢？

**选举限制**
针对第一个问题，之前并没有细讲，如果当前leader节点挂了，需要重新选举一个新leader，此时follower节点的状态可能是不同的，有的follower可能状态与刚刚挂掉的leader相同，状态较新，有的follower可能记录的当前index比原leader节点的少很多，状态更新相对滞后，此时，从系统最优的角度看，选状态最新的candidate为佳，从正确性的角度看，要确保Leader Completeness，即如果在某一任期一条entry被提交成功了，那么在更高任期的leader中这条entry一定存在，反过来讲就是如果一个candidate的状态旧于目前被committed的状态，它一定不能被选为leader。具体到投票规则：
+ 节点只投给拥有不比自己日志状态旧的节点；
+ 每个节点在一个term内只能投一次，在满足1的条件下，先到先得；

看一下请求投票 RPC（由候选人负责调用用来征集选票）的定义：

|参数|解释|
|-|-|
|term|候选人的任期号|
|candidateId|请求选票的候选人的 Id|
|lastLogIndex|候选人的最后日志条目的索引值|
|lastLogTerm	候选人最后日志条目的任期号|

|返回值|解释|
|-|-|
|term|当前任期号，以便于候选人去更新自己的任期号|
|voteGranted|候选人赢得了此张选票时为真|

接收者实现：
+ 如果term < currentTerm返回 false
+ 如果 votedFor 为空或者为 candidateId，并且候选人的日志至少和自己一样新，那么就投票给他

可以看到RequestVote投票请求中包含了lastLogIndex和lastLogTerm用于比较日志状态。这样，虽然不能保证最新状态的candidate成为leader，但能够保证被选为leader的节点一定拥有最新被committed的状态，但不能保证拥有最新uncommitted状态entries。

**提交之前任期的日志条目**
领导人知道一条当前任期内的日志记录是可以被提交的，只要它被存储到了大多数的服务器上。但是之前任期的未提交的日志条目，即使已经被存储到大多数节点上，也依然有可能会被后续任期的领导人覆盖掉。下图说明了这种情况：

<div align="center"> <img width="600px" src="https://gitee.com/squancher/bigdata_notes/raw/master/pictures/raft3.png"/> </div>

> 如图的时间序列展示了为什么领导人无法决定对老任期号的日志条目进行提交。在 (a) 中，S1 是领导者，部分的复制了索引位置 2 的日志条目。在 (b) 中，S1崩溃了，然后S5在任期3里通过S3、S4和自己的选票赢得选举，然后从客户端接收了一条不一样的日志条目放在了索引 2 处。然后到 (c)，S5又崩溃了；S1重新启动，选举成功，开始复制日志。在这时，来自任期2的那条日志已经被复制到了集群中的大多数机器上，但是还没有被提交。如果S1在(d)中又崩溃了，S5可以重新被选举成功（通过来自S2，S3和S4的选票），然后覆盖了他们在索引 2 处的日志。反之，如果在崩溃之前，S1 把自己主导的新任期里产生的日志条目复制到了大多数机器上，就如 (e) 中那样，那么在后面任期里面这些新的日志条目就会被提交（因为S5 就不可能选举成功）。 这样在同一时刻就同时保证了，之前的所有老的日志条目就会被提交。
为了消除上图里描述的情况，Raft永远不会通过计算副本数目的方式去提交一个之前任期内的日志条目。只有领导人当前任期里的日志条目通过计算副本数目可以被提交；一旦当前任期的日志条目以这种方式被提交，那么由于日志匹配特性，之前的日志条目也都会被间接的提交。
> 当领导人复制之前任期里的日志时，Raft 会为所有日志保留原始的任期号。

## 对Raft中几种情况的思考

**follower节点与leader日志内容不一致时怎么处理？**

<div align="center"> <img width="600px" src="https://gitee.com/squancher/bigdata_notes/raw/master/pictures/raft4.png"/> </div>

先举例说明：正常情况下，follower节点应该向B节点一样与leader节点日志内容一致，但也会出现A、C等情况，出现了不一致，以A、B节点为例，当leader节点向follower节点发送AppendEntries<prevLogIndex=7,prevLogTerm=3,entries=[x<-4]>,leaderCommit=7时，我们分析一下发生了什么，B节点日志与prevLogIndex=7,prevLogTerm=3相匹配，将index=7（x<-5）这条entry提交committed，并在日志中新加入entryx<-4，处于uncommitted状态；A节点接收到时，当前日志index<prevLogIndex与prevLogIndex=7,prevLogTerm=3不相匹配，拒接该请求，不会将x<-4添加到日志中，当leader知道A节点因日志不一致拒接了该请求后，不断递减preLogIndex重新发送请求，直到A节点index,term与prevLogIndex,prevLogTerm相匹配，将leader的entries复制到A节点中，达成日志状态一致。

我们看一下附加日志 RPC（由领导人负责调用复制日志指令；也会用作heartbeat）的定义：

|参数|解释|
|-|-|
|term|领导人的任期号|
|leaderId|领导人的 Id，以便于跟随者重定向请求|
|prevLogIndex|新的日志条目紧随之前的索引值|
|prevLogTerm|prevLogIndex 条目的任期号|
|entries[]|准备存储的日志条目（表示心跳时为空；一次性发送多个是为了提高效率）|
|leaderCommit|领导人已经提交的日志的索引值|

|返回值|解释|
|term|当前的任期号，用于领导人去更新自己|
|success|跟随者包含了匹配上 prevLogIndex 和 prevLogTerm 的日志时为真|

接收者实现：
+ 如果 term < currentTerm 就返回 false；
+ 如果日志在 prevLogIndex 位置处的日志条目的任期号和 prevLogTerm 不匹配，则返回 false；
+ 如果已经存在的日志条目和新的产生冲突（索引值相同但是任期号不同），删除这一条和之后所有的；
+ 附加日志中尚未存在的任何新条目；
+ 如果 leaderCommit > commitIndex，令 commitIndex 等于 leaderCommit 和 新日志条目索引值中较小的一个；

简单总结一下，出现不一致时核心的处理原则是一切遵从leader。当leader向follower发送AppendEntry请求，follower对AppendEntry进行一致性检查，如果通过，则更新状态信息，如果发现不一致，则拒绝请求，leader发现follower拒绝请求，出现了不一致，此时将递减nextIndex，并重新给该follower节点发送日志复制请求，直到找到日志一致的地方为止。然后把follower节点的日志覆盖为leader节点的日志内容。

**leader挂掉了，怎么处理**
前面可能断断续续的提到这种情况的处理方法，首要的就是选出新leader，选出新leader后，可能上一任期还有一些entries并没有提交，处于uncommitted状态，该怎么办呢？处理方法是新leader只处理提交新任期的entries，上一任期未提交的entries，如果在新leader选举前已经被大多数节点记录在日志中，则新leader在提交最新entry时，之前处于未提交状态的entries也被committed了，因为如果两个日志包含了一条具有相同index和term的entry，那么这两个日志在这个index之前的所有entry都相同；如果在新leader选举前没有被大多数节点记录在日志中，则原有未提交的entries有可能被新leader的entries覆盖掉。

**出现网络分区时怎么处理？**
分布式系统中网络分区的情况基本无法避免，出现网络分区时，原有leader在分区的一侧，此时如果客户端发来指令，旧leader依旧在分区一测进行日志复制的过程，但因收不到大多数节点的确认，客户端所提交的指令entry只能记录在日志中，无法进行提交确认，处于uncommitted状态。而在分区的另一侧，此时收不到心跳信息，会进入选举流程重新选举一个leader，新leader负责分区零一侧的请求，进行日志复制等操作。因为新leader可以收到大多数follower确认，客户端的指令entry可以被提交，并更新节点状态，当网络分区恢复时，此时两个leader会收到彼此广播的心跳信息，此时，旧leader发现更大term的leader，旧leader转为follower，此时旧leader分区一侧的所有操作都要回滚，接受新leader的更新。

