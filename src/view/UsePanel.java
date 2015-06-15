package view;

import controller.Controller;
import model.data.Name;
import model.methods.RungeKutta;
import model.models.ESModel;
import model.models.ESModelLoader;
import model.models.IModelObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class UsePanel extends JPanel {

    //  Начальные условия по умолчанию
    private static final String DEFAULT_XO = "0.0";
    private static final String DEFAULT_YO = "1.0";

    //  Интервал по умолчанию
    private static final String DEFAULT_X0 = "0.0";
    private static final String DEFAULT_X1 = "1.0";

    private final MainFrame mainFrame;
    private final Controller controller;

    private final JComboBox taskList;
    private final MyComboBoxModel taskListModel;
    private final JLabel desc;
    private final SetDataPanel setDataPanel;
    private final JPanel setConditionPanel;
    private final JButton showGraphicButton, showTableButton;

    private final JTextField xo, from, to;
    private final ArrayList<JTextField> yo = new ArrayList<JTextField>();
    private JLabel[] labelsYo;

    public UsePanel(final MainFrame mainFrame, final Controller controller) {
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
        desc = new JLabel(getCurrentModelDescription());
        horizBox12.add(descLabel);
        horizBox12.add(Box.createHorizontalStrut(12));
        horizBox12.add(desc);

        BoxLayoutUtils.setGroupAlignmentX(Component.LEFT_ALIGNMENT, horizBox11, horizBox12, vertBox1);
        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, taskList, desc, nameLabel, descLabel);
        GUITools.makeSameSize(new JComponent[]{nameLabel, descLabel});

        vertBox1.add(horizBox11);
        vertBox1.add(Box.createVerticalStrut(12));
        vertBox1.add(horizBox12);

        // Создание панели для задания значений переменных
        setDataPanel = new SetDataPanel();
        setDataPanel.build(controller.getData());

        // создание нижней панели
        final JButton calculateButton = new JButton("Просчет");
        showGraphicButton = new JButton("График");
        showGraphicButton.setEnabled(false);
        showTableButton = new JButton("Таблица");
        showTableButton.setEnabled(false);
        JButton exitButton = new JButton("Назад");

        xo = new JTextField(DEFAULT_XO, 6);
        from = new JTextField(DEFAULT_X0, 6);
        to = new JTextField(DEFAULT_X1, 6);

        setConditionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel cpLabel = new JLabel("Начальные условия: ");
        setConditionPanel.add(cpLabel);
        setConditionPanel.add(new JLabel("Xo: "));
        setConditionPanel.add(xo);
        buildYo();

        JPanel setIntervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel ipLabel = new JLabel("Интервал: ");
        setIntervalPanel.add(ipLabel);
        setIntervalPanel.add(new JLabel("X: от "));
        setIntervalPanel.add(from);
        setIntervalPanel.add(new JLabel(" до "));
        setIntervalPanel.add(to);

        GUITools.makeSameSize(new JComponent[]{cpLabel, ipLabel});

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        flow.add(calculateButton);
        flow.add(showGraphicButton);
        flow.add(showTableButton);
        flow.add(exitButton);

        JPanel buildPanel = BoxLayoutUtils.createVerticalPanel();
        buildPanel.add(setConditionPanel);
        buildPanel.add(Box.createVerticalStrut(12));
        buildPanel.add(setIntervalPanel);
        buildPanel.add(Box.createVerticalStrut(12));
        buildPanel.add(flow);

        add(vertBox1, BorderLayout.NORTH);
        add(setDataPanel, BorderLayout.CENTER);
        add(buildPanel, BorderLayout.SOUTH);

        // Добавление слушателя для JComboBox (выбор модели)
        taskList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGraphicButton.setEnabled(false);
                showTableButton.setEnabled(false);
                calculateButton.setEnabled(true);
                if (taskListModel.getSize() > 0) {
                    controller.setTask(getCurrentTask());
                    setDataPanel.build(controller.getData());
                    desc.setText(getCurrentModelDescription());

                    int prevEquationSize = labelsYo.length;
                    for(int i = 0; i < prevEquationSize; i++) {
                        setConditionPanel.remove(labelsYo[i]);
                        setConditionPanel.remove(yo.get(i));
                    }
                    buildYo();
                }
            }
        });

        // Добавленеие слушателя для JButton (просчет)
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double[] yoDouble = new Double[labelsYo.length];
                for (int i = 0; i < labelsYo.length; i++) {
                    yoDouble[i] = Double.valueOf(yo.get(i).getText());
                }
                controller.onClickCalculate(setDataPanel.getData(),
                        Double.valueOf(xo.getText()),
                        yoDouble,
                        Double.valueOf(from.getText()),
                        Double.valueOf(to.getText()));
                showGraphicButton.setEnabled(true);
                showTableButton.setEnabled(true);
                calculateButton.setEnabled(false);
            }
        });

        // Добавленеие слушателя для JButton (вывод графика)
        showGraphicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.onClickShowGraphic();
            }
        });

        // Добавленеие слушателя для JButton (вывод таблицы результатов просчета)
        showTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double[] xData = controller.getXData();
                double[][] yData = controller.getYData();
                int size = xData.length;
                int equationSize = controller.getEquations().size();
                Object[][] data = new Object[size][equationSize + 1];
                for(int i = 0; i < size; i++) {
                    data[i][0] = xData[i];
                    for(int j = 0; j < equationSize; j++) {
                        data[i][j + 1] = RungeKutta.r(yData[j][i], 5);
                    }
                }
                new Table(data, getCurrentTask());
            }
        });

        // Добавленеие слушателя для JButton (выход на главную)
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
     * Метод, создающий поля для ввода начальных значений Y
     */
    private void buildYo() {
        int equationSize = controller.getEquations().size();
        labelsYo = new JLabel[equationSize];
        for(int i = 0; i < equationSize; i++) {
            labelsYo[i] = new JLabel("Yo" + (equationSize == 1 ? "" : i + 1) + ": ");
            setConditionPanel.add(labelsYo[i]);
            yo.add(new JTextField(DEFAULT_YO, 6));
            setConditionPanel.add(yo.get(i));
        }
    }

    /**
     *  Метод, приводящий панель в первоначальный вид
     */
    private void refresh() {
        taskListModel.update(controller.getTasks());
        desc.setText(getCurrentModelDescription());
        setDataPanel.build(controller.getData());
        xo.setText(DEFAULT_XO);
        int prevEquationSize = labelsYo.length;
        for(int i = 0; i < prevEquationSize; i++) {
            setConditionPanel.remove(labelsYo[i]);
            setConditionPanel.remove(yo.get(i));
        }
        buildYo();
        from.setText(DEFAULT_X0);
        to.setText(DEFAULT_X1);
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
     * @return описание выбранной модели
     */
    private String getCurrentModelDescription() {
        ESModel model = ESModelLoader.getModel(getCurrentTask());
        if(model != null) {
            return "<html>" + model.name.longName + "</html>";
        }
        return "";
    }

}
