package Others;

import junit.framework.Assert;

public class StringAppend {
    private String data;

    public String getData() {
        return data;
    }

    public StringAppend() {
    }

    public StringAppend(String data) {
        this.data = data;
    }

    public String append(StringAppend a, StringAppend b) {
        return this.getData() + a.getData() + b.getData();
    }

    public static String doAppend(String[] arr, Append append) {
        String result = "";
        for (int i = 0; i < arr.length; i++) {
            if (i+2 <= arr.length-1) {
                result += append.append(new StringAppend(arr[i]), new StringAppend(arr[i+1]), new StringAppend(arr[i+2]));
                i += 2;
            } else {
                result += append.append(new StringAppend(arr[i]), new StringAppend(""), new StringAppend(""));
            }

        }

        return result;
    }
    public void test5() {
        String[] stringArray = { "Barbara", "James", "Mary", "John", "Mike" };
        String str1 = StringAppend.doAppend(stringArray, (t1,t2,t3) -> t1.getData()+t2.getData()+t3.getData());
        String str2 = StringAppend.doAppend(stringArray, StringAppend::append);
        Assert.assertTrue(str1.equals(str2));
        System.out.println(str1);
    }
}
@FunctionalInterface
interface Append {
    String append(StringAppend a, StringAppend b, StringAppend c);
}