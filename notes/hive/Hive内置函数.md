***UDTF函数与原表数据关联的时候需要使用lateral view***

## 一  
1. collect_set 函数
+ 查询表中数据 
  ```sql
  hive (gmall)> select \* from stud;
  stud.name stud.area stud.course stud.score
  zhang3 bj math 88 
  li4 bj math 99 
  wang5 sh chinese 92 
  zhao6 sh chinese 54 
  tian7 bj chinese 91 
  ```
+ 把同一分组的不同行的数据聚合成一个集合 
  ```
  hive (gmall)> select course, collect_set(area), avg(score) from stud group by course; 
  chinese ["sh","bj"] 79.0 
  math ["bj"] 93.5 
  ```
+ 用下标可以取某一个 
  ```
  hive (gmall)> select course, collect_set(area)[0], avg(score) from stud group by course; 
  chinese sh 79.0 math bj 93.5
  ```

2. collect_list 函数
类似 collect_set,但是不去重

## 二  
nvl 函数
1. 基本语法 
NVL（表达式 1，表达式 2） 
如果表达式 1 为空值，NVL 返回值为表达式 2 的值，否则返回表达式 1 的值。该函数的目的是把一个空值（null）转换成一个实际的值。其表达式的值可以是数字型、字符型 和日期型。但是表达式 1 和表达式 2 的数据类型必须为同一个类型。

## 三  
日期处理函数
1. date_format 函数（根据格式整理日期） 
  ```
  hive (gmall)> select date_format('2020-03-10','yyyy-MM');
  2020-03
  ```
2. date_add 函数（加减日期）
  ```
  hive (gmall)> select date_add('2020-03-10',-1); 
  2020-03-09 
  hive (gmall)> select date_add('2020-03-10',1); 
  2020-03-11
  ```
3. next_day 函数  
+ 取当前天的下一个周一 
  ```
  hive (gmall)> select next_day('2020-03-12','MO'); 
  2020-03-16 
  ```
  说明：星期一到星期日的英文（Monday，Tuesday、Wednesday、Thursday、Friday、Saturday、Sunday）

+ 取当前周的周一 
  ```
  hive (gmall)> select date_add(next_day('2020-03-12','MO'),-7); 
  2020-03-11
  ```
## 四  
1. last_day 函数（求当月最后一天日期） 
  ```
  hive (gmall)> select last_day('2020-03-10'); 
  2020-03-31
  ```

## 五   
get_json_object 函数（UDF）  
1. 输入数据   
xjson Xjson=[{"name":" aa ","sex":" 男 ","age":"25"},{"name":" bb ","sex":" 男 ","age":"47"}]   
2. 取出第一个 json 对象   
``SELECT get_json_object(xjson,"$.[0]") FROM person;``   
结果是：{"name":"aa","sex":"男","age":"25"}   
3. 取出第一个 json 的 age 字段的值   
``SELECT get_json_object(xjson,"$.[0].age") FROM person; ``  
结果是：25  
只有数组([])时才能用``.[index]``  

## 六   
json_tuple 函数（UDTF）
一行变多行，只能取一层。
有以下数据
  ```
  id line
  1 {"a":"aa","b":{"b":"bb"}}
  2 {"a":"qq","b":{"c":"bb"}}
  ```

  ```
  SELECT id,la_a,la_b FROM tbl LATERAL VIEW json_tuple(line,"a","b") la AS la_a,la_b

  id la_a la_b
  1 aa {"b":"bb"}
  2 qq {"c":"bb"}
  ```

## 七  
1. CONCAT 函数
语法：CONCAT(str1,str2,…)
如有：
```
+----+--------+  
| id | name   |  
+----+--------+  
|  1 | BOb    |  
+----+--------+      
```
  ```
  SELECT CONCAT(id, ',', name) AS con FROM tbl
  ```
```
+----------+  
| con      |  
+----------+  
| 1,BioCyc |  
+----------+  
```
2. CONCAT_WS函数
可指定分隔符
使用语法为：CONCAT_WS(separator,str1,str2,…)
```
SELECT CONCAT_WS('_',id,name) AS con_ws FROM tbl
+----------------------------------------------+
| CONCAT_WS(',','First name',NULL,'Last Name') |
+----------------------------------------------+
| First name,Last Name                         |
+----------------------------------------------+
```

## 七  
NEXT_DAY函数  
语法：next_day(string date, string week)  
返回大于指定日期并且与week相匹配的第一个日期。实质上是指下周几的具体日期。week可以是Mo, tue, FRIDAY等。  
例：``select next_day("2016-10-31", 'FRIDAY')``