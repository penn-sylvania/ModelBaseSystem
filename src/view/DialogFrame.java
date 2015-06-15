package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogFrame extends JDialog {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 200;

    private MainFrame mainFrame;

    public DialogFrame(final JFrame owner, String title, boolean modal) {
        super(owner, title, modal);
        mainFrame = (MainFrame)owner;

        setLayout(new GridBagLayout());

        JLabel label = new JLabel("<html>Модель успешно сохранена в Базе Моделей. " +
                "Вы можете проверить модель в режиме Использования моделей, " +
                "создать новую модель или перейти к выбору режима работы программы.</html>");
        JButton useModel = new JButton("Использовать модель");
        useModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setMode(MainFrame.Mode.USE);
                close();
            }
        });
        JButton newModel = new JButton("Создать новую модель");
        newModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getConstructPanel().refresh();
                close();
            }
        });
        JButton exit = new JButton("Выход");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.setMode(MainFrame.Mode.SET_MODE);
                close();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttonPanel.add(useModel);
        buttonPanel.add(newModel);
        buttonPanel.add(exit);

        JPanel box = BoxLayoutUtils.createVerticalPanel();
        box.add(label);
        box.add(buttonPanel);
        BoxLayoutUtils.setGroupAlignmentX(CENTER_ALIGNMENT, label, buttonPanel);

        Container main = getContentPane();
        main.add(box);

        pack();
        setBounds(0, 0, WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void close() {
        setVisible(false);
        dispose();
    }

}
