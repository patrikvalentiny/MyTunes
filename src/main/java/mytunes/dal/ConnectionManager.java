package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Class responsible for managing the connection to the database.
 * Implementation in this class is BAD practice from my understanding but performs well
 * Closing connection should be done sooner from my understanding not on the application termination
 * This will be fixed in the future versions of the course - considering using C3P0 connection pooling
 *
 * @author Patrik Valentiny
 */
public class ConnectionManager {
    private static final String CONFIG_FILE_NAME = "config.cfg";
    private final SQLServerDataSource ds = new SQLServerDataSource();

    private List<Connection> unusedConnections = new ArrayList<>();
    private List<Connection> usedConnections = new ArrayList<>();

    public ConnectionManager()
    {
        Properties props = new Properties();
        try {
            props.load(new FileReader(CONFIG_FILE_NAME));
        } catch (IOException e) {
            System.out.println("Config file could not be loaded");
            //throw new RuntimeException(e);
        }

        ds.setServerName(props.getProperty("SERVER"));
        ds.setDatabaseName(props.getProperty("DATABASE"));
        ds.setPortNumber(Integer.parseInt(props.getProperty("PORT")));
        ds.setUser(props.getProperty("USER"));
        ds.setPassword(props.getProperty("PASSWORD"));
        ds.setTrustServerCertificate(true);
        generateConnections();
    }


    private void generateConnections(){
        for (int i = 0; i < 10; i++) {
            try {
                unusedConnections.add(ds.getConnection());
            } catch (SQLServerException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public Connection getConnection() throws SQLException {
        if (unusedConnections.isEmpty()){
            return ds.getConnection();
        } else {
            Connection con = unusedConnections.remove(0);
            if (con.isValid(0)){
                usedConnections.add(con);
                return con;
            } else {
                Connection newCon = ds.getConnection();
                usedConnections.add(newCon);
                return newCon;
            }
        }
    }

    public void releaseConnection(Connection connection){
        if (usedConnections.contains(connection)){
            usedConnections.remove(connection);
            unusedConnections.add(0, connection);
        }
    }

    public void closeAllConnections(){
        for (Connection con : unusedConnections){
            try {
                con.close();
                System.out.println(con.isClosed());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (Connection con : usedConnections){
            try {
                con.close();
                System.out.println(con.isClosed());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
