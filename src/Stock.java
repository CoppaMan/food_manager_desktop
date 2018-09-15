import java.util.Date;

public class Stock {
	
	private String stockName;
	private String unitName;
	private int stockAmount;
	private Date boughtOn;
	private int count;
	
	public Stock(String name, String unit, int amount, Date last, int number) {
		stockName = name;
		unitName = unit;
		stockAmount = amount;
		boughtOn = last;
		count = number;
	}
	
	public String name() {
		return stockName;
	}
	
	public String unit() {
		return unitName;
	}
	
	public int amount() {
		return stockAmount;
	}
	
	public Date last() {
		return boughtOn;
	}
	
	public int count() {
		return count;
	}
}
