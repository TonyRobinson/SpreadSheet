package GUI;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import Operational.Cell;
import Operational.Grid;

/**
 * The model used by the JTable get and set all cell values.
 */
@SuppressWarnings("serial")
public class ExcelTableModel extends DefaultTableModel {

	/**
	 * The main grid.
	 */
	protected Grid grid;

	/**
	 * The number of columns the table will populate.
	 */
	protected int cols;

	/**
	 * The number of rows the table will populate.
	 */
	protected int rows;

	/**
	 * A constructor for the table model.
	 * 
	 * @param cols
	 *            The number of columns the table will draw.
	 * @param rows
	 *            The number of rows the table will draw.
	 */
	public ExcelTableModel(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		this.grid = new Grid(rows, cols);
	}

	/**
	 * Returns the current number of columns in the grid.
	 */
	public int getColumnCount() {
		return cols;
	}

	/**
	 * Returns the current number of rows in the grid.
	 */
	public int getRowCount() {
		return rows;
	}

	/**
	 * Converts the column integer into the appropriate column name.
	 * 
	 * @param col
	 *            The index of the column the name is being requested.
	 * @return A string name of the column.
	 */
	public String getColumnName(int col) {
		int name = col;
		String s = "";
		if (name < 26) {
			s += String.valueOf(Character.valueOf((char) (name + 65)));
		} else {
			s += String.valueOf(Character.valueOf((char) (name / 26 + 64)));
			s += String.valueOf(Character.valueOf((char) (name % 26 + 65)));
		}
		return s;
	}

	/**
	 * Returns the value of the cell on the table at a given row and col
	 * 
	 * @param row
	 *            The row requested.
	 * @param col
	 *            The col requested.
	 */
	public Object getValueAt(int row, int col) {
		Cell cell = grid.getCell(col, row);
		String s = "";
		if (cell.isString())
			s = cell.getString();
		else if (cell.getFormula() != "0") {
			s = cell.valueToString();
		}
		return s;
	}

	/**
	 * Sets the cell at row, col to the passed value (if it's a String).
	 * 
	 * @param value
	 *            The value to set
	 * @param row
	 *            The row in which to set the value.
	 * @param col
	 *            The col in which to set the value.
	 */
	public void setValueAt(Object value, int row, int col) {
		Cell cell = grid.getCell(col, row);
		if (value.toString().length() > 0) {
			String s = value.toString();
			if (s.charAt(0) != '=') {
				cell.setString((String) value);
			} else {
				try {
					cell.setFormula(s.substring(1, s.length()));
					grid.evaluateCells();
				} catch (NumberFormatException e) {
					cell.setFormula("0");
					JOptionPane.showMessageDialog(null,
							"Your formula is not in the correct format.",
							"Formula Error", JOptionPane.ERROR_MESSAGE);
				} catch (IndexOutOfBoundsException e) {
					cell.setFormula("0");
					JOptionPane.showMessageDialog(null,
							"A cell chosen does not exist in your formula.",
							"Cell Does Not Exist", JOptionPane.ERROR_MESSAGE);
				}
				grid.evaluateCells();
			}
		} else {
			cell.reset();
		}
		fireTableDataChanged();
	}

}