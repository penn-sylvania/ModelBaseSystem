package view;

import controller.Controller;
import controller.DuplicateModelNameException;
import controller.EmptyExpListException;
import controller.IllegalVarNameException;
import model.data.Name;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import java.util.Vector;

public class ConstructPanel extends JPanel {

    /**
     *  Перечесление типов поддерживаемых данных, которые описывают модели
     */
    public enum DataType {
        DE1("дифф. уравнения 1-го порядка"),
        DE2("дифф. уравнения 2-го порядка");

        private String describe;

        private DataType(String describe) {
            this.describe = describe;
        }

        @Override
        public String toString() {
            return describe;
        }
    }

    private static final String[] ITEMS = new String[] { DataType.DE1.toString(),
                                                         DataType.DE2.toString() };

    private DataType currentDataType = DataType.DE1;

    private final MainFrame mainFrame;
    private final Controller controller;

    private final JButton saveButton, exitButton;
    private final JTextField nameField;
    private final JTextArea descArea;
    private final JComboBox<String> comboBox;
    private final DefaultComboBoxModel<String> comboBoxModel;
    private final JTable kTable, expTable;
    private final DefaultTableModel kTableModel, expTableModel;

    public ConstructPanel(final MainFrame mainFrame, final Controller controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setLayout(new BorderLayout());

        //  Создание основной панели
        JPanel mainBox = BoxLayoutUtils.createVerticalPanel();

        //  Создание панели для задания имени и описания
        JPanel vertBox1 = BoxLayoutUtils.createVerticalPanel();
        JPanel horizBox11 = BoxLayoutUtils.createHorizontalPanel();
        JLabel nameLabel = new JLabel("Имя модели");
        nameField = new JTextField();
        horizBox11.add(nameLabel);
        horizBox11.add(Box.createHorizontalStrut(12));
        horizBox11.add(nameField);
        JPanel horizBox12 = BoxLayoutUtils.createHorizontalPanel();
        JLabel descLabel = new JLabel("Описание модели");
        descArea = new JTextArea();
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setRows(5); //!!!!!!!!!!!!!!!!!!!!
        JScrollPane scrollArea = new JScrollPane(descArea);
        scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        horizBox12.add(descLabel);
        horizBox12.add(Box.createHorizontalStrut(12));
        horizBox12.add(scrollArea);
        JPanel flowBox1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        JLabel label1 = new JLabel("<html>Задайте переменные модели</html>"); // выровнять влево, поменять название и текст
        JButton addVar = new JButton("Добавить");
        addVar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kTableModel.addRow(new Vector());
            }
        });
        flowBox1.add(label1);
        flowBox1.add(addVar);

        BoxLayoutUtils.setGroupAlignmentX(Component.LEFT_ALIGNMENT, horizBox11, horizBox12, flowBox1, vertBox1);
        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, nameField, scrollArea, nameLabel, descLabel);
        BoxLayoutUtils.setGroupAlignmentX(LEFT_ALIGNMENT, label1, addVar);
        GUITools.makeSameSize(new JComponent[]{nameLabel, descLabel});
        GUITools.createRecommendedMargin(new JButton[]{addVar});
        GUITools.fixTextFieldSize(nameField);

        vertBox1.add(horizBox11);
        vertBox1.add(Box.createVerticalStrut(12));
        vertBox1.add(horizBox12);
        vertBox1.add(Box.createVerticalStrut(12));
        vertBox1.add(flowBox1);

        //  Создание таблицы переменных
        kTableModel = new VarsTableModel();
        kTable = new JTable(kTableModel);
        kTable.getTableHeader().setReorderingAllowed(false);
        kTable.getColumnModel().getColumn(0).setMaxWidth(100);
        kTable.getColumnModel().getColumn(2).setMaxWidth(150);
        kTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        kTable.getColumnModel().getColumn(3).setMaxWidth(100);
        kTable.getColumnModel().getColumn(3).setMinWidth(100);
        kTable.getColumnModel().getColumn(3).setResizable(false);
        kTable.getColumnModel().getColumn(3).setCellRenderer(new MyButtonRenderer());
        kTable.getColumnModel().getColumn(3).setCellEditor(new MyButtonRenderer());
        JScrollPane scrollPane1 = new JScrollPane(kTable);

        //  Создание горизонтального бокса
        JPanel horizBox = BoxLayoutUtils.createHorizontalPanel();
        horizBox.setPreferredSize(new Dimension(mainBox.getPreferredSize().width, 200));

        //  Создание второй информационной панели
        JPanel vertBox2 = BoxLayoutUtils.createVerticalPanel();
        vertBox2.setMaximumSize(new Dimension(horizBox.getPreferredSize().width / 2, 200));
        JLabel label2 = new JLabel("<html>Задайте уравнения модели. Выберите тип уравнений из списка.</html>");
        comboBoxModel = new DefaultComboBoxModel<String>(ITEMS);
        comboBox = new JComboBox<String>(comboBoxModel);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDataType(DataType.values()[comboBox.getSelectedIndex()]);
            }
        });
        comboBox.setMaximumSize(comboBox.getPreferredSize());
        JButton addExp = new JButton("Добавить");
        addExp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expTableModel.addRow(new Object[]{""});
            }
        });

        vertBox2.add(label2);
        vertBox2.add(Box.createVerticalStrut(12));
        vertBox2.add(comboBox);
        vertBox2.add(Box.createVerticalStrut(12));
        vertBox2.add(addExp);
        BoxLayoutUtils.setGroupAlignmentX(LEFT_ALIGNMENT, label2, comboBox, addExp);

        //  Создание таблицы уравнений
        expTableModel = new ExpTableModel();
        expTable = new JTable(expTableModel);
        expTable.getTableHeader().setReorderingAllowed(false);
        expTable.getTableHeader().setPreferredSize(new Dimension(0, 0));
        expTable.getColumnModel().getColumn(0).setMinWidth(100);
        expTable.getColumnModel().getColumn(0).setMaxWidth(100);
        expTable.getColumnModel().getColumn(0).setResizable(false);
        expTable.getColumnModel().getColumn(0).setCellRenderer(new MyCellRenderer(DataType.DE1));
        expTable.getColumnModel().getColumn(2).setMaxWidth(100);
        expTable.getColumnModel().getColumn(2).setMinWidth(100);
        expTable.getColumnModel().getColumn(2).setResizable(false);
        expTable.getColumnModel().getColumn(2).setCellRenderer(new MyButtonRenderer());
        expTable.getColumnModel().getColumn(2).setCellEditor(new MyButtonRenderer());
        JScrollPane scrollPane2 = new JScrollPane(expTable);
        scrollPane2.setMinimumSize(new Dimension(400, 100));

        horizBox.add(vertBox2);
        horizBox.add(Box.createHorizontalStrut(12));
        horizBox.add(scrollPane2);
        BoxLayoutUtils.setGroupAlignmentY(TOP_ALIGNMENT, vertBox2, scrollPane2);

        //  Создание нижней панели кнопок
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        saveButton = new JButton("Сохранить модель");
        exitButton = new JButton("Отмена");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if(controller.saveModel(Controller.Action.SAVE,
                                            new Name(nameField.getText(), descArea.getText()),
                                            currentDataType,
                                            kTableModel.getDataVector(),
                                            expTableModel.getDataVector())) {
                        new DialogFrame(mainFrame, "Сохранение модели", true);
                    }
                } catch (IllegalArgumentException e1) {
                    String str = "Введите название модели!";
                    JOptionPane.showMessageDialog(null, str);
                } catch (DuplicateModelNameException e2) {
                    String str = "Модель с заданным именем \"" + e2.getName() + "\" уже существует.";
                    JOptionPane.showMessageDialog(null, str);
                } catch (IllegalVarNameException e3) {
                    String str = "Название переменной " + e3.getVar() + " в строке " + (e3.getIndex() + 1) +
                            " совпадает с названием функции, поддерживаемой программой. " +
                            "Пожалуйста, введите другое название.";
                    JOptionPane.showMessageDialog(null, str);
                } catch (EmptyExpListException e4) {
                    String str = "Ни одно уравнение не задано!";
                    JOptionPane.showMessageDialog(null, str);
                }
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setMode(MainFrame.Mode.SET_MODE);
            }
        });

        buttonsPanel.add(saveButton);
        buttonsPanel.add(exitButton);

        //  Укладываем все в главный бокс
        mainBox.add(vertBox1);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(scrollPane1);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(horizBox);
        mainBox.add(Box.createVerticalStrut(17));
        mainBox.add(buttonsPanel);
        BoxLayoutUtils.setGroupAlignmentX(LEFT_ALIGNMENT, vertBox1, scrollPane1, horizBox, buttonsPanel);

        add(mainBox, BorderLayout.CENTER);

        //  Добавление слушателя, обновляющего панель при установке видимости
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });

        revalidate();
    }

    /**
     *  Метод, устанавливающий рендерер для выбранного типа данных (уравнений)
     */
    private void setDataType(DataType dataType) {
        currentDataType = dataType;
        expTable.getColumnModel().getColumn(0).setCellRenderer(new MyCellRenderer(currentDataType));
        expTable.repaint();
    }

    /**
     *  Метод, приводящий панель в первоначальный вид
     */
    public void refresh() {
        nameField.setText("");
        descArea.setText("");

        kTableModel.setRowCount(0);
        kTableModel.setRowCount(2);

        expTableModel.setRowCount(0);
        expTableModel.setRowCount(1);

        comboBox.setSelectedIndex(0);
    }

    /**
     *  Класс, описывающий модель для таблицы переменных
     */
    class VarsTableModel extends DefaultTableModel {

        public VarsTableModel() {
            super();

            addColumn("Название");
            addColumn("Описание");
            addColumn("Значение по умолчанию");
            addColumn("");

            setRowCount(2);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if(columnIndex == 2) {
                return Double.class;
            } else {
                return String.class;
            }
        }

    }

    /**
     *  Класс, описывающий модель для таблицы уравнений
     */
    class ExpTableModel extends DefaultTableModel {
        public ExpTableModel() {
            super();

            addColumn("Левая часть");
            addColumn("Правая часть");
            addColumn("");

            setRowCount(1);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0 ? false : true;
        }
    }

    /**
     *  Класс, описывающий рендерер для столбца с кнопками "Удалить"
     */
    class MyButtonRenderer extends JButton implements TableCellRenderer, TableCellEditor {

        private int selectedRow = 0;
        private DefaultTableModel tableModel;

        public MyButtonRenderer() {
            super("Удалить");
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tableModel.removeRow(selectedRow);
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column) {
            setFocusable(false);
            return this;
        }

        @Override
        public boolean stopCellEditing() { return true; }

        @Override
        public void cancelCellEditing() { }

        @Override
        public void addCellEditorListener(CellEditorListener l) { }

        @Override
        public void removeCellEditorListener(CellEditorListener l) { }

        @Override
        public Object getCellEditorValue() { return null; }

        @Override
        public boolean isCellEditable(EventObject anEvent) { return true; }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) { return true; }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     int row, int column) {
            selectedRow = row;
            tableModel = (DefaultTableModel)table.getModel();
            setFocusable(false);
            return this;
        }

    }

    /**
     *  Класс, описывающий рендерер для столбца с левой частью уравнения
     */
    class MyCellRenderer extends DefaultTableCellRenderer {

        private String str;

        public MyCellRenderer(DataType dataType) {
            switch (dataType) {
                case DE1:
                    str = "y'";
                    break;
                case DE2:
                    str = "y''";
                    break;
                default:
                    str = "";
                    break;
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(str + (row + 1) + "(x) = ");
            setFocusable(false);
            return this;
        }

    }

}
