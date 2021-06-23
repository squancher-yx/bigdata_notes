package hive;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.tools.LineageInfo;
import org.apache.hadoop.hive.ql.parse.ParseException;

import java.util.*;

public class ParseSQL {
    public static void main(String[] args) throws ParseException {
        ParseDriver pd = new ParseDriver();
        ASTNode ast = pd.parse("select\n" +
                "    count(distinct uid) as device_brand_device_device_model_login_platform_new_uid_dcnt_1day,\n" +
                "    nvl(day_first_device_brand, 'all') as day_first_device_brand,\n" +
                "    nvl(day_first_device_model, 'all') as day_first_device_model,\n" +
                "    nvl(day_first_login_platform, 'all') as day_first_login_platform,\n" +
                "    nvl(day_first_device, 'all') as day_first_device,\n" +
                "    gate as gaga\n" +
                "from\n" +
                "    (\n" +
                "        select\n" +
                "            nvl(dt1.day_first_device, 'unknown') as day_first_device,\n" +
                "            nvl(dt1.day_first_device_brand, 'unknown') as day_first_device_brand,\n" +
                "            nvl(dt1.day_first_login_platform, 'unknown') as day_first_login_platform,\n" +
                "            nvl(dt1.day_first_device_model, 'unknown') as day_first_device_model,\n" +
                "            ft.uid as uid,\n" +
                "            ft.gate as gate\n" +
                "        from\n" +
                "            (\n" +
                "                select\n" +
                "                    distinct uid,\n" +
                "                    gate\n" +
                "                from\n" +
                "                    db_datawarehouse_game.dwd_game_new_user_incr_day\n" +
                "                where\n" +
                "                    pdate = 123\n" +
                "            ) as ft\n" +
                "            left join (\n" +
                "                select\n" +
                "                    day_first_device,\n" +
                "                    day_first_device_model,\n" +
                "                    day_first_login_platform,\n" +
                "                    day_first_device_brand,\n" +
                "                    uid,\n" +
                "                    gate\n" +
                "                from\n" +
                "                    db_datawarehouse_game.dim_firstlogin_lastlogout_user_info_incr_day\n" +
                "                where\n" +
                "                    pdate = 123\n" +
                "            ) as dt1 on ft.uid = dt1.uid\n" +
                "            and ft.gate = dt1.gate\n" +
                "    ) rt\n" +
                "group by\n" +
                "    day_first_device_brand,\n" +
                "    day_first_device_model,\n" +
                "    day_first_login_platform,\n" +
                "    day_first_device,\n" +
                "    gate grouping sets(\n" +
                "        (\n" +
                "            gate,\n" +
                "            day_first_device_brand,\n" +
                "            day_first_device,\n" +
                "            day_first_device_model\n" +
                "        ),\n" +
                "        (\n" +
                "            day_first_login_platform,\n" +
                "            gate,\n" +
                "            day_first_device_brand,\n" +
                "            day_first_device,\n" +
                "            day_first_device_model\n" +
                "        )\n" +
                "    )");
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
