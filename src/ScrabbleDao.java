import java.sql.*;

public class ScrabbleDao {

    private static ScrabbleDao dao;
    private Connection connection;

    private static final String VERIFY_SQL = "select * from dictionary where word = ?";
    private static final String INSERT_SCORE_SQL = "insert into game_log (player1_id, player2_id, player1_score, player2_score) values (?, ?, ?, ?);";
    private static final String CREATE_PLAYER_SQL = "insert into player (username) values (?);";
    private static final String PLAYER_NAME_SEARCH_SQL = "select id from player where username = ?";
    private PreparedStatement verifySqlStatement;
    private PreparedStatement insertScoreSqlStatement;
    private PreparedStatement createPlayerSqlStatement;
    private PreparedStatement playerNameSearchSqlStatement;

    public static ScrabbleDao getInstantDao(){
        if(null == dao){
            dao = new ScrabbleDao();
        }
        return dao;
    }

    public ScrabbleDao() {
        connection = getConnection();
        try {
            verifySqlStatement = connection.prepareStatement(VERIFY_SQL);
            insertScoreSqlStatement = connection.prepareStatement(INSERT_SCORE_SQL);
            createPlayerSqlStatement = connection.prepareStatement(CREATE_PLAYER_SQL);
            playerNameSearchSqlStatement = connection.prepareStatement(PLAYER_NAME_SEARCH_SQL);
        } catch (SQLException e) {
            System.out.println("PrepareStatement failure");
        }
    }

    protected Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/scrabble?useSSL=false", "thien","1234567890");
        } catch (SQLException e) {
            System.out.println("Getting the connection failure");
        }

        if(null == connection){
            System.out.println("Connection is null");
        }

        return connection;
    }

    public boolean isExist(String word){
        boolean isExisting = false;
        try {
            verifySqlStatement.setString(1,word);
            ResultSet rs = verifySqlStatement.executeQuery();

            if(null != rs && rs.next()){
                isExisting = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isExisting;
    }

    public void insertScore(int player1_id, int player2_id, int player1_score, int player2_score){
        try {
            insertScoreSqlStatement.setInt(1, player1_id);
            insertScoreSqlStatement.setInt(2, player2_id);
            insertScoreSqlStatement.setInt(3, player1_score);
            insertScoreSqlStatement.setInt(4, player2_score);
            insertScoreSqlStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createNewPlayer(String name) {
        StringBuilder sb = new StringBuilder();
        for(char c : name.toCharArray()){
            switch(c){
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\'':
                    sb.append("\\\'");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(c);
            }
        }
        String formattedName = sb.toString();
        try {
            playerNameSearchSqlStatement.setString(1, formattedName);
            ResultSet rs = playerNameSearchSqlStatement.executeQuery();
            if(rs == null){
                return false;
            }
            if (rs.next()) {
                return false;
            } else {
                createPlayerSqlStatement.setString(1, formattedName);
                createPlayerSqlStatement.execute();
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public int findPlayer(String username){StringBuilder sb = new StringBuilder();
        for(char c : username.toCharArray()){
            switch(c){
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\'':
                    sb.append("\\\'");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(c);
            }
        }
        String formattedName = sb.toString();
        try {
            playerNameSearchSqlStatement.setString(1, formattedName);
            ResultSet rs = playerNameSearchSqlStatement.executeQuery();
            if(rs != null){
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Failed to close");
        }
    }
}
