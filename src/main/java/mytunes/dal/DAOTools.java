package mytunes.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DAOTools {
    private static final ConnectionManager cm = new ConnectionManager();
    /**
     * Used to replace ' with '' in a string to make it SQL compatible
     *
     * @param string the string to check
     * @return the string with ' replaced by ''
     */
    public static String validateStringForSQL(String string) {
        if (string == null) return null;
        string = string.replace("'", "''");
        return string;
    }

    /**
     * SHOULD BE USED WITH A TRY-WITH-RESOURCES STATEMENT
     * Used to execute a SQL query that returns a ResultSet
     * @param query the SQL query to execute
     * @return the ResultSet of the query
     * @throws SQLException if the query fails
     */
    public static ResultSet SQLQueryWithRS(String query) throws SQLException {
        Connection con = cm.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        cm.releaseConnection(con);
        return rs;

    }

    /**
     * Used to execute a SQL query that does not return a ResultSet
     * @param query the SQL query to execute
     * @throws SQLException if the query fails
     */
    public static void SQLQuery(String query) throws SQLException {
        Connection con = cm.getConnection();
        Statement stmt = con.createStatement();
        stmt.execute(query);
        cm.releaseConnection(con);
    }
}
