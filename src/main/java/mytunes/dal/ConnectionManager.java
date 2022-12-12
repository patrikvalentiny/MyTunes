package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author jeppjleemoritzled
 * Used and abused by Team1, thanks again Jeppe ! o/
 */

public class ConnectionManager {
    private static final String CONFIG_FILE_NAME = "config.cfg";
    private final SQLServerDataSource ds = new SQLServerDataSource();

    List<Connection> unusedConnections = new ArrayList<>();
    List<Connection> usedConnections = new ArrayList<>();

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
    public Connection getConnection() {
        if (unusedConnections.isEmpty()){
            return null;
        } else {
            Connection con = unusedConnections.remove(0);
            usedConnections.add(con);
            System.out.println(con);
            return con;
        }
    }

    public void releaseConnection(Connection connection){
        if (usedConnections.contains(connection)){
            usedConnections.remove(connection);
            unusedConnections.add(0, connection);
        }
    }
}
