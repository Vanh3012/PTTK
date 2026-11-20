package vn.ptit.qlst9;

import java.io.File;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

/** Chạy trực tiếp trong NetBeans hoặc bằng mvn compile exec:java. */
public class Main {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.getInteger("port", 8080));
        tomcat.setBaseDir("target/tomcat");
        tomcat.getConnector();
        var context = tomcat.addWebapp("", new File("src/main/webapp").getAbsolutePath());
        context.setParentClassLoader(Main.class.getClassLoader());
        WebResourceRoot resources = new StandardRoot(context);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                new File("target/classes").getAbsolutePath(), "/"));
        context.setResources(resources);
        tomcat.start();
        System.out.println("http://localhost:" + tomcat.getConnector().getPort());
        tomcat.getServer().await();
    }
}
