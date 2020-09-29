创建删除查看数据库
```
CREATE DATABASE database;
DROP DATABASE database;
SHOW DATABASES;
```
创建和删除表
```
USE table
CREATE TABLE table (
(列名) (类型),
(列名) (类型),
........
);
DROP TABLE (表名);
SHOW TABLES;
``` 

查看表结构
```
DESC (表名);
SHOW CREATE TABLE (表名);
 ```
插入数据
```
INSERT INTO (表名) SET (列名)=(内容),(列名)=(内容).....;
INSERT INTO (表名) VALUES (内容,内容.....),(内容,内容.....).....;         #内容个数与列表个数相同
INSERT INTO 表名(字段1,字段2,...,字段m) VALUES (值1,值2,...,值m),(值1,值2,...,值m).....;
```

表操作

修改表名
```
ALTER TABLE(之前的表名) RENAME (之后的表名)；
```
修改列名及表类型
```
ALTER TABLE(表名) CHANGE COLUMN (之前的列名) (之后的列名) varchar(20);
ALTER TABLE(表名) CONVERT TO CHARACTER SET utf8;
```
添加列
```
ALTER TABLE(表名) ADD COLUMN  (列名) varchar(30);
ALTER TABLE(表名) ADD COLUMN  (列名) varchar(30) FIRST;
ALTER TABLE(表名) ADD COLUMN  (列名) varchar(30) AFTER (列名);
```
删除列
```
ALTER TABLE(表名) DROP COLUMN (列名);
```

删除表数据

程度从强到弱
1、DROP TABLE tb 
	drop将表格直接删除，没有办法找回
2、TRUNCATE tb
	删除表中的所有数据，不能与where一起使用
3、DELETE FROM tb (where)
	删除表中的数据(可制定某一行)
	
区别：truncate和delete的区别
1、事务：truncate是不可以rollback的，但是delete是可以rollback的；
   原因：truncate删除整表数据(ddl语句,隐式提交)，delete是一行一行的删除，可以rollback
2、效果：truncate删除后将重新水平线和索引(id从零开始) ,delete不会删除索引    
3、truncate 不能触发任何Delete触发器。  
4、delete 删除可以返回行数

查看编码及引擎
1、查看 MySQL 数据库服务器和数据库字符集
```
show variables like'%char%';
```
2、查看 MySQL 数据库服务器和数据库核对排序方式（校对规则）
```
SHOWVARIABLES LIKE 'COLLATION';
```
3、查看当前安装的 MySQL 所支持的字符集。
```
show charset;
```
4、查看当前数据库编码：
```
SHOW CREATE DATABASE db_name;
```
5、查看表编码：
```
SHOW CREATE TABLE tbl_name;
```
6、查看字段编码：
```
SHOW FULL COLUMNS FROM tbl_name;
```

四、修改字符集
1.修改数据库的字符集
```
mysql>usemydb
mysql>ALTER DATABASE mydb CHARACTER SET utf-8(utf8) COLLATE utf8_general_ci
```
2.修改表的字符集
把表默认的字符集和所有字符列(CHAR,VARCHAR,TEXT)改为新的字符集：
```
ALTER TABLE tbl_name CONVERT TO CHARACTER SET character_name[COLLATE ...]
```
如：
```
ALTER TABLE logtest CONVERT TO CHARACTER set utf8 COLLATE utf8_general_ci;
```
只是修改表的默认字符集：
```
ALTER TABLE tbl_name DEFAULT CHARACTER SET character_name[COLLATE...];
```
如：
```
ALTER TABLE logtest DEFAULT CHARACTER utf8 COLLATE utf8_general_ci;
```
3.修改字段的字符集：
```
ALTER TABLE tbl_nameCHANGE c_name c_name CHARACTER SET character_name [COLLATE ...];
```
如：  
```
ALTER TABLE logtest CHANGE title titleVARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci;
```

事务

初始化事务  
初始化MySQL事务，首先声明初始化MySQL事务后所有的SQL语句为一个单元。在MySQL中，应用``START TRANSACTION``命令来标记一个事务的开始。初始化事务的结构如下：  
START TRANSACTION；  
另外，用户也可以使用``BEGIN``或者``BEGIN WORK``命令初始化事务，通常``START TRANSACTION``命令后面跟随的是组成事务的SQL语句。  
在命令提示符中输入如下命令：  
```
start transaction；
```
创建保留点
```
savepoint xxxx;
```
回滚
```
rollback to xxxx;
```
提交
```
commit;
```