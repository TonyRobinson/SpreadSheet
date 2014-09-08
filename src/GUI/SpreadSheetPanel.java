package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;

import Operational.Cell;

/**
 * The main spreadsheet panel. This spreadsheet contains the JTable and nessery
 * menu items to support table operations.
 */
@SuppressWarnings("serial")
public class SpreadSheetPanel extends JPanel {

	/**
	 * The top formula field, will allow the user to edit the pre existing
	 * formula.
	 */
	private JTextField formulaField;

	/**
	 * The primary table that will hold all cells.
	 */
	private JTable table;

	/**
	 * The table model connects the JTable with the Grid.
	 */
	private ExcelTableModel tableModel;

	/**
	 * A menu bar.
	 */
	private JMenuBar sheetMenu;

	/**
	 * The menus to be added to the sheet menu.
	 */
	private JMenu fileMenu, optionsMenu, removeColMenu, removeRowMenu;

	/**
	 * The items that will be present in the JMenu.
	 */
	private JMenuItem clearItem, openItem, saveItem, printItem, toggleGridItem,
			newColItem, newRowItem, removeFirstRow, removeFirstCol,
			removeLastRow, removeLastCol, removeSelectedCol, removeSelectedRow;

	/**
	 * The last row and column where changes were made.
	 */
	private int activeRow, activeCol;

	/**
	 * Do we want to show the on the table or not.
	 */
	private boolean gridFlag = true;

	/**
	 * File Dialog used to open and save files.
	 */
	private JFileChooser fileChooser;
	
	/**
	 * File filter to limit selectable files;
	 */
	final private FileFilter filter1;
	
	/**
	 * Scanner to read files.
	 */
	private Scanner fileScanner;
	
	/**
	 * Makes a new complete spreadsheet panel.
	 * 
	 * @param rows
	 *            Number of rows
	 * @param cols
	 *            Number of columns
	 */
	public SpreadSheetPanel(int rows, int cols) {
		super(new BorderLayout());
		fileChooser = new JFileChooser(".");
		filter1 = new ExtensionFileFilter("CSV", new String[] {"CSV"});
		fileChooser.setFileFilter((javax.swing.filechooser.FileFilter) filter1);
		tableModel = new ExcelTableModel(rows, cols);
		table = new JTable(tableModel);
		table.setRowSelectionAllowed(false);
		formulaField = new JTextField();
		/*
		if (cols > 15)
			cols = 15;
		if (rows > 30)
			rows = 30;
		*/
		setUpTable(rows, cols);
		setUpTableMenu();
		addMenuListener();
		addMouse(table);
		addFocus();
	}

	/**
	 * Sets up GUI for the spread sheet table.
	 * 
	 * @param rows
	 * @param cols
	 */
	private void setUpTable(int rows, int cols) {
		// Add Formula Field at top
		add(formulaField, BorderLayout.NORTH);
		// Table Sizing
		table.setPreferredScrollableViewportSize(new Dimension(cols * 75,
				rows * 16));
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTable rowTable = new RowNumberTable(table);
		// Add Scroll Pane
		JScrollPane scrollPane = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setRowHeaderView(rowTable);
		add(scrollPane);
	}

