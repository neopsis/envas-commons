package test;

public class TestRunner {

    public static void main(String[] args) {

        LicenseTest t = new LicenseTest();

        t.generateKeyPair();
        t.licensorTest();
        t.licenseeTest();
    }
}

