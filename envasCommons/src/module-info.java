module com.neopsis.envas.commons {

    requires transitive niagara.baja;
    requires com.alibaba.fastjson2;
    requires java.sql;
    requires niagara.testng;

    exports com.neopsis.envas.commons.license;
    exports com.neopsis.envas.commons.util;
}
