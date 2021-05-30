import conexao.DBConnection;
import utils.Utils;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

/**
 * @author Andres.Cespedes
 * @version 1.0 $Date: 24/01/2015
 * @since 1.7
 */
public class Metadata {

    static Connection connection = null;
    static DatabaseMetaData metadata = null;

    // Static block for initialization
    static {
        connection = DBConnection.getConnection();

        try {
            metadata = connection.getMetaData();
        } catch (SQLException e) {
            System.err.println("There was an error getting the metadata: "
                    + e.getMessage());
        }
    }

    /**
     * Prints in the console the general metadata.
     *
     * @throws SQLException
     */
    public static void printGeneralMetadata() throws SQLException {
        System.out.println("Database Product Name: "
                + metadata.getDatabaseProductName());
        System.out.println("Database Product Version: "
                + metadata.getDatabaseProductVersion());
        System.out.println("Logged User: " + metadata.getUserName());
        System.out.println("JDBC Driver: " + metadata.getDriverName());
        System.out.println("Driver Version: " + metadata.getDriverVersion());
        System.out.println("\n");
    }

    /**
     * @return Arraylist with the table's name
     * @throws SQLException
     */
    public static ArrayList getTablesMetadata() throws SQLException {
        String table[] = {"TABLE"};
        ResultSet rs = null;
        ArrayList tables = null;
        // receive the Type of the object in a String array.
        rs = metadata.getTables("trabalho01", null, null, table);
        tables = new ArrayList();
        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME"));
        }
        return tables;
    }

    public static String geraModel(Object actualTable) throws SQLException {
        ResultSet rs = metadata.getColumns("trabalho01", null, actualTable.toString(), null);
        System.out.println("Criando model para a tabela " + actualTable.toString().toUpperCase());
        try {
            String corpo = "";
            String parametros = "";
            String parametroJc = "";
            File file = new File(System.getProperty("user.dir") + "/src/" + actualTable.toString() + "/" + actualTable.toString() + ".java");
            file.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("package " + actualTable.toString() + ";\n");
            writer.println("public class " + actualTable.toString() + " {");
            Utils utils = new Utils();
            while (rs.next()) {
                String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));

                writer.println("private " + tipo + " " + rs.getString("COLUMN_NAME") + ";");
                //rs.getString("COLUMN_SIZE"));
                writer.println("public " + tipo + " get" + rs.getString("COLUMN_NAME") + "(){ return " + rs.getString("COLUMN_NAME") + "; }");
                writer.println("public void set" + rs.getString("COLUMN_NAME") + "(" + tipo + " " + rs.getString("COLUMN_NAME") + "){ this." + rs.getString("COLUMN_NAME") + " = " + rs.getString("COLUMN_NAME") + "; }");
                if (rs.isLast()) {
                    parametroJc += rs.getString("COLUMN_NAME");
                    parametros += (tipo + " " + rs.getString("COLUMN_NAME"));
                } else {
                    parametros += (tipo + " " + rs.getString("COLUMN_NAME") + ",");
                    parametroJc += (rs.getString("COLUMN_NAME") + ",");
                }
                corpo += ("this." + rs.getString("COLUMN_NAME") + " = " + rs.getString("COLUMN_NAME") + ";");
            }
            writer.println("public " + actualTable.toString() + "(" + parametros + "){");
            writer.println(corpo);
            writer.println("}");
            writer.println("}");

            writer.close();
            return parametroJc;
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("\n");
        return "erro";
    }

    public static void geraExemplo(Object actualTable, String parametros) throws SQLException {
        ResultSet rs = metadata.getColumns("trabalho01", null, actualTable.toString(), null);
        System.out.println("Criando exemplo para a tabela " + actualTable.toString().toUpperCase());
        try {
            File file = new File(System.getProperty("user.dir") + "/src/" + actualTable.toString() + "/" + actualTable.toString() + "Exemplo.java");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("package " + actualTable.toString() + ";\n");
            writer.println("public class " + actualTable.toString() + "Exemplo {\n" +
                    "public static void main(String[] args){\n" +
                    actualTable.toString() + "Dao cs = new " + actualTable + "Dao();\n" +
                    "System.out.println(\"Listando todos os dados da tabela " + actualTable.toString() + "\");\n" +
                    "for(" + actualTable.toString() + " temp : cs.busca" + actualTable + "()){");
            String imprime = "System.out.println(";
            parametros.replaceAll(" ", "");
            String[] campos = parametros.split(",");
            for (Integer i = 0; i < campos.length; i++) {
                imprime += "\"- \" + temp.get" + campos[i] + "()";
                if (i != campos.length - 1) {
                    imprime += "+";
                }
            }
            imprime += ");";
            writer.println(imprime);
            writer.println("}");
            //INSERINDO REGISTROS
            writer.println("System.out.println(\"Inserindo dados na tabela " + actualTable.toString() + "\");\n" +
                    actualTable.toString() + " c1 = new " + actualTable.toString() + "();\n" +
                    "cs.insere" + actualTable + "(c1);");
            writer.println("System.out.println(\"Listando todos os dados da tabela " + actualTable.toString() + "\");\n" +
                    "for(" + actualTable.toString() + " temp : cs.busca" + actualTable + "()){\n" +
                    imprime + "\n" +
                    "}");

            writer.println("System.out.println(\"Alterando o cliente 1\");\n" +
                    "cs.edita" + actualTable.toString() + "();");
            writer.println("System.out.println(\"Listando todos os dados da tabela " + actualTable.toString() + "\");\n" +
                    "for(" + actualTable.toString() + " temp : cs.busca" + actualTable + "()){\n" +
                    imprime + "\n" +
                    "}");

            //APAGANDO REGISTROS
            writer.println("System.out.println(\"Apagando todos os registros da tabela " + actualTable + "\");\n" +
                    "for(" + actualTable.toString() + " temp : cs.busca" + actualTable + "()){\n" +
                    "cs.remove" + actualTable.toString() + "(temp.get" + campos[0] + "());");
            writer.println("}");
            writer.println("System.out.println(\"Listando todos os dados da tabela " + actualTable.toString() + "\");\n" +
                    "for(" + actualTable.toString() + " temp : cs.busca" + actualTable + "()){\n" +
                    imprime + "\n" +
                    "}");
            writer.println("}\n" +
                    "}");

            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("\n");
    }

    public static void criar(PrintWriter writer, Object actualTable, ResultSet rs, String parametros) throws SQLException {
        String settingValues = "";
        writer.println("public static void insere" + actualTable.toString() + "(" + actualTable.toString() + " objeto){");
        String sql = "String sql = \"INSERT INTO " + actualTable.toString() + "(" + parametros + ")" +
                " VALUES(";
        Integer i = 0, j;
        parametros.replaceAll(" ", "");
        String[] campos = parametros.split(",");
        Utils utils = new Utils();
        while (rs.next()) {
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            j = i + 1;
            if(tipo.equals("Integer"))
                tipo = "Int";
            settingValues += "pstm.set" + tipo + "(" + j + ",objeto.get" + campos[i] + "());\n";
            if(rs.isLast())
                sql += "?)\";\n";
            else
                sql += "?,";
            i++;
        }
        writer.println(sql);
        writer.println("DBConnection conn = null;\n" +
                "PreparedStatement pstm = null;\n" +
                "try {\n" +
                "conn = (DBConnection) DBConnection.getConnection();\n" +
                "pstm = conn.prepareStatement(sql);\n" +
                settingValues +
                "pstm.execute();\n" +
                "}catch (Exception e) {\n" +
                "e.printStackTrace();\n" +
                "}finally{\n" +
                "try{\n" +
                "if(pstm != null){\n" +
                "pstm.close();\n" +
                "}\n" +
                "if(conn != null){\n" +
                "conn.close();\n" +
                "}\n" +
                "}catch(Exception e){\n" +
                "e.printStackTrace();\n" +
                "}");
        writer.println("}");
        writer.println("}");
    }

    public static void remover(PrintWriter writer, Object actualTable, String coluna) throws SQLException {
        writer.println("public static void remove" + actualTable.toString() + "(" + coluna + "){");
        String sql = "String sql = \"DELETE FROM " + actualTable.toString() + " WHERE " + coluna + " = \"+" + coluna + "+\";";
        writer.println(sql);
        writer.println("Connection conn = null;\n" +
                "PreparedStatement pstm = null;\n" +
                "try {\n" +
                "conn = Conexao.getConnection();\n" +
                "pstm = conn.prepareStatement(sql);\n" +
                "pstm.execute();\n" +
                "} catch (Exception e) {\n" +
                "e.printStackTrace();\n" +
                "}finally{\n" +
                "e.printStackTrace();\n" +
                "try{\n" +
                "if(pstm != null){\n" +
                "pstm.close();\n" +
                "}\n" +
                "if(conn != null){\n" +
                "conn.close();\n" +
                "}\n" +
                "}catch(Exception e){\n" +
                "e.printStackTrace();\n" +
                "}\n" +
                "}\n" +
                "}");
    }

    public static void editar(PrintWriter writer, Object actualTable, String parametros, String coluna) throws SQLException {
        Utils utils = new Utils();
        ResultSet rs = metadata.getColumns("trabalho01", null, actualTable.toString(), null);
        String settingValues = "";
        writer.print("public static void edita" + actualTable.toString() + "(");
        String sql = "String sql = \"UPDATE " + parametros.substring(coluna.length() + 1) + " SET ";
        //NOME DA COLUNA PRIMARY KEY
        DatabaseMetaData dmd = connection.getMetaData();
        ResultSet primaryKeySet = dmd.getPrimaryKeys(null, null, actualTable.toString());
        String primaryKey = "";
        while (primaryKeySet.next()) {
            primaryKey = primaryKeySet.getString("COLUMN_NAME");
        }
        int i = 1;
        String columnName;
        String columnType;
        while (rs.next()) {
            columnType = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            columnName = rs.getString("COLUMN_NAME");

            //CONCATENA PARAMETROS NO METODO DA CLASSE
            writer.print(columnType + " " + columnName + "");
            if (!rs.isLast())
                writer.print(",");

            //CONCATENA PARAMETROS NA QUERY SQL
            if(!columnName.equals(primaryKey)){
                sql += columnName + " = \"+" + columnName + "+\"";
                if (!rs.isLast())
                    sql += ",";
            }

            //CONCATENA PARAMETROS NO PREPARED STATEMENT
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            if(tipo.equals("Integer"))
                tipo = "Int";
            settingValues += "pstm.set" + tipo + "(" + i + "," + columnName + ");\n";
            i++;
        }
        writer.println("){");
        sql += " WHERE " + primaryKey + " = \"+" + primaryKey + ";";
        writer.println(sql);
        writer.println("DBConnection conn = null;\n" +
                "PreparedStatement pstm = null;\n" +
                "try {\n" +
                "conn = (DBConnection) DBConnection.getConnection();\n" +
                "pstm = conn.prepareStatement(sql);\n" +
                settingValues +
                "pstm.execute();\n" +
                "}catch (Exception e) {\n" +
                "e.printStackTrace();\n" +
                "}finally{\n" +
                "try{\n" +
                "if(pstm != null){\n" +
                "pstm.close();\n" +
                "}\n" +
                "if(conn != null){\n" +
                "conn.close();\n" +
                "}\n" +
                "}catch(Exception e){\n" +
                "e.printStackTrace();\n" +
                "}");
        writer.println("}");
        writer.println("}");
    }

    public static void ler(PrintWriter writer, Object actualTable, String parametros) throws SQLException {
        writer.println("public static List<" + actualTable.toString() + "> busca" + actualTable.toString() + "(){");
        writer.println(" String sql = \"SELECT * FROM " + actualTable.toString() + "\";\n" +
                "List<" + actualTable.toString() + "> results = new ArrayList<" + actualTable.toString() + ">();\n" +
                "Connection conn = null;\n" +
                "PreparedStatement pstm = null;\n" +
                "ResultSet rset = null;");
        writer.println(" try {\n" +
                "conn = Conexao.getConnection();\n" +
                "pstm = conn.prepareStatement(sql);\n" +
                "rset = pstm.executeQuery();\n" +
                "while(rset.next()){\n" +
                actualTable.toString() + " result = new " + actualTable.toString() + "();");
        parametros.replaceAll(" ", "");
        String[] campos = parametros.split(",");
        for (Integer i = 0; i < campos.length; i++) {
            writer.println("result.set" + campos[i] + "(rset.getString(\"" + campos[i] + "\"))");
        }

        writer.println("results.add(result)\n" +
                "}\n" +
                "} catch (Exception e) {\n" +
                "e.printStackTrace();\n" +
                "}finally{\n" +
                "try{\n" +
                "if(rset != null){\n" +
                "rset.close();\n" +
                "}\n" +
                "if(pstm != null){\n" +
                "pstm.close();\n" +
                "}\n" +
                "if(conn != null){\n" +
                "conn.close();\n" +
                "}\n" +
                "}catch(Exception e){\n" +
                "e.printStackTrace();\n" +
                "}\n" +
                "}\n" +
                "return results;\n" +
                "}");
    }

    public static void geraDao(Object actualTable, String parametros) throws SQLException {
        ResultSet rs = metadata.getColumns("trabalho01", null, actualTable.toString(), null);
        System.out.println("Criando Dao para a tabela " + actualTable.toString().toUpperCase());
        try {
            File file = new File(System.getProperty("user.dir") + "/src/" + actualTable.toString() + "/" + actualTable.toString() + "Dao.java");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("package " + actualTable.toString() + ";\n");
            writer.println("import conexao.DBConnection;\n");
            writer.println("import java.sql.PreparedStatement;\n");

            writer.println("public class " + actualTable.toString() + "Dao {");
            DatabaseMetaData dmd = connection.getMetaData();
            ResultSet primaryKeySet = dmd.getPrimaryKeys(null, null, actualTable.toString());
            String primaryKey = "";
            while (primaryKeySet.next()) {
                primaryKey = primaryKeySet.getString("COLUMN_NAME");
            }
            criar(writer, actualTable, rs, parametros);
            editar(writer, actualTable, parametros, primaryKey);
            remover(writer, actualTable, primaryKey);
            ler(writer, actualTable, parametros);
            writer.println("}");
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("\n");
    }

    public static void getColumnsMetadata(ArrayList tables) throws SQLException {
        // Print the columns properties of the actual table
        for (Object actualTable : tables) {
            String parametros = geraModel(actualTable);
            geraDao(actualTable, parametros);
            geraExemplo(actualTable, parametros);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            printGeneralMetadata();
            // Print all the tables of the database scheme, with their names and
            // structure
            ArrayList<String> list = getTablesMetadata();

            String listString = "";

            for (String s : list) {
                listString += s + "\t";
            }

            getColumnsMetadata(getTablesMetadata());
        } catch (SQLException e) {
            System.err
                    .println("There was an error retrieving the metadata properties: "
                            + e.getMessage());
        }
    }
}

