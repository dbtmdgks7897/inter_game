import java.sql.*;
import java.util.Scanner;

public class GameDaoImpl implements GameDao{
    static ResultSet rs = null;
    static PreparedStatement pstmt = null;
    static Connection conn = null;
    public Connection dbConn(){
        final String driver = "org.mariadb.jdbc.Driver";
        final String DB_IP = "localhost";
        final String DB_PORT = "3306";
        final String DB_NAME = "database";
        final String DB_URL =
                "jdbc:mariadb://" + DB_IP + ":" + DB_PORT + "/" + DB_NAME;

        Connection conn = null;
//        ResultSet rs = null;

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(DB_URL, "root", "1234");
            if (conn != null) {
                System.out.println("DB 접속 성공");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 실패");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("DB 접속 실패");
            e.printStackTrace();
        }
        return conn;
    }

    public void dbClose(){
        try {
            //쿼리 닫기
            if (rs != null) {
                rs.close();
            }
            //데이터 닫기
            if (pstmt != null) {
                pstmt.close();
            }
            //sql 닫기
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(GameDto dto) {

        try {
            String sql = "INSERT INTO `database`.`game` (`userid`, `userpw`, `name`) VALUES (?, ?, ?);";

            conn = dbConn();
            pstmt = conn.prepareStatement(sql);

//            System.out.print("아이디 : ");
//            String userId = sc.next();
//            System.out.print("패스워드 : ");
//            String userPw = sc.next();
//            System.out.print("이름 : ");
//            String userName = sc.next();

            pstmt.setString(1, dto.getUserId());
            pstmt.setString(2, dto.getUserPw());
            pstmt.setString(3, dto.getUserName());

            pstmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("error: " + e);
        } finally {
            dbClose();
        }
    }

    @Override
    public GameDto findIdPw(String userId, String userPw) {
        Connection conn = dbConn();
        GameDto udto = null;

        try {
            String sql = "select * from `game` where userid=? and userpw=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userPw);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                udto = new GameDto();
                udto.setUserName(rs.getString("name"));
                udto.setUserBead(rs.getInt("marble"));
                udto.setUserInd(rs.getInt("id"));
                udto.setUserId(rs.getString("userid"));
                udto.setUserPw(rs.getString("userpw"));
            }
        } catch (SQLException e) {
            System.out.println("error: " + e);
        } finally {
            dbClose();
        }
        return udto;
    }

    @Override
    public void update(int id, int plusBead) {
        Connection conn = dbConn();

        try {
            String sql = "UPDATE `database`.`game` SET `marble`=?, update_at = NOW() WHERE  `id`=?;";
            pstmt = conn.prepareStatement(sql);

            if (plusBead <= 0) {
                plusBead += 10;
            }
            pstmt.setInt(1, plusBead);
            pstmt.setInt(2, id);

            // pstmt.executeQuery();
            int result = pstmt.executeUpdate();
            if (result == 0) {
                System.out.println("저장 실패");
            } else {
                System.out.println("저장 성공");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e);
        } finally {
            dbClose();
        }
    }

    @Override
    public void delete(int id) {
        Connection conn = dbConn();
        try {
            String sql = "DELETE FROM `database`.`game` WHERE  `id`=?;";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, id);
            // pstmt.executeQuery();
            int result = pstmt.executeUpdate();
            if (result == 0) {
                System.out.println("삭제 실패");
            } else {
                System.out.println("삭제 성공");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e);
        } finally {
            dbClose();
        }
    }
}
