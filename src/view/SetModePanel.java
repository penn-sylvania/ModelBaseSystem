package view;

import controller.Controller;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SetModePanel extends JPanel {

    private final MainFrame mainFrame;
    private final Controller controller;

    public SetModePanel(final MainFrame mainFrame, final Controller controller) {
        this.mainFrame = mainFrame;
        this.controller = controller;

        setLayout(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(5, 0, 0, 17));
        JLabel label = new JLabel("Выберите режим работы программы:");
        JLabel useMode = new ClickableLabel(MainFrame.USE_TITLE, MainFrame.Mode.USE);
        JLabel constructMode = new ClickableLabel(MainFrame.CONSTRUCT_TITLE, MainFrame.Mode.CONSTRUCT);
        JLabel editMode = new ClickableLabel(MainFrame.EDIT_TITLE, MainFrame.Mode.EDIT);

        grid.add(label);
        grid.add(useMode);
        grid.add(constructMode);
        grid.add(editMode);

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        //JButton help = new JButton("Справка");
        JButton exit = new JButton("Выход");
        //flow.add(help);
        flow.add(exit);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
            }
        });

        add(grid);
        add(flow, BorderLayout.SOUTH);
    }

    class ClickableLabel extends JLabel {

        private final Border border = BorderFactory.createSoftBevelBorder(SoftBevelBorder.RAISED);
        private final ImageIcon icon = new ImageIcon(SetModePanel.class.getResource("images/img.png"));

        public ClickableLabel(String text, final MainFrame.Mode mode) {
            super();
            setText(text);
            setIcon(icon);
            setIconTextGap(5);
            setOpaque(true);
            setBorder(border);
            setBackground(Color.WHITE);
            setHorizontalAlignment(LEFT);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    mainFrame.setMode(mode);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseClicked(e);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseClicked(e);
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            });
        }

    }

}
