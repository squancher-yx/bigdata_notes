package arango;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.DocumentField.Type;

import java.util.ArrayList;
import java.util.Collection;

public class GraphTest {
    public static void main(String[] args) {
        String TEST_DB = "java_driver_graph_test_db";
        ArangoDatabase db;
        String GRAPH_NAME = "traversalGraph";
        String EDGE_COLLECTION_NAME = "edges";
        String VERTEXT_COLLECTION_NAME = "circles";
        ArangoDB arangoDB = new ArangoDB.Builder()
                .host("192.168.121.128", 8529)
                .user("root")
                .password("123321")
                .build();
//        if (arangoDB.db(TEST_DB).exists())
//            arangoDB.db(TEST_DB).drop();
//        arangoDB.createDatabase(TEST_DB);
        db = arangoDB.db(TEST_DB);

        final Collection<EdgeDefinition> edgeDefinitions = new ArrayList<>();

        //建图
        final EdgeDefinition edgeDefinition = new EdgeDefinition().collection(EDGE_COLLECTION_NAME)
                .from(VERTEXT_COLLECTION_NAME).to(VERTEXT_COLLECTION_NAME);
        edgeDefinitions.add(edgeDefinition);

        if (!db.graph(GRAPH_NAME).exists())
            db.createGraph(GRAPH_NAME, edgeDefinitions, null);

        //添加顶点
        Collection<String> data = new ArrayList<>();
        //库
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game\"," +
                "  \"database\":\"游戏\"" +
                "}");
        //表
        data.add("{" +
                "  \"_key\": \"ods_game_behavior_money_incr_day\"," +
                "  \"table\":\"商城购买日志\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"ods_game_active_online_role_incr_day\"," +
                "  \"table\":\"角色在线日志\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"ods_game_account_user_info_incr_day\"," +
                "  \"table\":\"用户信息日志\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"ods_game_active_login_incr_day\"," +
                "  \"table\":\"活跃日志\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"dwd_game_active_login_incr_day\"," +
                "  \"table\":\"活跃明细表\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"dim_login_user_info_detail_day\"," +
                "  \"table\":\"活跃明细表\"" +
                "}");

        data.add("{" +
                "  \"_key\": \"dim_firstlogin_lastlogout_role_info_incr_day\"," +
                "  \"table\":\"每日首登和最后登出角色表\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"dim_login_user_info_detail_incr_day\"," +
                "  \"table\":\"登录用户信息维度表\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"dws_game_lost_role_incr_day\"," +
                "  \"table\":\"连续流失角色表\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"dwd_game_new_role_incr_day\"," +
                "  \"table\":\"角色创建明细表\"" +
                "}");
        //维度表
        data.add("{" +
                "  \"_key\": \"role_level\"," +
                "  \"table\":\"角色最新等级\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"login_platform\"," +
                "  \"table\":\"帐号登录平台\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"app_version\"," +
                "  \"table\":\"游戏应用版本\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"logic_server\"," +
                "  \"table\":\"逻辑服务器\"" +
                "}");

        arangoDB.db(TEST_DB).collection("circles").insertDocuments(data);

        //添加边
        data = new ArrayList<>();

        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_ods_game_behavior_money_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/ods_game_behavior_money_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_ods_game_behavior_money_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/ods_game_behavior_money_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_ods_game_account_user_info_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/ods_game_account_user_info_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_ods_game_active_login_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/ods_game_active_login_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dwd_game_active_login_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dwd_game_active_login_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dim_login_user_info_detail_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dim_login_user_info_detail_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dim_firstlogin_lastlogout_role_info_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dim_firstlogin_lastlogout_role_info_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dim_login_user_info_detail_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dim_login_user_info_detail_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dws_game_lost_role_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dws_game_lost_role_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dws_game_lost_role_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dws_game_lost_role_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"db_datawarehouse_game_dwd_game_new_role_incr_day\"," +
                "  \"_from\": \"circles/db_datawarehouse_game\"," +
                "  \"_to\": \"circles/dwd_game_new_role_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");


        data.add("{" +
                "  \"_key\": \"role_level_dim_firstlogin_lastlogout_role_info_incr_day\"," +
                "  \"_from\": \"circles/role_level\"," +
                "  \"_to\": \"circles/dim_firstlogin_lastlogout_role_info_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"role_level_dwd_game_active_login_incr_day\"," +
                "  \"_from\": \"circles/role_level\"," +
                "  \"_to\": \"circles/dwd_game_active_login_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");

        data.add("{" +
                "  \"_key\": \"login_platform_dwd_game_active_login_incr_day\"," +
                "  \"_from\": \"circles/login_platform\"," +
                "  \"_to\": \"circles/dwd_game_active_login_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");

        data.add("{" +
                "  \"_key\": \"app_version_dim_login_user_info_detail_incr_day\"," +
                "  \"_from\": \"circles/app_version\"," +
                "  \"_to\": \"circles/dim_login_user_info_detail_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"app_version_dwd_game_active_login_incr_day\"," +
                "  \"_from\": \"circles/app_version\"," +
                "  \"_to\": \"circles/dwd_game_active_login_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");

        data.add("{" +
                "  \"_key\": \"logic_server_dws_game_lost_role_incr_day\"," +
                "  \"_from\": \"circles/logic_server\"," +
                "  \"_to\": \"circles/dws_game_lost_role_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"logic_server_dwd_game_active_login_incr_day\"," +
                "  \"_from\": \"circles/logic_server\"," +
                "  \"_to\": \"circles/dwd_game_active_login_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");
        data.add("{" +
                "  \"_key\": \"logic_server_\"," +
                "  \"_from\": \"circles/logic_server\"," +
                "  \"_to\": \"circles/dwd_game_new_role_incr_day\"," +
                "  \"relation\": \"in_use\"" +
                "}");


        arangoDB.db(TEST_DB).collection("edges").insertDocuments(data);

        arangoDB.shutdown();
    }

    //添加边
    public static void insertEdge(String key, String from, String to, String relation,ArangoDB arangoDB) {
        Collection<String> data = new ArrayList<>();
//        data.add("{" +
//                "  \"_key\": \"t3_t4\"," +
//                "  \"_from\": \"circles/t3\"," +
//                "  \"_to\": \"circles/t4\"," +
//                "  \"relation\": \"is_father\"" +
//                "}");
        data.add("{" +
                "  \"_key\": \"" + key + "\"," +
                "  \"_from\": \"" + from + "\"," +
                "  \"_to\": \"" + to + "\"," +
                //边属性
                "  \"relation\": \"" + relation + "\"" +
                "}");
    }

    //添加顶点
    public static void insertVertexct(String key, String tableName, String table,ArangoDB arangoDB) {
        Collection<String> data = new ArrayList<>();
//        data.add("{" +
//                "  \"_key\": \"t3\"," +
//                "  \"table\":\"dim_1\"" +
//                "}");
        data.add("{" +
                //key:每个文档的唯一标识符
                "  \"_key\": \"" + key + "\"," +
                //顶点属性
                "  \"" + tableName + "\":\"" + table + "\"" +
                "}");
    }

}
