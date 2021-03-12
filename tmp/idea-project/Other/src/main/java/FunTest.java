import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.AlterTableOptions;
import org.apache.kudu.client.PartialRow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FunTest {
    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition1 = lock.newCondition();
        Lock lock2 = new ReentrantLock();
        Condition condition2 = lock2.newCondition();
        lock.lock();
        System.out.println("lock 1");
        lock2.lock();
        condition2.await();
        lock.unlock();
    }
}
