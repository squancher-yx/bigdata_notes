package hive;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;

public class ParseSQL {
    public static void main(String[] args) throws ParseException {
        ParseDriver pd = new ParseDriver();
        ASTNode ast = pd.parse("SELECT Q.C,T FROM ( SELECT * FROM B ) AS A");
        parseChild(ast);
    }


    public static void parseChild(ASTNode ast){
        parseCurrent(ast);
        int numCh = ast.getChildCount();
        if (numCh > 0) {
            for (int num = 0; num < numCh; num++) {
                ASTNode child = (ASTNode) ast.getChild(num);
//                System.out.print(child.getText()+"    ");
                parseChild(child);
            }
            System.out.println();
        }

    }
    public static void parseCurrent(ASTNode ast){
        System.out.println(ast.getText());
    }
}
