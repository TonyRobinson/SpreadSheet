package GUI;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Use a JPanel to ask user for size of the spread sheet. The valid range for
 * number of rows and columns of the spread sheet is between 2 and 500, so if
 * user enters an invalid input , it shows a message until to get the valid
 * input. 
 */
@SuppressWarnings("serial")
public class IntroPanel extends JPanel {
	/**
	 * The number of rows text field.
	 */
	JTextField numRows = new JTextField("5", 5);
	
	/**
	 * The number of columns text field.
	 */
	JTextField numCols = new JTextField("5", 5);
	
	/**
	 * The number of rows.
	 */
	private int rows;
	
	/**
	 * The number of columns.
	 */
	private int cols;

	/**
	 * This panel creates an Intro into the SpreadSheet program. The user can select the number of rows and columns needed.
	 */
	public IntroPanel() {
		add(new JLabel("Rows (2-500):"));
		add(numRows);
		add(Box.createHorizontalStrut(15)); // a spacer
		add(new JLabel("Columns (2-500):"));
		add(numCols);
		boolean valid = false;
		while (!valid) {
			int result = JOptionPane
					.showConfirmDialog(null, this,
							"How many cells do you need?",
							JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				try {
					valid = true;
					rows = Integer.parseInt(numRows.getText());
					if (rows < 2 || rows > 500)
						throw new IndexOutOfBoundsException();
					cols = Integer.parseInt(numCols.getText());
					if (cols < 2 || cols > 500)
						throw new IndexOutOfBoundsException();

				} catch (NumberFormatException e) {
					valid = false;
					JOptionPane
							.showMessageDialog(this,
									"Your entry is not in the proper format. \n Please enter only integer values.");
				} catch (IndexOutOfBoundsException e) {
					valid = false;
					JOptionPane
							.showMessageDialog(
									this,
									"The number you entered is out of range. \n Please enter an integer between 2 and 500");
				}
			} else if (result == JOptionPane.CANCEL_OPTION
					|| result == JOptionPane.CLOSED_OPTION) {
				System.exit(0);
			}
		}
	}

	/**
	 * @returns the number of rows chosen by the user.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @returns the number of columns chosen by the user.
	 */
	public int getCols() {
		return cols;
	}

}
