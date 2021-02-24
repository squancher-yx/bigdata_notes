package classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

public class ClassLoaderTest {
    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InterruptedException, NoSuchMethodException, InvocationTargetException {
//        sun.misc.Launcher
//                ClassLoader
        while (true) {
            Scanner sc = new Scanner(System.in);
            String str = sc.next();

                    String path = "file:\\D:\\idea-project\\java-test\\out\\artifacts\\java_test_jar\\java-test.jar";
                    Class<?> clazz = null;
                    try {
                        URLClassLoader loader = new URLClassLoader(new URL[]{new URL(path)});
                        clazz = loader.loadClass("com.yx.classloader.TestFun");
                        TestFun foo = (TestFun)clazz.newInstance();
                        foo.Fun1();

                        Method m = clazz.getMethod(str);
//                        loader.close();
//
//                        m.invoke(foo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//        TestFun test = (TestFun) clazz.newInstance();
//        test.Fun1();

        }
    }
}
