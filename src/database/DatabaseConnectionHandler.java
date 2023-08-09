package database;

import model.*;
import util.PrintablePreparedStatement;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DatabaseConnectionHandler {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";

    private Connection connection = null;

    public DatabaseConnectionHandler() {
        try {
            // Load the Oracle JDBC driver
            // Note that the path could change for new drivers
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public boolean login(String username, String password) {
        try {
            if (connection != null) {
                connection.close();
            }

            connection = DriverManager.getConnection(ORACLE_URL, username, password);
            connection.setAutoCommit(false);

            System.out.println("\nConnected to Oracle!");
            return true;
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return false;
        }
    }

    private void rollbackConnection() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public Menus getMenusInfo() {
        Menus menus = new Menus();

        try {
            String query = "SELECT * FROM Menu";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Menu menu = new Menu(rs.getString("name"),
                        rs.getFloat("cost"),
                        rs.getString("category"));
                menus.addMenu(menu);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return menus;
    }

    public Reservations getReservationsInfo() {
        Reservations reservations = new Reservations();

        try {
            String query = "SELECT * FROM Reservations";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ResultSet rs = ps.executeQuery();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

            while(rs.next()) {
                Reservation reservation = new Reservation(rs.getInt("rID"),
                                                            rs.getInt("cID"),
                                                            rs.getInt("locID"),
                                                            rs.getInt("wID"),
                                                            dateFormat.format(rs.getDate("rDate")),
                                                            timeFormat.format(rs.getDate("rTime")),
                                                            rs.getInt("numOfPeople"),
                                                            rs.getString("reservationName"));

                reservations.addReservation(reservation);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return reservations;
    }

    public Branches getBranchesInfo() {
        Branches branches = new Branches();

        try {
            String query = "SELECT * FROM Branches";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Branch branch = new Branch(rs.getInt("locID"),
                                            rs.getString("streetAddress"),
                                            rs.getString("city"),
                                            rs.getString("province"));

                branches.addBranches(branch);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return branches;
    }

    public void insertBranch(Branch model) {
        try {
            String query = "INSERT INTO branches VALUES (?,?,?,?)";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ps.setInt(1, model.getLocId());
            ps.setString(2, model.getStreetAddress());
            ps.setString(3, model.getCity());
            ps.setString(4, model.getProvince());
            
            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void insertMenu(Menu model) {
        try {
            String query = "INSERT INTO menu VALUES (?,?,?)";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ps.setString(1, model.getName());
            ps.setFloat(2, model.getCost());
            ps.setString(3, model.getCategory());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void insertReservation(Reservation model) {
        try {
            String query = "INSERT INTO reservations VALUES (?,?,?,?,?,?,?,?)";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ps.setInt(1, model.getRId());
            ps.setInt(2, model.getCId());
            ps.setInt(3, model.getLocId());
            ps.setInt(4, model.getWId());
            ps.setDate(5, Date.valueOf(model.getDate()));
            ps.setTime(6, Time.valueOf(model.getTime()));
            ps.setInt(7,model.getNumOfPeople());
            ps.setString(8,model.getReservationName());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void deleteMenu(String tblName) {
        try {
            String query = "DELETE FROM Menu WHERE name = ?";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ps.setString(1, tblName);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Menu " + tblName + " does not exist!");
            }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void deleteReservation(int tblRId) {
        try {
            String query = "DELETE FROM Reservations WHERE rID = ?";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ps.setInt(1, tblRId);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Reservation " + tblRId + " does not exist!");
            }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public void deleteBranch(int tblLocId) {
        try {
            String query = "DELETE FROM Branches WHERE locID = ?";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ps.setInt(1, tblLocId);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Branch " + tblLocId + " does not exist!");
            }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public Menus showDivision() {
        Menus menus = new Menus();

        try {
            String query = "SELECT name " +
                            "FROM Menu m " +
                            "WHERE NOT EXISTS (" +
                                                "(SELECT locID FROM Branches b) " +
                                                "MINUS " +
                                                "(SELECT locID " +
                                                "FROM MenuServed ms " +
                                                "WHERE m.name = ms.menuName))";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Menu menu = new Menu(rs.getString("name"));
                menus.addMenu(menu);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return menus;
    }

    public MenusAvgCost showAvgCostMenu() {
        MenusAvgCost menus = new MenusAvgCost();

        try {
            String query = "SELECT category, AVG(cost) as avgCost " +
                    "FROM Menu m " +
                    "GROUP BY category " +
                    "HAVING COUNT(category) > 1";
            PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                AvgCostHaving menu = new AvgCostHaving(rs.getString("category"), rs.getInt("avgCost"));
                menus.addAvgCostHaving(menu);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return menus;
    }
}
