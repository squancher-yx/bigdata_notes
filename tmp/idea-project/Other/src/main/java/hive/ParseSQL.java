package hive;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

import java.util.*;

public class ParseSQL {
    public static void main(String[] args) throws ParseException {
        ParseDriver pd = new ParseDriver();
        ASTNode ast = pd.parse("SELECT A.C as CC,T FROM ( SELECT * FROM B ) AS A");
//        System.out.println(ast.getChild(0).getChild(0));
//        System.out.println(ast.getChild(0).getChild(1));
//        parseChild(ast);

        parse(ast);
    }


    public static void parseChild(ASTNode ast, Stack<ASTNode> stack) {
//        parseCurrent(ast);
        int numCh = ast.getChildCount();
        System.out.println(numCh);
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                System.out.print(child + " ");
            }
            System.out.println();
        }
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
                parseChild(child, stack);
            }
        }

    }

    public static void parse(ASTNode ast) {
        JSONObject object = new JSONObject();
        LinkedList<ASTNode> astList = new LinkedList<>();
        LinkedList<JSONObject> jsonList = new LinkedList<>();
        astList.push(ast);
        jsonList.push(object);
        while (!astList.isEmpty()) {
            ASTNode tmpAST = astList.getFirst();
            JSONObject tmpJson = jsonList.getFirst();
            String name = tmpAST.getText();
            int numCh = tmpAST.getChildCount();
            tmpJson.put("name",name);
            if (numCh > 0) {
//                JSONObject children = new JSONObject();
                ArrayList<JSONObject> children = new ArrayList<>();
                for (int num = 0; num < numCh; num++) {
                    ASTNode child = (ASTNode) tmpAST.getChild(num);
                    astList.addLast(child);
                    JSONObject tmpChildren = new JSONObject();
                    tmpChildren.put("name",child.getText());
                    children.add(tmpChildren);
                    jsonList.addLast(tmpChildren);
//                    System.out.print(child + " ");
                }
                tmpJson.put("children",children);
//                System.out.println();
            }
            astList.removeFirst();
            jsonList.removeFirst();
        }
        System.out.println(object.toJSONString());
    }
}
