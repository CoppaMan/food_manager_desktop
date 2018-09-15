public class FoodManagerDesktop { 
   public static void main(String[] args) {
	   DB database = new DB();
	   UI ui = new UI(database);
	   ui.stocksOverview();
   } 
}