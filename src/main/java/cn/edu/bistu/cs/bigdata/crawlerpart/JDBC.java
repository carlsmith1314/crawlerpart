package cn.edu.bistu.cs.bigdata.crawlerpart;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {

    private final String url = "jdbc:mysql://localhost:3306/kw?useUnicode=true&characterEncoding=utf8";
    private final String user = "root";
    private final String password = "123456";
    private static Connection conn;
    private static Statement stmt;

    public JDBC() {
        try {
            // 加载驱动
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver);
            // 连接数据库
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("The database connection is successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 插入语句
    public void insert(String sql) {
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询语句
    public ResultSet select(String sql) {
        try {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        try {

            conn.close();
            System.out.println("Close the database connection successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JDBC jdbc = new JDBC();
        String sql = "INSERT INTO articleInformation VALUES('机器学习', '[a, b, c]', '测试集合', '[2, 3]');";
        jdbc.insert(sql);
        jdbc.close();
    }
}