## 语法

```
sed [-hnV][-e<script>][-f<script文件>][文本文件]
```

## 参数说明：
+ -e\<script>或--expression=\<script> 以选项中指定的script来处理输入的文本文件。  
+ -f\<script文件>或--file=\<script文件> 以选项中指定的script文件来处理输入的文本文件。  
+ -h或--help 显示帮助。  
+ -n或--quiet或--silent 仅显示script处理后的结果。  
+ -V或--version 显示版本信息。
+ -i：直接修改文件内容
+ -r：启用扩展的正则表达式，若与其他选项一起使用，应作为首个选项  

动作说明：
+ a ：新增， a 的后面可以接字串，而这些字串会在新的一行出现(目前的下一行)～
+ c ：取代， c 的后面可以接字串，这些字串可以取代 n1,n2 之间的行！
+ d ：删除，因为是删除啊，所以 d 后面通常不接任何咚咚；
+ i ：插入， i 的后面可以接字串，而这些字串会在新的一行出现(目前的上一行)；
+ p ：打印，亦即将某个选择的数据印出。通常 p 会与参数 sed -n 一起运行～
+ g :匹配每一行有行首到行尾的所有字符，不加g，匹配每一行的行首开始匹配，匹配到第一个符合的字段，就会结束，跳到下一行
+ s ：取代，可以直接进行取代的工作哩！通常这个 s 的动作可以搭配正规表示法，例如 1,20s/old/new/g

**注意：使用 -i 直接修改文件时，同时使用pg会产生两行相同的，且不会打印如：**  
**``sed -i 1,20s/old/new/pg file``**

## 常见处理操作示例

|操作|示例|含义解析|
|-|-|-|
|输出文本|sed '1p' a.txt 或 sed -n 'p' a.txt|输出所有行，等同于cat a.txt|
||sed -n '1p' a.txt|输出第1行|
||sed -n '4p' a.txt|输出第4行|
||sed -n '$p' a.txt||
||sed -n '5,$p' a.txt|从第5行输出到最后一行|
||sed -n '4,7p' a.txt|输出第4～7行|
||sed -n '4,+10p' a.txt|输出第4行及其后的10行内容，共11行|
||sed -n '2p;5p;7p' a.txt||
||sed -n '{2p;5p;7p}' a.txt|输出第2,5,7行       用分号来隔离多个操作（如果有定址条件，则应该使用{ }括起来）|
||sed -n '/a/p' a.txt||
||sed -n '/A/p' a.txt||
||sed -n '/^id/p' a.txt|列出以id开头的行|
||sed -n '/^a/p;/^r/p' a.txt||
||sed -n '/local$/p' a.txt|输出以local结尾的行|
||sed -n 'p;n' a.txt|输出奇数行，n表示读入下一行文本（隔行）next|
||sed -n 'n;p' a.txt|输出偶数行，n表示读入下一行文本（隔行）|
||sed -n '$=' a.txt|输出文件的行数，wc -l返回行数及文件名|
|删除文本|sed  'd' a.txt|删除所有|
||sed  '$d' a.txt|删除文件的最后一行|
||sed  '/^$/d' a.txt|删除所有空行|
||sed  '1d' a.txt|删除第1行|
||sed  '2,5d' a.txt|删除第2～5行|
||sed  '5d;7d;9d' a.txt|删除第5、7、9行|
||sed '/init/d;/bin/d' a.txt|删除所有包含“init”及“bin”的行|
||sed  '/[0-9]/d' a.txt||
||sed  '/^#/d' a.txt||
||sed  '/^s/d' a.txt||
||sed -i  '/^s/d' a.txt|直接删除|
||sed  '/^install/d' a.txt|删除以install开头的行|
||sed  '/xml/d' a.txt|删除所有包含xml的行，只作输出，不更改原文件，若需要更改，应添加选项-i|
||sed  '/xml/！d' a.txt等效于sed -n '/xml/p' a.txt|删除不包含xml的行，！符号表示取反|
替换文本|sed 's/xml/XML/' a.txt|将每行中第1个xml替换为XML|
||sed 's/xml/XML/3' a.txt|将每行中第3个xml替换为XML，只作输出，不更改原文件（若需要更改，应添加选项-i）|
||sed '2s/xml/XML/3' a.txt|将第2行中第3个xml替换为XML，只作输出，不更改原文件（若需要更改，应添加选项-i）|
||sed 's/xml/XML/g' a.txt|将所有的xml都替换为XML|
||sed 's/xml//g' a.txt|将所有的xml都删除（替换为空串）|
||sed 's/doc/＆s/g' a.txt|将所有的doc都替换为docs，＆代表查找串|
||sed '4,7s/^/#/' a.txt|将第4～7行注释掉（行首加#号）|
||sed '3,5s/^#//' a.txt|解除文件第3~5行的注释（去掉开头的 # ）|
||sed 's/^#an/an/' a.txt|解除以＃an开头的行的注释（去除行首的＃号）|
||sed 's/xml\|XML\|e//g' a.txt|删除所有的“xml”、所有的“XML”、所有的字母e，或者的关系用转义方式 \| 来表示|