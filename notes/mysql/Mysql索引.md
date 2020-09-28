## 索引的创建

[官方网址](https://dev.mysql.com/doc/refman/5.7/en/create-index.html)

### 建表时创建：
```
CREATE TABLE 表名(
字段名 数据类型 [完整性约束条件],
...
[UNIQUE | FULLTEXT | SPATIAL] [INDEX | KEY] [索引名](字段名1[(长度)], ... [ASC | DESC]) [USING [BTREE | HASH]]
[PRIMARY KEY] (字段名1 [(长度), ...] [ASC | DESC]) [USING [BTREE | HASH]]
);

[]:可选
```
说明：  
PRIMARY KEY：主键, 
UNIQUE:可选。表示索引为唯一性索引。  
FULLTEXT:可选。表示索引为全文索引，只有MyISAM引擎支持（5.7Innodb支持？）。  
SPATIAL:可选。表示索引为空间索引，GIS支持。（MySQL5.7中InnoDB支持Spatial Index，可以对空间数据类型建立索引）  
INDEX和KEY:用于指定字段为索引，两者选择其中之一就可以了，作用是一样的。  
索引名:可选。给创建的索引取一个新名称。  
字段名1:指定索引对应的字段的名称，该字段必须是前面定义好的字段。  
长度:可选。指索引的长度，必须是字符串（BLOB | TEXT | VARCHAR ...）类型才可以使用。**（前缀索引）**  
ASC:可选。表示升序排列。  
DESC:可选。表示降序排列。  
USING:见下表  


|Storage Engine|Permissible Index Types|
|-|-|
|InnoDB|BTREE|
|MyISAM|BTREE|
|MEMORY/HEAP|HASH, BTREE|
|NDB|HASH, BTREE (see note in text)|

### 建表后创建：  
```
ALTER TABLE 表名 ADD [UNIQUE | FULLTEXT | SPATIAL]  INDEX | KEY  [索引名]((字段名1)[(长度)], ... [ASC | DESC]) [USING 索引方法];
或
CREATE  [UNIQUE | FULLTEXT | SPATIAL]  INDEX  索引名 ON 表名(字段名1[(长度)], ...) [USING 索引方法];
```


### 查看已创建的索引：
```
show index from 表名;
```

索引的删除：
```
DROP INDEX 索引名 ON 表名
或
ALTER TABLE 表名 DROP INDEX 索引名

如何删除mysql 主键索引
如果一个主键是自增长的，不能直接删除该列的主键索引，
应当先取消自增长，再删除主键特性
ALTER TABLE 表名 DROP PRIMARY KEY; 【如果这个主键是自增的，先取消自增长.】
具体方法如下:
ALTER TABLE (表名) MODIFY id INT; 【重新定义列类型】
ALTER TABLE (表名) DROP PRIMARY KEY;
```

### 聚簇索引&&二级索引&&辅助索引
概念：mysql中每个表都有一个聚簇索引（clustered index ），除此之外的表上的每个非聚簇索引都是二级索引，又叫辅助索引（secondary indexes）  
聚集索引与非聚集索引：
1. 如果表上定义有主键，该主键索引就是聚簇索引。如果未定义主键，MySQL取第一个唯一索引（unique）而且只含非空列（NOT NULL）作为主键，InnoDB使用它作为聚簇索引。如果没有这样的列，InnoDB就自己产生一个这样的ID值，它有六个字节，而且是隐藏的，使其作为聚簇索引。  
2. 聚簇索引的叶子节点存储了一行完整的数据，而二级索引只存储了主键值，相比于聚簇索引，占用的空间要少。当我们需要为表建立多个索引时，如果都是聚簇索引，那将占用大量内存空间，所以InnoDB中主键所建立的是聚簇索引，而唯一索引、普通索引、前缀索引等都是二级索引。

**何时使用聚集索引或非聚集索引**

|动作描述|使用聚集索引|使用非聚集索引|
|-|-|-|
|列经常被分组排序|使用|使用|
|返回某范围内的数据|使用|不使用|
|一个或极少不同值|不使用|不使用|
|小数目的不同值|使用|不使用|
|大数目的不同值|不使用|使用|
|频繁更新的列|不使用|使用|
|外键列|使用|使用|
|主键列|使用|使用|
|频繁修改索引列|不使用|使用|

**回表**  

回表：  
当二级索引无法直接查询到(SQL中select需要的所有)列的数据时，会通过二级索引查询到聚簇索引(即:一级索引)后，再根据(聚集索引)查询到(二级索引中无法提供)的数据，这种通过二级索引查询出一级索引，再通过一级索引查询(二级索引中无法提供的)数据的过程，就叫做回表。

**索引覆盖**

索引覆盖就是查这个索引能查到所需要的所有数据，不需要去另外的数据结构（聚簇索引）去查。其实就是不用回表。

## 常见不走索引的情况

1. 联合索引 is not null 只要在建立的索引列（不分先后）都会走。
2. 过滤字段使用了函数运算后（如abs(column)），MySQL 无法使用索引。
3. 使用非等值查询的时候MySQL 无法使用Hash 索引。

## NULL 字段影响

NULL 字段不参加统计

|id|name|
|-|-|
|1|(NULL)|
|2|qqq|

```
select count(*) from tbl where name<>qqq;
```
结果：0
