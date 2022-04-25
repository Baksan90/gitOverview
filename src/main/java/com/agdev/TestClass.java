package com.agdev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TestClass {

    public static class TestClass1 {
        public static void print(List<? super String> list) {
            list.add("Hello World!");
            System.out.println(list.get(0));
        }
    }

    public static void main(String []args) {
        List<String> list = new ArrayList<>();
        TestClass1.print(list);
    }
}
