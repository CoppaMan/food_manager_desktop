import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DB {
	
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:./myManager";  
	static final String USER = "sa"; 
	static final String PASS = "";
	static final String[] CREATE_TABLES = {
			"CREATE TABLE units(unit_id int auto_increment PRIMARY KEY, unit_name VARCHAR(4) UNIQUE NOT NULL)",
			"CREATE TABLE food(food_id int auto_increment PRIMARY KEY, food_name VARCHAR(64) UNIQUE NOT NULL, unit_id int NOT NULL REFERENCES units(unit_id) on delete cascade on update cascade)",
			"CREATE TABLE meals(meal_id int auto_increment PRIMARY KEY, meal_name VARCHAR(64) UNIQUE NOT NULL, cook_time INT NOT NULL, CHECK (cook_time > 0))",
			"CREATE TABLE contains(meal_id int not null references meals(meal_id) on delete cascade on update cascade, food_id int not null references food(food_id) on delete restrict on update cascade, contains_amount int not null, check (contains_amount > 0))",
			"CREATE TABLE stocks(stock_id int auto_increment PRIMARY KEY, food_id int not null references food(food_id) on delete restrict on update cascade, stock_amount int not null, bought_on DATE not null, check (stock_amount > 0))"
			};
	static final List<String> TABLE_NAMES = Arrays.asList("UNITS", "FOOD", "MEALS", "CONTAINS", "STOCKS");
	
	int status = 0; // 0 = Empty schema, 1 = Incomplete schema, 2 = Complete schema
	Connection conn;
	Statement stmt;
	
	public DB() {
		open();
		check();
		if(status == 0) {
			System.out.println("DB is empty!");
			setup();
		} else if (status == 1) {
			System.out.println("DB is incomplete!");
		} else {
			System.out.println("DB is complete!");
		}
	}
	
	private void setup() {
		createTables();
		
	}
	
	public void loadFood(String fileName) {
		
	}
	
	private void createTables() {
		for (String s : CREATE_TABLES) {
	         try {
				stmt.executeUpdate(s);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Food> getFood() {
		ArrayList<Food> res = new ArrayList<Food>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT f.food_id, f.food_name, u.unit_name FROM food f JOIN units u ON f.unit_id = u.unit_id");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				res.add(new Food(rs.getInt(1), rs.getString(2), rs.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public ArrayList<Stock> getStock() {
		ArrayList<Stock> res = new ArrayList<Stock>();
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT f.food_name, u.unit_name, SUM(s.stock_amount), MAX(s.bought_on), COUNT(s.stock_id) "
					+ "FROM food f JOIN units u ON f.unit_id = u.unit_id JOIN stocks s ON f.food_id = s.food_id "
					+ "GROUP BY f.food_id");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				res.add(new Stock(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getDate(4), rs.getInt(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public void addFood(String name, String unit) {
		int unit_id;
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("INSERT INTO units(unit_name) VALUES (?)");
			ps.setString(1, unit.toLowerCase());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		try {
			ps = conn.prepareStatement("SELECT unit_id FROM units WHERE unit_name = ?");
			ps.setString(1, unit.toLowerCase());
			ResultSet rs = ps.executeQuery();
			rs.next();
			unit_id = rs.getInt(1);
			
			ps = conn.prepareStatement("INSERT INTO food(food_name, unit_id) VALUES (?,?)");
			ps.setString(1, name);
			ps.setInt(2, unit_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addStocks(int id, int amount) {
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("INSERT INTO stocks(food_id, stock_amount, bought_on) VALUES (?, ?, ?)");
			ps.setInt(1, id);
			ps.setInt(2, amount);
			ps.setDate(3, new Date(System.currentTimeMillis()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getStatus() {
		return status;
	}
	
	public void open() {
		try {
			Class.forName(JDBC_DRIVER);
	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        stmt = conn.createStatement(); 
	    } catch(Exception e) { 
	         //Error loading the driver
	         e.printStackTrace(); 
	    }
	}
	
	public void check() {
		int missing = 0;
		for(String s : TABLE_NAMES) {
			try {
				DatabaseMetaData md = conn.getMetaData();
				ResultSet rs = md.getTables(null, null, s, null);
				if(!rs.next()) missing++; 
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		status = missing == 0 ? 2 : (missing == TABLE_NAMES.size() ? 0 : 1);
	}
	
	public void close() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}