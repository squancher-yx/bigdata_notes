package hive;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class FunTest {
    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        JSONObject child = new JSONObject();
        ArrayList<String> list = new ArrayList<>();
        list.add("dfgdfh");
        child.put("sdf",list);
        object.put("123",child);
        System.out.println(object.toJSONString());
    }
}
