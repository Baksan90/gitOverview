package com.agdev;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class Lesson9 {

    private static Connection con;
    private static Statement st;



    public static void main(String[] args) throws URISyntaxException, IOException, IllegalAccessException {

        try {
connection();
createTable();
createInstance(new Cat("qwerty", 10, 12.2));


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            disconnect();
        }

    }

    private static void createInstance(Cat qwerty) throws SQLException, IOException, URISyntaxException, IllegalAccessException {
        st = con.createStatement();
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        List<Class> classes = getClasses();

        String clasName;
        for(Class cls: classes) {
            if (cls.isAnnotationPresent(MyAnTable.class) & cls == qwerty.getClass()) {

                clasName = cls.getSimpleName();
                sb.append(clasName);
                sb.append(" (");


                Field[] fields = cls.getDeclaredFields();
                StringBuilder sb1 = new StringBuilder(" VALUES (");
                for(Field field : fields){
                    if(field.isAnnotationPresent(Column.class)){
                        sb.append(String.format(" %s,", field.getName()));
                        field.setAccessible(true);
                        sb1.append(String.format(field.getType() == String.class ? "'%s',": "%s,", field.get(qwerty)));
                    }
                }
                if(fields.length > 0){
                    sb.delete(sb.length() - 1, sb.length());
                    sb.append(" )");
                    sb1.delete(sb1.length() - 1, sb1.length());
                    sb1.append(" );");
                    sb.append(sb1.toString());
                    int rs = st.executeUpdate(sb.toString());
                }

            }
        }
    }

    private static void createTable() throws SQLException, URISyntaxException, IOException {
        st = con.createStatement();
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
           List<Class> classes = getClasses();

        String clasName;
        for(Class cls: classes) {
            if (cls.isAnnotationPresent(MyAnTable.class)) {

                clasName = cls.getSimpleName();
                Field[] fields = cls.getDeclaredFields();
                sb.append(clasName);
                sb.append(" (");
                for(Field field : fields){
                    if(field.isAnnotationPresent(Column.class)){
                      sb.append(String.format(" %s %s,", field.getName(), field.getType().getSimpleName()));
                    }
                }
                if(fields.length > 0){
                    sb.delete(sb.length() - 1, sb.length());
                  sb.append(" );");
                    int rs = st.executeUpdate(sb.toString());
                }

            }
        }
    }

    private static List<Class> getClasses() throws IOException, URISyntaxException {
        String packageName = Lesson9.class.getPackage().getName();
        String path = packageName.replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        Iterable<URL> urls = resources::asIterator;
        List<File> dirs = new ArrayList<>();

        for (URL url: urls) {
            dirs.add(new File(url.toURI().getPath()));
        }

        List<Class> classes = dirs.stream().flatMap((File d) -> findClasses(d, packageName).stream()).collect(Collectors.toList());
        return classes;
    }


    private static List<Class> findClasses(File directory, String packageName) {
        if (!directory.exists()) {
            return Collections.emptyList();
        }

        List<Class> classes = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                    //Skip, as we are searching for the classes
                }
            }
        }
        return classes;
    }

    private static void connection() throws SQLException {
        try{
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:test.db");}
        catch(ClassNotFoundException | SQLException e){
            throw new SQLException("Unable to DB");
        }

    }

    private static  void disconnect(){
        try{
            st.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        try{
            con.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

}
