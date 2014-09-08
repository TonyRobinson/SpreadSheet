package Operational;

import java.util.ArrayList;

/**
 * The cell class represents a cell that can be added to a spreadsheet. The Cell
 * keeps track of other cells that it depends on and also the cells that will
 * depend on this cell.
 */
public class Cell {

	/**
	 * The list of cells that depend on me.
	 */
	private ArrayList<Cell> my_dependents;

	/**
	 * The list of cells that I depend on them.
	 */
	private ArrayList<Cell> my_dependables;

	/**
	 * Formula, this String does not get changed when evaluation occurs.
	 */
	private String formula = "0";

	/**
	 * The Sting representation of the value of the formula, This String does
	 * get changed when evaluation occurs.
	 */
	private String expression = "0";

	/**
	 * Result value of the formula
	 */
	private double my_value = 0;

	/**
	 * A reference to the grid to retrieve and inform other cells that I depend
	 * on them.
	 */
	private Grid grid;

	/**
	 * The name of this cell, only to be set by the spreadsheet before
	 * evaluation.
	 */
	private String name;

	/**
	 * Is this cell a String, value, or formula?
	 */
	private boolean isString = false;

	/**
	 * The String (if this cell is a string).
	 */
	private String theString;

	/**
	 * Creates a new Cell
	 * 
	 * @param the
	 *            grid that will hold this cell.
	 * @param col
	 *            The column this cell was created.
	 * @param row
	 *            The row where this cell was created.
	 */
	public Cell(Grid g, int col, int row) {
		setName(col, row);
		my_dependables = new ArrayList<Cell>();
		my_dependents = new ArrayList<Cell>();
		grid = g;
	}

	/**
	 * Returns the value of this cell.
	 * 
	 * @return double the value
	 */
	public double getValue() {
		return my_value;
	}

	/**
	 * Returns the String representation of the formula of the cell.
	 * 
	 * @return String representation of the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * Breaks down an incoming formula and decides whether it has in depandables
	 * or not.
	 * 
	 * @param String
	 *            f the new formula
	 * 
	 */
	public void setFormula(String f) throws NumberFormatException {
		isString = false;
		formula = f.toUpperCase();
		expression = f;
		Operations op = new Operations();
		my_dependables.clear();
		addDependendables(op.infixToPostfix(formula));
	}

	/**
	 * Resets the cell to a new state.
	 */
	public void reset() {
		my_value = 0;
		isString = false;
		theString = "";
		my_dependables.clear();
		my_dependents.clear();
		setFormula(formula);
	}

	/**
	 * Returns the current expression in this cell. The expression can be a mix
	 * of numeric and cell names depending on what cells were previously
	 * evaluated.
	 * 
	 * @return String the String representation of the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Used for testing.
	 * 
	 * @param double the new value
	 */
	public void setValue(double v) {
		my_value = v;
	}

	/**
	 * Adds the cells that I depend on them from the arrayList of token to the
	 * my_dependables list.
	 * 
	 * @param ArrayList
	 *            of token
	 * 
	 */
	public void addDependendables(ArrayList<String> tokens)
			throws IndexOutOfBoundsException {
		for (String s : tokens) {
			if (Operations.isCell(s)) {
				my_dependables.add(grid.getCell(s)); // add s to the cells I
													 // depend on..
				grid.getCell(s).addDependent(this);  // let s know that I depend
													 // on it...
			}
		}
	}

	/**
	 * Returns the list of cells that I depend on.
	 * 
	 * @return The list of dependables.
	 */
	public ArrayList<Cell> getDependables() {
		return my_dependables;
	}

	/**
	 * Adds a dependent to my dependent collection.
	 * 
	 * @param Cell
	 *            the cell that depends on me
	 */
	public void addDependent(Cell dependent) {
		my_dependents.add(dependent);
	}

	/**
	 * Returns the list of dependents.
	 * 
	 * @return List of dependents.
	 */
	public ArrayList<Cell> getDependents() {
		return my_dependents;
	}

