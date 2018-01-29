package at.ac.tuwien.sepm.fridget.server.persistence;

import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;

/**
 * Autowire to get connection to DB in DAOs
 */
@Component
public class H2Handler {

    /**
     * Connection pool
     */
    private JdbcConnectionPool pool = JdbcConnectionPool.
        create(URL, USER, PASSWORD);

    /**
     * Checks if the database is correctly configured, which ATM means checking if there is data in it
     * If so, then nothing happens, as the regular startup script (database.sql) is used on db startup
     * NOTE: The regular startup script now only contains the CREATE TABLE IF NOT EXISTS instructions
     * Other instruction, e.g. INSERTs were moved to database_init.sql, because they should only be executed
     * if the table is not set up.
     */
    @PostConstruct
    private void checkDBConfiguration() {
        try {
            // Check if db is set up
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM BILL",
                Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                // DB NOT SET UP -> SETUP
                pool.dispose();
                // Create new connection with runscript, that contains INSERT and FOREIGN KEY instructions
                pool = JdbcConnectionPool.create("jdbc:h2:~/fridget;INIT=RUNSCRIPT FROM 'classpath:sql/database_init.sql'",
                    USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * URL to database
     */
    private static final String URL = "jdbc:h2:~/fridget;INIT=RUNSCRIPT FROM 'classpath:sql/database.sql'";

    /**
     * Database driver
     */
    private static final String DRIVER = "org.h2.Driver";

    /**
     * Database user name
     */
    private static final String USER = "sa";

    /**
     * Database password
     */
    private static final String PASSWORD = "";

    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
}
