package view;

import controller.Controller;
import controller.DuplicateModelNameException;
import controller.EmptyExpListException;
import controller.IllegalVarNameException;
import model.data.Equation;
import model.data.EquationSystem;
import model.data.IData;
import model.data.Name;
import model.models.ESModel;
import model.models.ESModelLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class EditPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Controller controller;

    private final JTextArea descArea;

    private final JComboBox taskList;
    private final MyComboBoxModel taskListModel;

    private final JTable kTable, expTable;
    private final DefaultTableModel kTableModel, expTableModel;

    public EditPanel(final MainFrame mainFrame, final Controller controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setLayout(new BorderLayout());

        // Создание верхней панели
        JPanel vertBox1 = BoxLayoutUtils.createVerticalPanel();
        JPanel horizBox11 = BoxLayoutUtils.createHorizontalPanel();
        JLabel nameLabel = new JLabel("Имя модели");

        taskListModel = new MyComboBoxModel(controller.getTasks());
        ESModelLoader.addObserver(taskListModel);
        taskList = new JComboBox(taskListModel);

        horizBox11.add(nameLabel);
        horizBox11.add(Box.createHorizontalStrut(12));
        horizBox11.add(taskList);
        JPanel horizBox12 = BoxLayoutUtils.createHorizontalPanel();
        JLabel descLabel = new JLabel("Описание модели");

        descArea = new JTextArea(getCurrentModelDescription());
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setRows(5);
        JScrollPane scrollArea = new JScrollPane(descArea);
        scrollArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        horizBox12.add(descLabel);
        horizBox12.add(Box.createHorizontalStrut(12));
        horizBox12.add(scrollArea);

        BoxLayoutUtils.setGroupAlignmentX(Component.LEFT_ALIGNMENT, horizBox11, horizBox12, vertBox1);
        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, taskList, scrollArea, nameLabel, descLabel);
        GUITools.makeSameSize(new JComponent[]{nameLabel, descLabel});

        vertBox1.add(horizBox11);
        vertBox1.add(Box.createVerticalStrut(12));
        vertBox1.add(horizBox12);
        vertBox1.add(Box.createVerticalStrut(12));


        JLabel label1 = new JLabel("Переменные:");

        //  Создание таблицы переменных
        kTableModel = new VarsTableModel();
        if(getCurrentModel() != null) {
            setCurrentDataVector(kTableModel);
        }
        kTable = new JTable(kTableModel);
        kTable.getTableHeader().setReorderingAllowed(false);
        kTable.getColumnModel().getColumn(0).setMaxWidth(100);
        kTable.getColumnModel().getColumn(2).setMaxWidth(150);
        kTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        JScrollPane scrollPane1 = new JScrollPane(kTable);


        JLabel label2 = new JLabel("Уравнения:");

        //  Создание таблицы уравнений
        expTableModel = new ExpTableModel();
        if(getCurrentModel() != null) {
            setCurrentDataVector(expTableModel);
        }
        expTable = new JTable(expTableModel);
        expTable.getTableHeader().setReorderingAllowed(false);
        expTable.getTableHeader().setPreferredSize(new Dimension(0, 0));
        expTable.getColumnModel().getColumn(0).setMinWidth(100);
        expTable.getColumnModel().getColumn(0).setMaxWidth(100);
        expTable.getColumnModel().getColumn(0).setResizable(false);
        JScrollPane scrollPane2 = new JScrollPane(expTable);
        scrollPane2.setMinimumSize(new Dimension(400, 100));

        //  Создание вертикального бокса с таблицами
        JPanel vertBox2 = BoxLayoutUtils.createVerticalPanel();
        vertBox2.add(label1);
        vertBox2.add(Box.createVerticalStrut(5));
        vertBox2.add(scrollPane1);
        vertBox2.add(Box.createVerticalStrut(12));
        vertBox2.add(label2);
        vertBox2.add(Box.createVerticalStrut(5));
        vertBox2.add(scrollPane2);
        vertBox2.add(Box.createVerticalStrut(12));

        BoxLayoutUtils.setGroupAlignmentX(Component.CENTER_ALIGNMENT, label1, label2, scrollPane1, scrollPane2);

        // создание нижней панели
        JButton deleteButton = new JButton("Удалить модель");
        JButton saveButton = new JButton("Сохранить изменения");
        JButton exitButton = new JButton("Назад");

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        flow.add(deleteButton);
        flow.add(saveButton);
        flow.add(exitButton);

        JPanel buildPanel = BoxLayoutUtils.createVerticalPanel();
        buildPanel.add(flow);

        add(vertBox1, BorderLayout.NORTH);
        add(vertBox2, BorderLayout.CENTER);
        add(buildPanel, BorderLayout.SOUTH);

        // Добавление слушателя для JComboBox (выбор модели)
        taskList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (taskListModel.getSize() > 0) {
                    controller.setTask(getCurrentTask());
                    descArea.setText(getCurrentModelDescription());
                    setCurrentDataVector(kTableModel);
                    setCurrentDataVector(expTableModel);
                }
            }
        });

        // Добавление слушателя, удаляющего модель из Базы Моделей
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentTask = getCurrentTask();
                controller.deleteModel(getCurrentModel());
                refresh();
                JOptionPane.showMessageDialog(null, "Модель \"" + currentTask +"\" была успешно удалена.");
            }
        });

        // Добавление слушателя, сохраняющего модель в Базе Моделей
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String currentTask = getCurrentTask();
                    if(controller.saveModel(Controller.Action.EDIT,
                                            new Name(getCurrentTask(), descArea.getText()),
                                            ConstructPanel.DataType.DE1,
                                            kTableModel.getDataVector(),
                                            expTableModel.getDataVector())) {
                        JOptionPane.showMessageDialog(null, "Модель \"" + currentTask +"\" была успешно сохранена.");
                    }
                } catch (IllegalVarNameException e2) {
                    String str = "Название переменной " + e2.getVar() + " в строке " + (e2.getIndex() + 1) +
                            " совпадает с названием функции, поддерживаемой программой. " +
                            "Пожалуйста, введите другое название.";
                    JOptionPane.showMessageDialog(null, str);
                } catch (DuplicateModelNameException e3) {
                    String str = "Модель с заданным именем \"" + e3.getName() + "\" уже существует.";
                    JOptionPane.showMessageDialog(null, str);
                } catch (EmptyExpListException e4) {
                    String str = "Ни одно уравнение не задано!";
                    JOptionPane.showMessageDialog(null, str);
                }
            }
        });

        // Добавление слушателя для выхода на главную
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setMode(MainFrame.Mode.SET_MODE);
            }
        });

        // Добавление слушателя, обновляющего панель при установке видимости
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
    }

    /**
     *  Метод, приводящий панель в первоначальный вид
     */
    private void refresh() {
        taskListModel.update(controller.getTasks());
        descArea.setText(getCurrentModelDescription());
        setCurrentDataVector(kTableModel);
        setCurrentDataVector(expTableModel);
    }

    /**
     * Вспомагательный метод
     * @return выбранная задача
     */
    private String getCurrentTask() {
        if(taskListModel.getSelectedItem() != null) {
            return taskListModel.getSelectedItem().toString();
        }
        return "";
    }

    /**
     * Вспомагательный метод
     * @return выбранная модель
     */
    private ESModel getCurrentModel() {
        return ESModelLoader.getModel(getCurrentTask());
    }

    /**
     * Вспомагательный метод
     * @return описание выбранной модели
     */
    private String getCurrentModelDescription() {
        return controller.getCurrentModelDescription(getCurrentTask());
    }

    /**
     * Вспомагательный метод
     * @return массив данных о переменных выбранной модели (для таблицы)
     */
    private Object[][] getCurrentModelVarsTable() {
        Object[] vars = controller.getData().entrySet().toArray();
        int size = vars.length;
        Object[][] data = new Object[size][3];
        for(int i = 0; i < size; i++) {
            Name name = (Name)((Map.Entry)vars[i]).getKey();
            data[i][0] = name.shortName;
            data[i][1] = name.longName;
            data[i][2] = ((Map.Entry)vars[i]).getValue();
        }

        return data;
    }

    /**
     * Вспомагательный метод
     * @return массив данных об уравнениях выбранной модели (для таблицы)
     */
    private Object[][] getCurrentModelExpTable() {
        List<Equation> equations = controller.getEquations();
        int size = equations.size();
        Object[][] data = new Object[size][2];
        for(int i = 0; i < size; i++) {
            data[i][0] = "y" + (i + 1) + "(x)";
            data[i][1] = equations.get(i);
        }

        return data;
    }

    /**
     * Вспомагательный метод
     * Заполняет текущими данными модель таблицы tableModel
     */
    private void setCurrentDataVector(DefaultTableModel tableModel) {
        if(tableModel instanceof VarsTableModel) {
            tableModel.setDataVector(getCurrentModelVarsTable(),
                    new Object[]{"Название", "Описание", "Значение по умолчанию"});
        } else if(tableModel instanceof ExpTableModel) {
            tableModel.setDataVector(getCurrentModelExpTable(),
                    new Object[] {"Левая часть", "Правая часть"});
        } else return;
    }

    /**
     *  Класс, описывающий модель для таблицы переменных
     */
    class VarsTableModel extends DefaultTableModel {

        public VarsTableModel() {
            super();

            setColumnCount(3);
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

            setColumnCount(2);
            setRowCount(1);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0 ? false : true;
        }
    }

}
