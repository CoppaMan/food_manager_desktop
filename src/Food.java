
public class Food {
	
	private int foodId;
	private String foodName;
	private String unitName;
	
	public Food(int id, String name, String unit) {
		foodId = id;
		foodName = name;
		unitName = unit;
	}
	
	public int id() {
		return foodId;
	}
	
	public String name() {
		return foodName;
	}
	
	public String unit() {
		return unitName;
	}
}
