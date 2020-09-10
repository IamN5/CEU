package framework.core.db;

import framework.Logger;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInterface<T extends Model<T> > implements SimpleOperations<T> {
    protected String table;
    protected Connection connection;
    protected Class<T> clazz;

    public DatabaseInterface(Connection connection, Class<T> clazz) {
        this.connection = connection;
        this.clazz = clazz;
    }

    @Override
    public List<T> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", table);

        ResultSet rs = executeQuery(query);


        ArrayList<T> results = new ArrayList<>();
        while (rs.next()) {
            try {
                results.add(clazz.getDeclaredConstructor(ResultSet.class).newInstance(rs));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    @Override
    public T findBy(DatabaseColumn column) throws SQLException {
        String query = String.format("SELECT * FROM %s WHERE %s = '" + column.getKey() + "'", table, column.getName());

        ResultSet rs = executeQuery(query);
        if (rs.next()) {
            try {
                return clazz.getDeclaredConstructor(ResultSet.class).newInstance(rs);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public T findById(int id) throws SQLException {
        return findBy(new DatabaseColumn("id", id));
    }

    @Override
    public boolean create(T entry) throws SQLException {
        String query = String.format("INSERT INTO %s %s", table, entry.toQuery());

        int rowsAffected = connection.prepareStatement(query).executeUpdate();
        Logger.db("Executado update \"" + query + "\" com " + rowsAffected + " linhas afetadas");

        return rowsAffected > 0;
    }

    private ResultSet executeQuery(String query) {
        Logger.db("Executando query \"" + query + "\"");
        ResultSet rs = null;

        try {
            rs = connection.prepareStatement(query).executeQuery();
        } catch (SQLException throwables) {
            Logger.db("Error while executing query:");
            throwables.printStackTrace();
        }

        return rs;
    }
}
