package com.yx.flink;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.apache.flink.annotation.PublicEvolving;
import static com.yx.flink.testclass.$;
import org.apache.flink.table.api.ApiExpression;

import static org.apache.flink.table.expressions.ApiExpressionUtils.unresolvedRef;

public class FunTest {
    public static void main(String[] args) throws ParseException {
        String a = "123";
        System.out.println(a.hashCode());

    }
}


class testclass {
    public static int $(String name) {
        return 1;
    }
}