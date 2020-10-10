## 一、选择机制中的概念  
1、Serverid：服务器ID  
比如有三台服务器，编号分别是1,2,3。  

>编号越大在选择算法中的权重越大。

2、Zxid：数据ID  
服务器中存放的最大数据ID.

>值越大说明数据越新，在选举算法中数据越新权重越大。

3、Epoch：逻辑时钟  
或者叫投票的次数，同一轮投票过程中的逻辑时钟值是相同的。每投完一次票这个数据就会增加，然后与接收到的其它服务器返回的投票信息中的数值相比，根据不同的值做出不同的判断。

4、Server状态：选举状态  
LOOKING，竞选状态。  
FOLLOWING，随从状态，同步leader状态，参与投票。  
OBSERVING，观察状态,同步leader状态，不参与投票。  
LEADING，领导者状态。

## 二、选举消息内容  
在投票完成后，需要将投票信息发送给集群中的所有服务器，它包含如下内容。

服务器ID  
数据ID  
逻辑时钟  
选举状态  

## 三、选举流程详述

1. 首先开始选举阶段，每个Server读取自身的zxid。

2. 发送投票信息  
   a、首先，每个Server第一轮都会投票给自己。  
   b、投票信息包含 ：所选举leader的Serverid，Zxid，Epoch。Epoch会随着选举轮数的增加而递增。  
   
3. 接收投票信息  
&nbsp;&nbsp;1、如果服务器B接收到服务器A的数据（服务器A处于选举状态(LOOKING 状态)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1）首先，判断逻辑时钟值：  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a）如果发送过来的逻辑时钟Epoch大于目前的逻辑时钟。首先，更新本逻辑时钟Epoch，同时清空本轮逻辑时钟收集到的来自其他server的选举数据。然后，判断是否需要更新当前自己的选举leader Serverid。判断规则rules judging：保存的zxid最大值和leader Serverid来进行判断的。先看数据zxid,数据zxid大者胜出;其次再判断leader Serverid,leader Serverid大者胜出；然后再将自身最新的选举结果(也就是上面提到的三种数据（leader Serverid，Zxid，Epoch）广播给其他server)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b）如果发送过来的逻辑时钟Epoch小于目前的逻辑时钟。说明对方server在一个相对较早的Epoch中，这里只需要将本机的三种数据（leader Serverid，Zxid，Epoch）发送过去就行。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;c）如果发送过来的逻辑时钟Epoch等于目前的逻辑时钟。再根据上述判断规则rules judging来选举leader ，然后再将自身最新的选举结果(也就是上面提到的三种数据（leader  Serverid，Zxid，Epoch）广播给其他server)。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2）其次，判断服务器是不是已经收集到了所有服务器的选举状态：若是，根据选举结果设置自己的角色(FOLLOWING还是LEADER)，退出选举过程就是了。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最后，若没有收到没有收集到所有服务器的选举状态：也可以判断一下根据以上过程之后最新的选举leader是不是得到了超过半数以上服务器的支持,如果是,那么尝试在200ms内接收一下数据,如果没有新的数据到来,说明大家都已经默认了这个结果,同样也设置角色退出选举过程。  

&nbsp;&nbsp;2、 如果所接收服务器A处在其它状态（FOLLOWING或者LEADING）。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a)逻辑时钟Epoch等于目前的逻辑时钟，将该数据保存到recvset。此时Server已经处于LEADING状态，说明此时这个server已经投票选出结果。若此时这个接收服务器宣称自己是leader, 那么将判断是不是有半数以上的服务器选举它，如果是则设置选举状态退出选举过程。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b) 否则这是一条与当前逻辑时钟不符合的消息，那么说明在另一个选举过程中已经有了选举结果，于是将该选举结果加入到outofelection集合中，再根据outofelection来判断是否可以结束选举,如果可以也是保存逻辑时钟，设置选举状态，退出选举过程。  