	/**
	 * Returns the number of cells that I depend on
	 * 
	 * @return The in degree.
	 */
	public int degree() {
		return my_dependables.size();
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Return my_value as a String.
	 * 
	 * @return my_value.
	 */
	public String valueToString() {
		return String.valueOf(my_value);
	}

	/**
	 * Prints the cell.
	 * 
	 * @return A pretty cell ;)
	 */
	public String printCell() {
		return "[\t" + expression + "\t]";
	}

	/**
	 * Replaces a dependent string with a value string.
	 * 
	 * @param target The value being replaced.
	 * @param newVal The new value of the dependent/ dependable.
	 */
	public void replace(String target, String newVal) {
		expression = expression.replace(target, newVal);
	}

	/**
	 * Sets the name of the cell at col, row to its alphanumerical
	 * representation. For example: cell at col 2, and row 2, will be named: B2.
	 * 
	 * @param col the column
	 * @param row the row
	 */
	public void setName(int col, int row) {
		name = String.valueOf((char) (col + 65)) + (row);
	}

	/**
	 * Returns the string representation of the cell
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Removes a passed cell from the list of cells that I depend on them
	 * 
	 * @param temp
	 *            - dependable cell to remove.
	 */
	public void removeDependable(Cell temp) {
		my_dependables.remove(temp);
	}
	
	/**
	 * Returns true if the cell contains a string.
	 */
	public boolean isString() {
		return isString;
	}

	/**
	 * Sets this cells string value.
	 * 
	 * @param value The String value.
	 */
	public void setString(String value) {
		theString = value;
		isString = true;
	}

	/**
	 * @return This cells String value.
	 */
	public String getString() {
		return theString;
	}

	/**
	 * Clears the current cell of all information;
	 */
	public void clearCell() {
		my_value = 0;
		isString = false;
		expression = "0";
		formula = "0";
		theString = "";
		my_dependables.clear();
		my_dependents.clear();		
		setFormula(formula);
	}
	
	//Testing
	public static void main(String[] a) {
		Grid grid = new Grid(5, 5);

		// Cell testing
		// Test 1: Naming Convention
		System.out.println("\nTEST 1: NAMING CONVENTION");
		Cell B0 = grid.getCell(1, 0); // Should be B0
		Cell A3 = grid.getCell("A3");
		System.out.println("Should be B0 then A3: " + B0.getName() + ", "
				+ A3.getName());

		// Test 2: Set Formula
		System.out.println("\nTEST 2: SET FORMULA");
		B0.setFormula("A1 + A0 + 10");
		System.out.println("Should be A1 + A0 + 10 for both: "
				+ B0.getFormula() + ", " + B0.getExpression());
		System.out
				.println("2 dependables should have been added to B0, namely (A1 and A0): "
						+ B0.getDependables());
		System.out.println("B0 should now be a dependent of both A1 and A0: "
				+ grid.getCell("A0").getDependents() + ", "
				+ grid.getCell("A1").getDependents());

		// Test 3: Replacing values in the pre
		System.out.println("\nTEST 3: REPLACING VALUES");
		System.out.println("Before: " + B0.getExpression());
		B0.replace(grid.getCell("A0").getName(),
				String.valueOf(grid.getCell("A0").getValue()));
		B0.replace(grid.getCell("A1").getName(),
				String.valueOf(grid.getCell("A1").getValue()));
		System.out.println("After: " + B0.getExpression());

		// Test 4: Evaluating
		System.out.println("\nTEST 4: EVALUATING");
		System.out.println("Before: " + B0.getValue());
		Operations op = new Operations();
		B0.setValue(op.calculatePostfix(op.infixToPostfix(B0.getExpression())));
		System.out.println("After (Should be 10): " + B0.getValue());

		// Test 5: Reset
		System.out.println("\nTEST 5: RESET");
		System.out.println("Before reset: " + B0.getExpression());
		// B0.setFormula("B1 + D5 + 10");
		B0.reset();
		// B0.setFormula("B1 + D5 + 10");
		System.out.println("After reset: " + B0.getExpression());

		// Test 6: Remove Dependable
		System.out.println("\nTEST 6: REMOVE DEPENDABLE");
		System.out
				.println("Cells dependables before remove should be A1 and A0: "
						+ B0.getDependables());
		B0.removeDependable(grid.getCell("A1"));
		System.out.println("Cells dependables after remove should only be A0: "
				+ B0.getDependables());

	}
	
	


}
