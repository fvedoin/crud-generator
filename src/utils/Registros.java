package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import conexao.DBConnection;

public abstract class Registros<T> {
    private String sqlInsercao;
    private String sqlAlteracao;
    private String sqlExclusao;
    private String sqlBuscaChavePrimaria;
    private String sqlBuscaTodos;

    public String getSqlInsercao() {
        return sqlInsercao;
    }

    public void setSqlInsercao(String sqlInsercao) {
        this.sqlInsercao = sqlInsercao;
    }

    public String getSqlAlteracao() {
        return sqlAlteracao;
    }

    public void setSqlAlteracao(String sqlAlteracao) {
        this.sqlAlteracao = sqlAlteracao;
    }

    public String getSqlExclusao() {
        return sqlExclusao;
    }

    public void setSqlExclusao(String sqlExclusao) {
        this.sqlExclusao = sqlExclusao;
    }

    public String getSqlBusca() {
        return sqlBuscaChavePrimaria;
    }

    public void setSqlBuscaChavePrimaria(String sqlBusca) {
        this.sqlBuscaChavePrimaria = sqlBusca;
    }

    public String getSqlBuscaTodos() {
        return sqlBuscaTodos;
    }

    public void setSqlBuscaTodos(String sqlBuscaTodos) {
        this.sqlBuscaTodos = sqlBuscaTodos;
    }

    public Connection abrir() {
        Connection c = DBConnection.getConnection();
        return c;
    }

    public void inserir(T t) {
        Connection conexao = abrir();
        try {
            PreparedStatement ps = conexao.prepareStatement(getSqlInsercao());
            preencherInsercao(ps, t);
            ps.execute();
            ps.close();
            conexao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void alterar(T t) {
        Connection conexao = abrir();
        try {
            PreparedStatement ps = conexao.prepareStatement(getSqlAlteracao());
            preencherAlteracao(ps, t);
            ps.execute();
            ps.close();
            conexao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(T t) {
        Connection conexao = abrir();
        try {
            PreparedStatement ps = conexao.prepareStatement(getSqlExclusao());
            preencherExclusao(ps, t);
            ps.execute();
            ps.close();
            conexao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public T buscar(T t) {
        Connection conexao = abrir();
        T temp = null;
        try {
            PreparedStatement ps = conexao.prepareStatement(getSqlBusca());
            preencherExclusao(ps, t);
            ResultSet rs = ps.executeQuery();
            temp = preencher(rs);
            rs.close();
            ps.close();
            conexao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public Collection<T> buscarTodos() throws SQLException {
        Connection conexao = abrir();
        Statement s = conexao.createStatement();
        ResultSet rs = s.executeQuery(getSqlBuscaTodos());
        Collection<T> registros = preencherColecao(rs);
        rs.close();
        conexao.close();
        return registros;
    }

    protected abstract void preencherInsercao(PreparedStatement ps, T t) throws SQLException;

    protected abstract void preencherAlteracao(PreparedStatement ps, T t) throws SQLException;

    protected abstract void preencherExclusao(PreparedStatement ps, T t) throws SQLException;

    protected abstract T preencher(ResultSet rs) throws SQLException;

    protected abstract Collection<T> preencherColecao(ResultSet rs) throws SQLException;

}