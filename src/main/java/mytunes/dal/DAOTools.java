package mytunes.dal;

import java.sql.*;

public class DAOTools {
    private static final ConnectionManager cm = new ConnectionManager();
    private static PreparedStatement ps;
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
     * Used to execute a SQL query that returns a ResultSet
     * @param query the SQL query to execute
     * @return the ResultSet of the query
     * @throws SQLException if the query fails
     */
    public static ResultSet SQLQueryWithRS(String query) throws SQLException {
        Connection con = cm.getConnection();
        ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
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
        ps = con.prepareStatement(query);
        ps.execute();
        cm.releaseConnection(con);
    }

    public static void closeAllConnections() {
        cm.closeAllConnections();
    }
}
