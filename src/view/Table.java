package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.NoRouteToHostException;

public class Table extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 500;
    private static final String TITLE = "Таблица результатов просчета";

    public Table(Object[][] data, String task) {
        super(TITLE);
        setLayout(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);

        int size = data[0].length;
        Object[] titles = new Object[size];
        titles[0] = "x";
        if(size > 2) {
            for(int i = 1; i < size; i++) {
                titles[i] = "y" + i + "(x)";
            }
        } else {
            titles[1] = "y(x)";
        }

        tableModel.setDataVector(data, titles);
        JScrollPane scrollPane = new JScrollPane(table);

        getContentPane().add(new JLabel(task), BorderLayout.NORTH);
        getContentPane().add(scrollPane);

        pack();
        setBounds(0, 0, WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);

    }

}
