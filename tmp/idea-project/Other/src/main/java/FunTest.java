import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.AlterTableOptions;
import org.apache.kudu.client.PartialRow;

import java.util.ArrayList;
import java.util.List;

public class FunTest {
    public static void main(String[] args) {
        List<ColumnSchema> columns = new ArrayList<>();
        columns.add(new ColumnSchema.ColumnSchemaBuilder("id", Type.STRING).build());
        Schema u = new Schema(columns);
//        u.newPartialRow().addBinary();
        AlterTableOptions o = new AlterTableOptions().addRangePartition(new PartialRow(u),new PartialRow(u));
    }
}
