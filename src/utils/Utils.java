package utils;

public class Utils {

    public String firstLetterUppercase(String str) {
        String output = str.substring(0, 1).toUpperCase() + str.substring(1);

        return output;
    }

    public String tipoAtributo(String tipo) {
        tipo = tipo.toLowerCase();
        if (tipo.equals("serial") || tipo.equals("integer") || tipo.equals("numeric") || tipo.equals("int") || tipo.equals("mediumint") || tipo.equals("bigserial")) {
            tipo = "Integer";
        } else if (tipo.equals("bigint")) {
            tipo = "Long";
        } else if (tipo.equals("varchar") || tipo.equals("nchar") || tipo.equals("char") || tipo.equals("character")) {
            tipo = "String";
        } else if (tipo.equals("bit") || tipo.equals("boolean")) {
            tipo = "Boolean";
        } else if (tipo.equals("decimal") || tipo.equals("real") || tipo.equals("double")) {
            tipo = "Float";
        } else if (tipo.equals("datetime")) {
            tipo = "Timestamp";
        }
        return firstLetterUppercase(tipo);
    }

    public String tipoAtributoModel(String tipo) {
        tipo = tipo.toLowerCase();
        if (tipo.equals("serial") || tipo.equals("integer") || tipo.equals("numeric") || tipo.equals("int") || tipo.equals("mediumint") || tipo.equals("bigserial")) {
            tipo = "Integer";
        } else if (tipo.equals("bigint")) {
            tipo = "Long";
        } else if (tipo.equals("varchar") || tipo.equals("nchar") || tipo.equals("char") || tipo.equals("character")) {
            tipo = "String";
        } else if (tipo.equals("bit") || tipo.equals("boolean")) {
            tipo = "Boolean";
        } else if (tipo.equals("date")) {
            tipo = "java.sql.Date";
        } else if (tipo.equals("time")) {
            tipo = "java.sql.Time";
        } else if (tipo.equals("timestamp") || tipo.equals("datetime")) {
            tipo = "java.sql.Timestamp";
        } else if (tipo.equals("float") || tipo.equals("decimal") || tipo.equals("real") || tipo.equals("double")) {
            tipo = "Float";
        } else if (tipo.equals("array")) {
            tipo = "ArrayList<E>";
        }
        return tipo;
    }
}
