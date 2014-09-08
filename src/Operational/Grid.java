package Operational;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * This class creates a grid(Spread sheet) with specific row and column
 * that user entered. It can add and remove the row and column from the grid.
 * Sorts the cells in Ascending order of their degree.
 */

public class Grid {
	LinkedList<LinkedList<Cell>> data = new LinkedList<LinkedList<Cell>>();

	public Grid(int cols, int rows) {
		addRow();
		addColumn();
		for(int x = 1; x < rows; x++){
			addRow();		
		}
		for(int y = 1; y < cols ; y++){
			addColumn();
		}
	}

	/**
	 * Adds one row into the grid
	 */
	public void addRow() {
		data.addLast(new LinkedList<Cell>());
		for (int x = 0; x < getCols(); x++) {
			data.getLast().add(new Cell(this, x, data.size() - 1));
		}
	}

	/**
	 *  Adds one row at a given index.
	 *  
	 *@param index the index where to add the row.
	 */
	public void addRow(int index) {
		LinkedList<Cell> newRow = new LinkedList<Cell>();
		for (int i = 0; i < getRows() - 1; i++) {
			newRow.add(new Cell(this, i, index));
		}
		data.add(index, newRow);
		checkNames();
	}
    
	/**
	 * @param index Remove a row at this index.
	 */
	public void removeRow(int index) {
		data.remove(index);
		checkNames();
	}

	/**
	 * Removes one column from the grid.
	 * 
	 * @param The index to remove the column.
	 */
	public void removeColumn(int index) {
		for (LinkedList<Cell> l : data) {
			l.remove(index);
		}
		checkNames();

	}
    
	/**
	 * Adds a culumn to the grid.
	 */
	public void addColumn() {
		for(int i = 0; i < data.size(); i++){
			LinkedList<Cell> l = data.get(i);
			l.addLast(new Cell(this, l.size(), i));
		}		
	}

	/**
	 * Adds a column as a given index.
	 * 
	 * @param index The index to add the column at. 
	 */
	public void addColumn(int index) {
		for(int i = 0; i < data.size(); i++){
			LinkedList<Cell> l = data.get(i);
			l.add(index, new Cell(this, index, i));
		}
		checkNames();

	}

	/**
	 * if a row for column is removed then the naming conventions need to be checked.
	 */
	public void checkNames(){
		//reset names
		for (int i = 0; i < getRows(); i++) {
			LinkedList<Cell> l = data.get(i);
			for(int j = 0; j<getCols(); j++){
				Cell c = l.get(j);
				c.setName(j, i);
			}
		}
	}
	
	/**
	 * Gets the cell from the grid at x, y
	 * 
	 * @param x the x-coord.
	 * @param y the y-coord
	 * @return The cell at x, y
	 */
	public Cell getCell(int y, int x) throws IndexOutOfBoundsException {
		return data.get(x).get(y);
	}
	
	/**
	 * Gets the cell from the grid at x, y, once x, y is parced from the String name.
	 * 
	 * @param name the name of the string to be computed.
	 * @return The cell at x, y
	 */
	public Cell getCell(String name) throws IndexOutOfBoundsException {
		int col = Integer.valueOf(name.charAt(0) - 65); // 'A' - 65 = 0
		int row = Integer.valueOf(name.substring(1, name.length()));
		return getCell(col, row);
	}
	
	/**
	 * Returns the number of columns that grid has.
	 */
	public int getCols() {
		return data.getFirst().size();
	}
    
