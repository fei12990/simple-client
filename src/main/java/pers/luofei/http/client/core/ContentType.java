package pers.luofei.http.client.core;

/**
 * Created by luofei on 2017/9/1.
 */
public enum ContentType {

    FORM_URLENCODED("application/x-www-form-urlencoded"),

    FORM_DATA("multipart/form-data"),

    JSON("application/json"),

    XML("text/xml"),

    BINARY("application/x-www-form-urlencoded"),

    OTHER("");

    public static final String KEY = "Content-Type";

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public void setValue(String value) {

        if (this == OTHER) {
            this.value = value;
        }
    }

    public String getValue() {
        return value;
    }
}
