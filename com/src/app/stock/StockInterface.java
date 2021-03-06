package app.stock;

import framework.Logger;
import framework.core.db.DatabaseInterface;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StockInterface extends DatabaseInterface<Stock> {
    public StockInterface(Connection connection) {
        super(connection, Stock.class);
        table = "stocks";
    }

    public List<Stock> index() {
        try {
            return findAll();
        } catch (SQLException throwables) {
            Logger.error("Erro ao indexar estoques:");
            throwables.printStackTrace();
        }

        return null;
    }

    public boolean store(String name, int userId) {
        Logger.info("Criando estoque " + name + " pertencente ao id " + userId);

        try {
            return create(new Stock(name, userId));
        } catch (SQLException throwables) {
            Logger.error("Erro ao criar estoque:");
            throwables.printStackTrace();
        }

        return false;
    }

    public boolean remove(Stock entry) {
        Logger.db("Removendo estoque " + entry.getName());

        try {
            return delete(entry);
        } catch (SQLException throwables) {
            Logger.error("Erro ao remover estoque:");
            throwables.printStackTrace();
        }

        return false;
    }

    private boolean delete(Stock entry) throws SQLException {
        String query = String.format("DELETE FROM products WHERE stock_id = '%d'", entry.getId());

        Logger.db("Executando delete \"" + query + "\"");
        int rowsAffected = connection.prepareStatement(query).executeUpdate();
        Logger.db("Executado delete com " + rowsAffected + " linhas afetadas");

        query = String.format("DELETE FROM %s WHERE %s", table, entry.toWhere());
        rowsAffected = connection.prepareStatement(query).executeUpdate();
        Logger.db("Executado delete \"" + query + "\" com " + rowsAffected + " linhas afetadas");

        return rowsAffected > 0;
    }
}
