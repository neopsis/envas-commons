package com.neopsis.envas.test;

public class TestRunner {

    public static void main(String[] args) {

        Tests t = new Tests();

        t.generateKeyPair();
        t.licensorTest();
        t.licenseeTest();
    }
}

