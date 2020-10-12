**常用的字符串函数**

|函数|说明|
|-|-|
|CONCAT(s1,s2，...)|返回连接参数产生的字符串，一个或多个待拼接的内容，任意一个为NULL则返回值为NULL。|
|CONCAT_WS(x,s1,s2,...)|返回多个字符串拼接之后的字符串，每个字符串之间有一个x。|
|SUBSTRING(s,n,len)、MID(s,n,len)|两个函数作用相同，从字符串s中返回一个第n个字符开始、长度为len的字符串。|
|LEFT(s,n)、RIGHT(s,n)|前者返回字符串s从最左边开始的n个字符，后者返回字符串s从最右边开始的n个字符。|
|INSERT(s1,x,len,s2)|返回字符串s1，其子字符串起始于位置x，被字符串s2取代len个字符。|
|REPLACE(s,s1,s2)|返回一个字符串，用字符串s2替代字符串s中所有的字符串s1。|
|LOCATE(str1,str)、POSITION(str1 IN str)、INSTR(str,str1)|三个函数作用相同，返回子字符串str1在字符串str中的开始位置（从第几个字符开始）。|
|FIELD(s,s1,s2,...)|返回第一个与字符串s匹配的字符串的位置。|

**去掉字符串空格**  

TRIM(str) 去除两边空格  
LTRIM(str)去除左边空格  
RTRIM(str)去除右边空格  

