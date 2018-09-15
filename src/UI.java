import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class UI extends JFrame {
	
	DB database;

	public UI(DB db) {
		database = db;
		setDefaultLookAndFeelDecorated(true);
		setTitle("Food Manager Desktop");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void stocks(int records, String[] name, int[] amount, String[] unit) {
		JPanel frame = new JPanel();
		frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));

		for (int i = 0; i < records; i++) {
			JPanel item = new JPanel();
			item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
			item.setBorder((Border) new EmptyBorder(new Insets(20, 20, 20, 20)));
			JLabel l1 = new JLabel(name[i] + "\t", JLabel.LEFT);
			JLabel l2 = new JLabel(amount[i] + unit[i], JLabel.RIGHT);
			JButton b1 = new JButton("+");
			JButton b2 = new JButton("-");
			item.add(l1);
			item.add(l2);
			item.add(b1);
			item.add(b2);
			frame.add(item);
		}

		add(frame);
		pack();
		setVisible(true);
	}

	public void foodSelection() {

		JPanel frame = new JPanel();
		add(frame);

		JPanel nav = new JPanel();
		nav.setLayout(new GridLayout(1, 2));
		JButton b1 = new JButton("<");
		JButton b2 = new JButton(">");
		nav.add(b1);
		nav.add(b2);
		frame.add(nav);

		List<Food> res = database.getFood();
		JPanel options = new JPanel();
		options.setLayout(new GridLayout(0, 1));
		for (Food f : res) {
			JButton b = new JButton(f.name());
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFrame subFrame = new JFrame("Count");
					String amount = JOptionPane.showInputDialog(subFrame, "How much in " + f.unit());
					if (amount != null && !amount.isEmpty()) {
						if (amount.matches("^[0-9]*$")) {
							int res = Integer.parseInt(amount);
							if (res > 0) {
								database.addStocks(f.id(), res);
							}
						}
					}
				}
			});
			options.add(b);
		}
		frame.add(options);

		pack();
		setVisible(true);
	}
	
	public void stocksOverview() {

		JPanel frame = new JPanel();
		add(frame);

		List<Stock> res = database.getStock();
		JPanel stocks = new JPanel();
		stocks.setLayout(new GridLayout(0, 1));
		for (Stock s : res) {
			
			JPanel stock = new JPanel();
			stock.add( new JLabel(s.count() + " " + s.name(), JLabel.LEFT));
			stock.add( new JLabel(s.amount() + " " + s.unit(), JLabel.RIGHT));
			JButton add = new JButton("+");
			stock.add(add);
			JButton remove = new JButton("-");
			stock.add(remove);
			
			stocks.add(stock);
		}
		frame.add(stocks);

		pack();
		setVisible(true);
	}

}
