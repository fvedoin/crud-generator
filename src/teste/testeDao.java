package teste;

import java.sql.Connection;
import conexao.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class testeDao {
    public static void insereteste(teste objeto) {
        String sql = "INSERT INTO teste(codigo,nome) VALUES(?,?)";

        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBConnection.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setInt(1, objeto.getcodigo());
            pstm.setString(2, objeto.getnome());
            pstm.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void editateste(Integer codigo, String nome) {
        String sql = "UPDATE teste SET nome = ? WHERE codigo = ?";
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBConnection.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, nome);
            pstm.setInt(2, codigo);
            pstm.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeteste(Integer codigo) {
        String sql = "DELETE FROM teste WHERE codigo = " + codigo;
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = DBConnection.getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.execute();
            pstm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<teste> buscateste() {
        String sql = "SELECT * FROM teste";
        List<teste> results = new ArrayList<teste>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rset = null;
        try {
            conn = DBConnection.getConnection();
            pstm = conn.prepareStatement(sql);
            rset = pstm.executeQuery();
            while (rset.next()) {
                teste result = new teste(rset.getInt("codigo"), rset.getString("nome"));
                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
