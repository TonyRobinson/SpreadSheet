package GUI;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * This class take in a JTable and syncs that table with this one
 */
@SuppressWarnings("serial")
public class RowNumberTable extends JTable implements ChangeListener,
		PropertyChangeListener {

	/**
	 * The main table to add the rows too.
	 */
	private JTable main;

	/**
	 * Creates a new table of rows.
	 * 
	 * @param table
	 *            The table to sync the rows with.
	 */
	public RowNumberTable(JTable table) {
		main = table;
		main.addPropertyChangeListener(this);

		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		setModel(main.getModel());
		setSelectionModel(main.getSelectionModel());

		TableColumn column = new TableColumn();
		column.setHeaderValue(" ");
		addColumn(column);
		column.setCellRenderer(new RowNumberRenderer());

		getColumnModel().getColumn(0).setPreferredWidth(50);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Component c = getParent();

		// Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport) {
			JViewport viewport = (JViewport) c;
			viewport.addChangeListener(this);
		}
	}

	/**
	 * Make the rows the same number as the main table has.
	 */
	@Override
	public int getRowCount() {
		return main.getRowCount();
	}

	/**
	 * Make height same as main cell height.
	 */
	@Override
	public int getRowHeight(int row) {
		return main.getRowHeight(row);
	}

	/**
	 * Names the rows by its corresponding number.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		return Integer.toString(row);
	}

	/**
	 * Make row labels not editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * Sync with main table when scrolling.
	 */
	public void stateChanged(ChangeEvent e) {
		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane) viewport.getParent();
		scrollPane.getVerticalScrollBar()
				.setValue(viewport.getViewPosition().y);
	}

	/**
	 * Sync with main table when changes are made.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if ("selectionModel".equals(e.getPropertyName())) {
			setSelectionModel(main.getSelectionModel());
		}

		if ("model".equals(e.getPropertyName())) {
			setModel(main.getModel());
		}
	}

	/**
	 * How the cells look, Bold when selected, Aligned Center, Background etc...
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer {
		public RowNumberRenderer() {
			setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					setBackground(header.getBackground());
					setFont(header.getFont());
				}
			}

			if (isSelected) {
				setFont(getFont().deriveFont(Font.BOLD));
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return this;
		}
	}
}
