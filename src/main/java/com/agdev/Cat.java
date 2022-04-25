package com.agdev;

@MyAnTable
public class Cat {

    @Column
    private String name;
    @Column
    public int age;
    public double weight;

    public Cat(String name, int age, double weight) {
        this.name = name;
        this.age = age;
        this.weight = weight;
    }
}
