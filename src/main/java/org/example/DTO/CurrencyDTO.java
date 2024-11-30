package org.example.DTO;

public class CurrencyDTO {

    private int id;
    private String code;
    private String name;
    private String sign;

    public CurrencyDTO() {

    }

    public CurrencyDTO(int id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public CurrencyDTO(String code, String name, String sign) {
        this.id = -1;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }



    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {

        return "{\n" +
                "       \"id\": " + id + ",\n" +
                "       \"name\": " + name + ",\n" +
                "       \"code\": " + code + ",\n" +
                "       \"sign\": " + sign + "\n" +
                "}";
    }
}
