package sr.will.jarvis.sql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import sr.will.jarvis.Jarvis;

import java.sql.*;

public class Database {
    private Connection connection;

    private Jarvis jarvis;

    public Database(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void connect() {
        System.out.println("Connecting to database...");

        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(jarvis.config.sql.host);
            dataSource.setDatabaseName(jarvis.config.sql.database);
            dataSource.setUser(jarvis.config.sql.user);
            dataSource.setPassword(jarvis.config.sql.password);
            dataSource.setSocketTimeout(86400);
            dataSource.setAutoReconnect(true);

            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Done");

        deployDatabase();
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public PreparedStatement replaceParams(PreparedStatement statement, Object... params) throws SQLException {
        // Get params and insert them into the query
        for (int x = 0; x < params.length; x += 1) {
            if (params[x] == null) {
                statement.setNull(x + 1, Types.NULL);
            } else if (params[x] instanceof String) {
                statement.setString(x + 1, (String) params[x]);
            } else if (params[x] instanceof Integer) {
                statement.setInt(x + 1, (Integer) params[x]);
            } else if (params[x] instanceof Long) {
                statement.setLong(x + 1, (Long) params[x]);
            } else if (params[x] instanceof Boolean) {
                statement.setBoolean(x + 1, (Boolean) params[x]);
            } else {
                throw new SQLException("Unknown paramater type at position " + (x + 1) + ": " + params[x].toString());
            }
        }

        return statement;
    }

    public boolean execute(String query, Object... params) {
        // Send to other function with the status of not tried
        return execute(query, false, params);
    }

    private boolean execute(String query, boolean tried, Object... params) {
        try {
            // Create a prepared statement
            PreparedStatement statement = connection.prepareStatement(query);
            statement = replaceParams(statement, params);

            // Run the query
            return statement.execute();
        } catch (SQLException e) {
            // If the query errors with the start of this message than it has been too long since anything was communicated
            // If tried is false than this is the first attempt, try reconnecting and execute the query again
            if (e.getMessage().startsWith("The last packet successfully received from the server was ") && !tried) {
                reconnect();
                return execute(query, true, params);
            }

            // Otherwise the query failed
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet executeQuery(String query, Object... params) {
        // Send to other function with the status of not tried
        return executeQuery(query, false, params);
    }

    public ResultSet executeQuery(String query, boolean tried, Object... params) {
        try {
            // Create a prepared statement
            PreparedStatement statement = connection.prepareStatement(query);
            statement = replaceParams(statement, params);

            // Run the query
            return statement.executeQuery();
        } catch (SQLException e) {
            // If the query errors with the start of this message than it has been too long since anything was communicated
            // If tried is false than this is the first attempt, try reconnecting and execute the query again
            if (e.getMessage().startsWith("The last packet successfully received from the server was ") && !tried) {
                reconnect();
                return executeQuery(query, true, params);
            }

            // Otherwise the query failed
            e.printStackTrace();
            return null;
        }
    }

    public void deployDatabase() {
        System.out.println("Deploying database....");

        // Create various tables if they do not exist
        execute("CREATE TABLE IF NOT EXISTS mutes(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild char(64) NOT NULL," +
                "user char(64) NOT NULL," +
                "invoker char(64)," +
                "duration bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
        execute("CREATE TABLE IF NOT EXISTS custom_commands(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild char(64) NOT NULL," +
                "command varchar(255) NOT NULL," +
                "response text NOT NULL," +
                "PRIMARY KEY (id));");

        System.out.println("Done.");
    }
}
