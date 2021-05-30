package utils;

public class Utils {
    public String firstLetterUppercase(String str) {
        String output = str.substring(0, 1).toUpperCase() + str.substring(1);

        return output;
    }

    public String tipoAtributo(String tipo) {
        tipo = tipo.toLowerCase();
        if (tipo.equals("serial") || tipo.equals("integer") || tipo.equals("numeric") || tipo.equals("double precision") || tipo.equals("real") || tipo.equals("int") || tipo.equals("bigint") || tipo.equals("bigserial")) {
            tipo = "Integer";
        } else if (tipo.equals("varchar") || tipo.equals("nchar") || tipo.equals("char") || tipo.equals("character")) {
            tipo = "String";
        } else if (tipo.equals("bit") || tipo.equals("BOOLEAN")) {
            tipo = "Boolean";
        } else if (tipo.equals("Time")) {
            tipo = "java.sql.Time";
        } else if (tipo.equals("TIMESTAMP")) {
            tipo = "java.sql.Timestamp";
        } else if (tipo.equals("float")) {
            tipo = "Float";
        } else if (tipo.equals("ARRAY")) {
            tipo = "ArrayList<E>";
        }
        return tipo;
    }
}
