package com.ruben.codespector.settings

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.ruben.codespector.MessageBundle
import java.awt.Component
import java.beans.PropertyChangeListener
import javax.swing.DefaultCellEditor
import javax.swing.JTextField
import javax.swing.event.TableModelListener
import javax.swing.table.AbstractTableModel

class PackageTable(
    packages: Set<String>,
    packageTableListener: PackageTableListener
) {

    interface PackageTableListener {
        fun onPackagesChanged(packages: Set<String>)
    }

    private val packageTableModel = PackageTableModel(packages.toMutableList(), packageTableListener)

    private val _propertyChangeListener: PropertyChangeListener = PropertyChangeListener {
        val editorValue = ((table.cellEditor as? DefaultCellEditor)?.component as? JTextField)?.text.orEmpty()
        val row = table.selectedRow

        if (!table.isEditing && editorValue.isNotEmpty()) {
            packageTableModel.updateRow(row, editorValue)
        }
    }

    private val table = JBTable(packageTableModel).apply {
        cellSelectionEnabled = true
        addPropertyChangeListener(_propertyChangeListener)
    }

    private var lastRow = packages.size - 1

    fun createTable(): Component {
        return ToolbarDecorator.createDecorator(table).apply {
            setAddAction {
                //add a row
                lastRow = lastRow.inc()
                packageTableModel.addRow("", lastRow)
            }

            setRemoveAction {
                //remove a row
                packageTableModel.removeRow(0, lastRow, table.selectedRow)
                lastRow = lastRow.dec()
            }
        }.createPanel()
    }

    fun disposeResources() {
        table.removePropertyChangeListener(_propertyChangeListener)
    }

}

class PackageTableModel(
    private val packages: MutableList<String>,
    private val packageTableListener: PackageTable.PackageTableListener
) : AbstractTableModel() {
    override fun getRowCount(): Int {
        return packages.size
    }

    override fun getColumnCount(): Int {
        return 1 //only 1 column
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        return packages.elementAtOrElse(rowIndex) { "" }
    }

    override fun getColumnName(column: Int): String {
        return MessageBundle.get("inspection.package.column")
    }

    override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
        return true
    }

    override fun addTableModelListener(l: TableModelListener?) {
        super.addTableModelListener(l)
    }

    override fun removeTableModelListener(l: TableModelListener?) {
        super.removeTableModelListener(l)
    }

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
        packages[rowIndex] = aValue.toString()
        packageTableListener.onPackagesChanged(packages.toSet())
    }

    fun addRow(value: String, rowIndex: Int) {
        packages.add(rowIndex, value)
        setValueAt(value, rowIndex, 1)
    }

    fun removeRow(firstRow: Int, lastRow: Int, rowIndex: Int) {
        packages.removeAt(rowIndex)
        packageTableListener.onPackagesChanged(packages.toSet())
        fireTableRowsDeleted(firstRow, lastRow)
    }

    fun updateRow(rowIndex: Int, value: String) {
        setValueAt(value, rowIndex, 1)
    }
}