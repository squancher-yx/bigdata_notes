package Others;

/**
 * try catch finally 顺序问题
 * 1. 当catch中没有return返回语句是，try  catch  finally是按从上到下的顺序依次执行！
 * 2. 当catch中有return语句时，执行结果是先执行catch中的内容，当执行到return的时候，先去执行finally中的内容，最后执行return语句。
 * 3. 当catch和finally中都有return字句时，finally中的return字句会覆盖catch中的return返回值.
 * 4. 当catch中有return字句，而finally中没有return字句，不过finally中有改变catch中return的返回值时（注意：当返回值类型为基本类型，Date类型时，在finally中修改返回值的值时，不影响catch中return的返回值结果）：
 * 5. 即上面第4点的特殊点：当catch中有return字句，而finally中没有return字句，不过finally中有改变catch中return的返回值时（注意：当返回值类型为list,map,数组时，在finally中修改返回值的值时，会影响catch中return的返回值结果）：
 */

public class ExceptionTest {
    public static void main(String[] args) throws Exception {
        try{
            throw new Exception("异常1");
        }catch (Exception e){
            throw new Exception("异常2");
        }finally {
            try {
                throw new Exception("异常3");
            } catch (Exception e) {
                // finally 中的异常信息覆盖了 try 中的异常信息，一个方法无法抛出两个异常，只会打印异常 4
                throw new Exception("异常4");
            }
        }
    }
}