	/**
	 * Sets up spread sheet menu bar.
	 */
	private void setUpTableMenu() {
		sheetMenu = new JMenuBar();
		clearItem = new JMenuItem("Clear Table");
		openItem = new JMenuItem("Open");
		saveItem = new JMenuItem("Save");
		newColItem = new JMenuItem("New Column");
		newRowItem = new JMenuItem("New Row");
		printItem = new JMenuItem("Print SpreadSheet");
		toggleGridItem = new JMenuItem("Toggle Grid");
		removeFirstRow = new JMenuItem("Remove First Row");
		removeFirstCol = new JMenuItem("Remove First Column");
		removeLastRow = new JMenuItem("Remove Last Row");
		removeLastCol = new JMenuItem("Remove Last Column");
		removeSelectedRow = new JMenuItem("Remove Selected Row");
		removeSelectedCol = new JMenuItem("Remove Selected Column");
		removeColMenu = new JMenu("Remove Column");
		removeRowMenu = new JMenu("Remove Row");
		fileMenu = new JMenu("File");
		optionsMenu = new JMenu("Options");
		sheetMenu.add(fileMenu);
		sheetMenu.add(optionsMenu);
		fileMenu.add(clearItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(printItem);
		removeColMenu.add(removeFirstCol);
		removeColMenu.add(removeLastCol);
		removeColMenu.add(removeSelectedCol);
		removeRowMenu.add(removeFirstRow);
		removeRowMenu.add(removeLastRow);
		removeRowMenu.add(removeSelectedRow);
		optionsMenu.add(toggleGridItem);
		optionsMenu.add(newColItem);
		optionsMenu.add(newRowItem);
		optionsMenu.add(new JSeparator());
		optionsMenu.add(removeColMenu);
		optionsMenu.add(removeRowMenu);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		optionsMenu.setMnemonic(KeyEvent.VK_T);
	    clearItem.setMnemonic(KeyEvent.VK_C);
		openItem.setMnemonic(KeyEvent.VK_O);
		saveItem.setMnemonic(KeyEvent.VK_S);
		printItem.setMnemonic(KeyEvent.VK_P);
	}

	/**
	 * Add listeners to the menu items. Allow the menu items to perform functions on the table.
	 */
	private void addMenuListener() {
		newColItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.cols++;
				tableModel.grid.addColumn();
				tableModel.fireTableStructureChanged();
			}
		});

		newRowItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.rows++;
				tableModel.grid.addRow();
				tableModel.fireTableStructureChanged();
			}
		});

		removeFirstCol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeColumn(tableModel, 0);
			}
		});

		removeLastCol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeColumn(tableModel, tableModel.cols - 1);
			}
		});

		removeSelectedCol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeColumn(tableModel, table.getSelectedColumn());
			}
		});

		removeFirstRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeRow(tableModel, 0);
			}
		});
		removeLastRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeRow(tableModel, tableModel.rows - 1);
			}
		});

		removeSelectedRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				removeRow(tableModel, table.getSelectedRow());
			}
		});

		toggleGridItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gridFlag = (!gridFlag) ? true : false;
				table.setShowGrid(gridFlag);
			}
		});

		clearItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < tableModel.rows; i++) {
					for (int j = 0; j < tableModel.cols; j++) {
						tableModel.grid.getCell(i,j).clearCell();
						tableModel.fireTableDataChanged();
					}
				}
			}
		});

		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File openFile;
				int rowCount = 0;
				int retVal = fileChooser.showOpenDialog((Component) arg0.getSource());
				if (retVal == JFileChooser.APPROVE_OPTION) {
					openFile = fileChooser.getSelectedFile();
					try {
						fileScanner = new Scanner(openFile);
						while (fileScanner.hasNextLine()) {
							String rowString = fileScanner.nextLine();
							insertValues(rowString, rowCount);
							rowCount++;
						}
						tableModel.grid.evaluateCells();
						tableModel.fireTableDataChanged();
					} catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null,"This file does not exist", "File Existance Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int savePos = 0;
				File saveFile;
				FileWriter writer;
				BufferedWriter bufWriter;

				int retVal = fileChooser.showSaveDialog((Component) arg0.getSource());
				if (retVal == JFileChooser.APPROVE_OPTION) {
					saveFile = fileChooser.getSelectedFile();
					try {
						writer = new FileWriter(saveFile.getAbsoluteFile());
						bufWriter = new BufferedWriter(writer);
						while (savePos < tableModel.rows) {
							bufWriter.write(getValues(savePos));
							savePos++;
						}
						bufWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		printItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					table.print();
				} catch (PrinterException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Adds a mouse listener to the JTable, The JTable listens for a click so it can set the formulaField.
	 * 
	 * @param table The table to add the mouse listener too.
	 */
	private void addMouse(final JTable table) {
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					Point pnt = evt.getPoint();
					activeRow = table.rowAtPoint(pnt);
					activeCol = table.columnAtPoint(pnt);
					String formula = tableModel.grid.getCell(activeCol,
							activeRow).getFormula();
					if (formula != "0") {
						formulaField.setText("=" + formula + "");
					} else {
						formulaField.setText("");
					}
					repaint();
				}
			}
		});
	}

	/**
	 * Adds focus listener to the formula field.
	 */
	private void addFocus() {
		formulaField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) {// not used
			}
		
			@Override
			public void focusLost(FocusEvent arg0) {
				String newFormula = formulaField.getText();
				if (newFormula.length() > 0) {
					tableModel.setValueAt(newFormula, activeRow, activeCol);
				}
				repaint();
			}
		});
	}

	/**
	 * Removes from tableModel column at index.
	 * 
	 * @param tableModel The table model to update.
	 * @param index The index to remove.
	 */
	private void removeColumn(ExcelTableModel tableModel, int index) {
		try {
			if (tableModel.cols - 1 < 2)
				throw new IndexOutOfBoundsException();
			tableModel.cols--;
			tableModel.grid.removeColumn(index);
			tableModel.fireTableStructureChanged();
			tableModel.grid.evaluateCells();
		} catch (IndexOutOfBoundsException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"Error removing column.\nSpreadSheet cannot have less than 2 columns.",
							"Table Size Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Removes from tableModel row at index.
	 * 
	 * @param tableModel The table model to update.
	 * @param index The index to remove.
	 */
	private void removeRow(ExcelTableModel tableModel, int index) {
		try {
			if (tableModel.rows - 1 < 2)
				throw new IndexOutOfBoundsException();
			tableModel.rows--;
			tableModel.grid.removeRow(index);
			tableModel.fireTableStructureChanged();
			tableModel.grid.evaluateCells();
		} catch (IndexOutOfBoundsException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"Error removing row.\nSpreadSheet cannot have less than 2 rows.",
							"Table Size Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Helper method to insert strings, values and formulas into the
	 * current grid.
	 * @param row Comma delimited string of the current row.
	 * @param rowCount The current row 0 inclusive.
	 */
	private void insertValues(String row, int rowCount) {
		StringTokenizer rowTokens = new StringTokenizer(row, ",");
		int colCount = 0;
		while (rowTokens.hasMoreTokens()) {
			String nextToken = rowTokens.nextToken(",");
			if (nextToken.charAt(0) == '=') {
				nextToken = nextToken.substring(nextToken.indexOf('=') + 1);
				if (!nextToken.equals("0")){
					tableModel.grid.getCell(colCount, rowCount).setFormula(nextToken);
				} 
			} else {
				tableModel.grid.getCell(colCount, rowCount).setString(nextToken);
			}
			
			
			colCount++;
		}
		
	}
	
	/**
	 * Helper method to retrieve values from cells and print out to a file.
	 * @param rowPos The current row position ) inclusive.
	 * @return A comma delimited string of the current row.
	 */
	private String getValues(int rowPos) {
		String retVal = new String();
		for (int i = 0; i < tableModel.cols; i++) {
			Cell tempCel = tableModel.grid.getCell(i, rowPos);
			if (!tempCel.isString()) {
				if (i < tableModel.cols - 1) {
					retVal += "=" + tableModel.grid.getCell(i, rowPos).getFormula() + ",";
				} else {
					retVal += "=" + tableModel.grid.getCell(i, rowPos).getFormula() + "\n";
				}
			} else {
				if (i < tableModel.cols - 1) {
					retVal += tableModel.grid.getCell(i, rowPos).getString() + ",";
				} else {
					retVal += tableModel.grid.getCell(i, rowPos).getString() + "\n";
				}
			}
			
		}
		return retVal;
	}
	/**
	 * @return A ready JMenuBar.
	 */
	public JMenuBar getMenuBar() {
		return sheetMenu;
	}
}