	/**
	 * Returns the number of rows that grid has.
	 */
	public int getRows() {
		return data.size();
	}

	
	/**
	 * Calculates the value of cells (including formulas and dependents).
	 */
	public void evaluateCells() {
		Operations op = new Operations();
		try {
			Deque<Cell> sorted = topologicalSort(toSingleList());
			for (Cell c : sorted) {
				try {
					c.setValue(op.calculatePostfix(op.infixToPostfix(c
							.getExpression())));
					for (Cell cell : c.getDependents()) {
						cell.replace(c.getName(), String.valueOf(c.getValue()));
					}
				} catch (NumberFormatException e) {
					c.setString(c.getExpression());
				} 
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"Erroneous Input Detected.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	/**
	 * Returns an arraylist of all cells in grid.
	 * 
	 * @return ArrayList<Cell> of a single list.
	 */
	public ArrayList<Cell> toSingleList() {
		ArrayList<Cell> fullList = new ArrayList<Cell>(getCols() * getRows());
		for (int row = 0; row < data.size(); row++) {
			for (int col = 0; col < data.get(row).size(); col++) {
				Cell cell = data.get(row).get(col);
				cell.setName(col, row);
				fullList.add(cell);
			}
		}
		return fullList;
	}
    
	/**
	 *  Sorts and returns cells in Ascending order of their degree in a dequeue.
	 *  
	 *  @param the_cells - the cells of the grid.
	 *  @return Dequeue<Cell> a sorted deque of cells.
	 */
	private Deque<Cell> topologicalSort(List<Cell> the_cells) throws Exception{
		Deque<Cell> executionOrder = new LinkedList<Cell>();
		boolean isCycle = false;
		int count = 0;
		while (!the_cells.isEmpty() && !isCycle) {
			count++;
			for (int i = 0; i < the_cells.size(); i++) {
				Cell temp = the_cells.get(i);
				if (temp.isString()) {
					the_cells.remove(temp);
				} else if (temp.degree() == 0) {
					count = 0;
					ArrayList<Cell> dependents = temp.getDependents();
					for (Cell c : dependents) {
						c.removeDependable(temp);
					}
					executionOrder.addLast(temp);
					the_cells.remove(temp);
					temp.reset();
				} else if (count > 200) {
					isCycle = true;
					temp.setFormula("0");
					throw new Exception();
				}
			}
		}
		return executionOrder;
	}
		
	/**
	 * Returns the String representation of the this grid 
	 */
	public String nameToString() {
		String temp = "";
		for (LinkedList<Cell> l : data) {
			for (Cell o : l) {
				String s = "[\t" + o.toString() + "\t]";
				temp += s;
			}
			temp += "\n";
		}
		return temp;
	}
	
	@Override
	public String toString() {
		String temp = "";
		for (LinkedList<Cell> l : data) {
			for (Cell o : l) {
				String s = o.valueToString();
				temp += s;
			}
			temp += "\n";
		}
		return temp;
	}
	
	//Testing
	public static void main(String[] a ) {
		Grid grid = new Grid(2, 2);
		//Test 1: Naming
		System.out.println("TEST 1: CELL NAMES");
		System.out.println(grid.nameToString());
		
		//Test 2a: Add Row 
		System.out.println("\nTEST 2a: ADD ROW");
		grid.addRow();
		System.out.println(grid.nameToString());
		
		//Test 2b: Add Row 
		System.out.println("\nTEST 2b: ADD ROW at INDEX");
		grid.addRow(1);
		//grid.addRow(5);
		System.out.println(grid.nameToString());
		
		//Test 2: Add Column
		System.out.println("\nTEST 2a: ADD COLUMN");
		grid.addColumn();
		System.out.println(grid.nameToString());
		
		//Test 3: Add Row
		System.out.println("\nTEST 2b: ADD COLUMN at INDEX");
		grid.addColumn(1);
		System.out.println(grid.nameToString());
		
		//Test 4: Remove Row
		System.out.println("TEST 4: REMOVE ROW");
		grid.removeRow(1);
		System.out.println(grid.nameToString());
		
		//Test 4: Remove Column
		System.out.println("TEST 4: REMOVE COLUMN");
		grid.removeColumn(1);
		System.out.println(grid.nameToString());
		
		//Test 5: Get a cell
		System.out.println("\nTEST 5a GET CELL");
		System.out.println("Expected: C2: " + "Actual: " + grid.getCell("C2").getName());
		System.out.println("\nTEST 5b GET CELL");
		System.out.println("Expected: C0: " + "Actual: " + grid.getCell(2, 0).getName());

		//Test 6: Get rows/cols
		System.out.println("\nTEST 6: GET ROWS/COLS");
		System.out.println("Expected Rows: 3, Expected Cols: 3");
		System.out.println("Actual Rows: " + grid.getRows() + ", Actual Cols: " + grid.getCols());

		//Test 7: In Degree
		System.out.println("\nTEST 7: Degree");
		grid.getCell("A1").setFormula("B0 + B1 + B2");
		System.out.println("A1 expected degree: 3, Actual: " + grid.getCell("A1").degree());
		grid.getCell("A1").removeDependable(grid.getCell("B2"));
		System.out.println("A1 expected degree: 2, Actual: " + grid.getCell("A1").degree());

		
		//Test 8: Sorting
		System.out.println("\nTEST 8: CELL SORTING");
		System.out.println("List before sort" + grid.toSingleList());
		grid.getCell("A1").setFormula("B0 + B1 + B2");
		grid.getCell("B2").setFormula("C2");
		try {
			System.out.println("List after sort (A1 should be last following B2): " + grid.topologicalSort(grid.toSingleList()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(grid.getCell("A1").getFormula());
		
		//Test 9: Detect Cycle
		System.out.println("\nTEST 9: DETECT CYCLE");
		grid.getCell("C0").setFormula("C0");
		System.out.println("Cycle formula: " + grid.getCell("C0").getFormula());
		try {
			grid.topologicalSort(grid.toSingleList());
		} catch (Exception e) {
			System.out.println("The cycle was detected!");
		}
		System.out.println("Formula after a detected cycle: " + grid.getCell("C0").getFormula());

		
		//Test 10: Evaluate cells
		System.out.println("\nTEST 10: EVALUATE CELLS");
		grid.getCell("B0").setFormula("5");
		grid.getCell("B1").setFormula("10");
		grid.getCell("B2").setFormula("15");
		grid.evaluateCells();
		System.out.println("A1 expected value: 30, A1 Actual Value: " + grid.getCell("A1").getValue());
		
		
		
	}
}
