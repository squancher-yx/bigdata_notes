Raft概述
我们主要分以下三部分对Raft进行讨论：

Leader election——a new leader must be chosen when
an existing leader fails. （领导人选举）
Log replication——the leader must accept log entries from clients and replicate them across the cluster,
forcing the other logs to agree with its own.（日志复制）
Safety——the key safety property for Raft. （安全性）
正常工作过程中，Raft分为两部分，首先是leader选举过程，然后在选举出来的leader基础上进行正常操作，比如日志复制操作等。

一个Raft集群通常包含2N+1个服务器，允许系统有N个故障服务器。每个服务器处于3个状态之一：leader、follower或candidate。正常操作状态下，仅有一个leader，其他的服务器均为follower。follower是被动的，不会对自身发出的请求而是对来自leader和candidate的请求做出响应。leader处理所有的client请求（若client联系follower，则该follower将转发给leader)。candidate状态用来选举leader。状态转换如下图所示：
<div align="center"> <img width="600px" src="https://github.com/squancher-yx/bigdata_notes/tree/master/pictures/raft.png"/> </div>
