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
        ArrayList<String> tables = null;
        // receive the Type of the object in a String array.
        rs = metadata.getTables("trabalho01", null, null, table);
        tables = new ArrayList<>();
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
            writer.println("public " + actualTable.toString() + "(){}");
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
        String nomeTabela = actualTable.toString();
        System.out.println("Criando exemplo para a tabela " + nomeTabela.toUpperCase());
        //NOME DA COLUNA PRIMARY KEY
        DatabaseMetaData dmd = connection.getMetaData();
        ResultSet primaryKeySet = dmd.getPrimaryKeys("trabalho01", null, nomeTabela);
        String primaryKey = "";
        while (primaryKeySet.next()) {
            primaryKey = primaryKeySet.getString("COLUMN_NAME");
        }
        try {
            File file = new File(System.getProperty("user.dir") + "/src/" + nomeTabela + "/" + nomeTabela + "Exemplo.java");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("package " + nomeTabela + ";\n");
            writer.println("import org.jeasy.random.EasyRandom;");
            writer.println("import org.objenesis.Objenesis;");
            writer.println("import com.fasterxml.jackson.core.*;");
            writer.println("import com.fasterxml.jackson.annotation.*;");
            writer.println("import com.fasterxml.jackson.databind.*;");
            writer.println("import java.sql.SQLException;");
            writer.println("public class " + nomeTabela + "Exemplo {\n" +
                    "public static void main(String[] args) throws ClassNotFoundException, SQLException {\n" +
                    nomeTabela + "Dao cs = new " + actualTable + "Dao();\n" +
                    "System.out.println(\"Listando todos os dados da tabela " + nomeTabela + "\");\n" +
                    "for(" + nomeTabela + " temp : cs.buscarTodos()){");
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

            writer.println("System.out.println(\"Excluindo todos os dados da tabela " + nomeTabela + "\");");
            writer.println("for(" + nomeTabela + " temp : cs.buscarTodos())");
            writer.println("cs.excluir(temp);");

            writer.println("System.out.println(\"Listando novamente todos os dados da tabela " + nomeTabela + "\");\n" +
                    "for(" + nomeTabela + " temp : cs.buscarTodos()){");
            writer.println(imprime + "}");

            //INSERINDO REGISTROS
            writer.println("EasyRandom easyRandom = new EasyRandom();");
            writer.println(nomeTabela + " c1 = easyRandom.nextObject(" + nomeTabela + ".class);");
            writer.println("System.out.println(\"Inserindo dados na tabela " + nomeTabela + "\");\n" +
                    "cs.inserir(c1);");
            writer.println(nomeTabela + " c2 = easyRandom.nextObject(" + nomeTabela + ".class);");
            writer.println("cs.inserir(c2);");

            //LISTANDO REGISTROS
            writer.println("System.out.println(\"Listando novamente todos os dados da tabela " + nomeTabela + "\");\n" +
                    "for(" + nomeTabela + " temp : cs.buscarTodos()){");
            writer.println(imprime + "}");

            writer.println(nomeTabela + " c3 = easyRandom.nextObject(" + nomeTabela + ".class);");
            writer.println("c3.set" + primaryKey + "(c1.get" + primaryKey + "());");
            writer.println("System.out.println(\"Alterando o registro com identificador \" + c1.get" + primaryKey + "());\n " +
                    "cs.alterar(c3);");

            //LISTANDO REGISTROS
            writer.println("System.out.println(\"Listando novamente todos os dados da tabela " + nomeTabela + "\");\n" +
                    "for(" + nomeTabela + " temp : cs.buscarTodos()){");
            writer.println(imprime + "}");

            writer.println("}\n" +
                    "}");

            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("\n");
    }

    public static String montaSqlInsercao(String nomeTabela, String parametros, ResultSet rs) throws SQLException {
        String sql = "";
        sql += "INSERT INTO " + nomeTabela + " (" + parametros + ") VALUES (";
        while (rs.next()) {
            if (rs.isLast())
                sql += "?";
            else
                sql += "?,";
        }
        sql += ")";
        return sql;
    }

    public static String montaSqlAlteracao(String nomeTabela, DatabaseMetaData metadata, String primaryKey) throws SQLException {
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);
        String sql = "UPDATE " + nomeTabela + " SET ";
        String columnName;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME");
            if (!columnName.equals(primaryKey)) {
                sql += "" + columnName + " = ?";
                if (!rs.isLast())
                    sql += ",";
            }
        }
        sql += " WHERE " + primaryKey + " = ?";
        return sql;
    }

    public static String montaSqlExclusao(String nomeTabela, String primaryKey) throws SQLException {

        String sql = "DELETE FROM " + nomeTabela + " WHERE " + primaryKey + " = ?";
        return sql;

    }

    public static String montaSqlBuscaChavePrimaria(String nomeTabela, String primaryKey) throws SQLException {

        String sql = "SELECT * FROM " + nomeTabela + " WHERE " + primaryKey + " = ?";
        return sql;

    }

    public static String montaSqlBuscaTodos(String nomeTabela) throws SQLException {

        String sql = "SELECT * FROM " + nomeTabela;
        return sql;

    }

    public static void preencheAlteracao(PrintWriter writer, Object actualTable, String primaryKey) throws SQLException {
        String nomeTabela = actualTable.toString();
        Utils utils = new Utils();
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);
        //NOME DA COLUNA PRIMARY KEY
        DatabaseMetaData dmd = connection.getMetaData();
        String primaryKeyType = "";
        String settingValues = "";

        int i = 1;
        String columnName;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME");

            //CONCATENA PARAMETROS NO PREPARED STATEMENT
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            if (tipo.equals("Integer"))
                tipo = "Int";
            if (!columnName.equals(primaryKey)) {
                settingValues += "ps.set" + tipo + "(" + i + ", c.get" + columnName + "());\n";
                i++;
            } else {
                primaryKeyType = tipo;
            }
        }
        writer.print("@Override"
                + " protected void preencherAlteracao(PreparedStatement ps, " + nomeTabela + " c) throws SQLException {"
                + settingValues
                + "ps.set" + primaryKeyType + "(" + i + ", c.get" + primaryKey + "());"
                + "}");
    }

    public static void preencheExclusao(PrintWriter writer, Object actualTable, String primaryKey) throws SQLException {
        String nomeTabela = actualTable.toString();
        Utils utils = new Utils();
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);

        String settingValues = "";

        String columnName;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME");

            //CONCATENA PARAMETROS NO PREPARED STATEMENT
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            if (tipo.equals("Integer"))
                tipo = "Int";
            if (columnName.equals(primaryKey)) {
                settingValues = "ps.set" + tipo + "(1, c.get" + columnName + "());\n";
            }
        }
        writer.print("@Override"
                + " protected void preencherExclusao(PreparedStatement ps, " + nomeTabela + " c) throws SQLException {"
                + settingValues
                + "}");
    }

     public static void preencheInsercao(PrintWriter writer, Object actualTable) throws SQLException {
        String nomeTabela = actualTable.toString();
        Utils utils = new Utils();
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);
        String settingValues = "";

        int i = 1;
        String columnName;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME");

            //CONCATENA PARAMETROS NO PREPARED STATEMENT
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            if (tipo.equals("Integer"))
                tipo = "Int";

            settingValues += "ps.set" + tipo + "(" + i + ", c.get" + columnName + "());\n";
            i++;

        }
        writer.print("@Override"
                + " protected void preencherInsercao(PreparedStatement ps, " + nomeTabela + " c) throws SQLException {"
                + settingValues
                + "}");
    }

    public static void preenche(PrintWriter writer, Object actualTable) throws SQLException {
        String nomeTabela = actualTable.toString();
        Utils utils = new Utils();
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);
        String settingValues = "";

        int i = 1;
        String columnName;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME");

            //CONCATENA PARAMETROS NO PREPARED STATEMENT
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            if (tipo.equals("Integer"))
                tipo = "Int";

            settingValues += "c.set" + columnName + "(rs.get" + tipo + "(\"" + columnName + "\"));\n";
            i++;

        }
        writer.print("@Override"
                + " protected " + nomeTabela + " preencher(ResultSet rs) throws SQLException {"
                + nomeTabela + " c = new " + nomeTabela + "();"
                + settingValues
                + "return c;"
                + "}");
    }

    public static void preencheColecao(PrintWriter writer, Object actualTable) throws SQLException {
        String nomeTabela = actualTable.toString();
        Utils utils = new Utils();
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);
        String settingValues = "";

        int i = 1;
        String columnName;
        while (rs.next()) {
            columnName = rs.getString("COLUMN_NAME");

            //CONCATENA PARAMETROS NO PREPARED STATEMENT
            String tipo = utils.tipoAtributo(rs.getString("TYPE_NAME"));
            if (tipo.equals("Integer"))
                tipo = "Int";

            settingValues += "temp.set" + columnName + "(rs.get" + tipo + "(\"" + columnName + "\"));\n";
            i++;

        }
        writer.print("@Override"
                + " protected Collection<" + nomeTabela + "> preencherColecao(ResultSet rs) throws SQLException {"
                + "Collection<" + nomeTabela + "> retorno = new ArrayList<" + nomeTabela + ">();"
                + "while (rs.next()){"
                + nomeTabela + " temp = new " + nomeTabela + "();"
                + settingValues
                + "retorno.add(temp);"
                + "}"
                + "return retorno;"
                + "}");
    }

    public static void geraDao(Object actualTable, String parametros) throws SQLException {
        String nomeTabela = actualTable.toString();
        ResultSet rs = metadata.getColumns("trabalho01", null, nomeTabela, null);
        System.out.println("Criando Dao para a tabela " + nomeTabela.toUpperCase());
        DatabaseMetaData dmd = connection.getMetaData();
        ResultSet primaryKeySet = dmd.getPrimaryKeys(null, null, nomeTabela);
        String primaryKey = "";
        while (primaryKeySet.next()) {
            primaryKey = primaryKeySet.getString("COLUMN_NAME");
        }
        try {
            File file = new File(System.getProperty("user.dir") + "/src/" + nomeTabela + "/" + nomeTabela + "Dao.java");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println("package " + nomeTabela + ";\n");
            writer.println("import java.sql.PreparedStatement;");
            writer.println("import java.sql.ResultSet;");
            writer.println("import java.sql.SQLException;");
            writer.println("import java.util.ArrayList;");
            writer.println("import java.util.Collection;");
            writer.println("import utils.Registros;\n");


            writer.println("public class " + nomeTabela + "Dao extends Registros<" + nomeTabela + ">{");
            writer.println("public " + nomeTabela + "Dao() {");
            writer.print("setSqlInsercao(\"" + montaSqlInsercao(nomeTabela, parametros, rs) + "\");");
            writer.println("setSqlAlteracao(\"" + montaSqlAlteracao(nomeTabela, metadata, primaryKey) + "\");");
            writer.println("setSqlExclusao(\"" + montaSqlExclusao(nomeTabela, primaryKey) + "\");");
            writer.println("setSqlBuscaChavePrimaria(\"" + montaSqlBuscaChavePrimaria(nomeTabela, primaryKey) + "\");");
            writer.println("setSqlBuscaTodos(\"" + montaSqlBuscaTodos(nomeTabela) + "\");");
            writer.println("}");


            preencheInsercao(writer, actualTable);
            preencheAlteracao(writer, actualTable, primaryKey);
            preencheExclusao(writer, actualTable, primaryKey);
            preenche(writer, actualTable);
            preencheColecao(writer, actualTable);
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

