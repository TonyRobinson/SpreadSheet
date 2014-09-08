package GUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

/**
 * This JFrame class is the main container of the spreadsheet program.
 * JOptionPanes are used to gather what row and column count the user wants
 * and generates a JTable of the correct size.
 */
@SuppressWarnings("serial")
public class SpreadSheetFrame extends JFrame{
	private static int numRows;
	private static int numCols;
	SpreadSheetPanel sheetPanel;
	JMenuBar sheetMenuBar;
	
	/**
	 * Constructor
	 */
	public SpreadSheetFrame() {
		super();
		IntroPanel panel = new IntroPanel();
		numCols = panel.getCols();
		numRows = panel.getRows();
		setUpSheetFrame();
		
	}
	
	/**
	 * Initializes the SpreadSheetPanel object and adds it to the
	 * main frame (SpreadSheetFrame).
	 */
	private void setUpSheetFrame() {
		//numCols = 5; numRows = 5;
		
		sheetPanel = new SpreadSheetPanel(numCols, numRows);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(sheetPanel, BorderLayout.CENTER);
		setVisible(true);
		setJMenuBar(sheetPanel.getMenuBar());
		pack();
	}
}
