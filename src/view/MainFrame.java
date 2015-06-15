package view;

import controller.Controller;
import model.models.ESModelLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

public class MainFrame extends JFrame {

    /**
     * Перечесление режимов работы программы.
     */
    enum Mode {
        SET_MODE,          //  выбор режима
        USE,               //  режим использования моделей
        CONSTRUCT,         //  режим конструирования моделей
        EDIT               //  режим редактирования моделей
    }

    private static final int WIDTH = 500;
    private static final int HEIGHT = 400;
    private static final int WIDTH_CHILD = 700;
    private static final int HEIGHT_CHILD = 600;
    private static String iconPath = "images/icon.png";

    private static final String SET_MODE_TITLE = "Система управления Базой Моделей";
    public static final String USE_TITLE = "Режим использования моделей";
    public static final String CONSTRUCT_TITLE = "Режим конструирования моделей";
    public static final String EDIT_TITLE = "Режим редактирования моделей";

    private final JPanel mainPanel;
    private final SetModePanel setModePanel;
    private final UsePanel usePanel;
    private final ConstructPanel constructPanel;
    private final EditPanel editPanel;
    private final CardLayout cardLayout;
    private final Controller controller;

    public MainFrame() {
        super();

        // загрузка базы моделей
        try {
            ESModelLoader.loadFromFile();
        } catch (ClassNotFoundException e) {
            ESModelLoader.defaultLoad();
            System.out.println("loading ClassNotFoundException");
            e.printStackTrace();
        } catch (ClassCastException e2) {
            ESModelLoader.defaultLoad();
            System.out.println("loading ClassCastException");
            e2.printStackTrace();
        } catch (IOException e3) {
            ESModelLoader.defaultLoad();
            System.out.println("loading IOException");
            e3.printStackTrace();
        }

        controller = new Controller();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        URL imgURL = SetModePanel.class.getResource(iconPath);
        ImageIcon icon = new ImageIcon(imgURL);
        setIconImage(icon.getImage());

        cardLayout = new CardLayout();
        mainPanel = new JPanel();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        setModePanel = new SetModePanel(this, controller);
        usePanel = new UsePanel(this, controller);
        constructPanel = new ConstructPanel(this, controller);
        editPanel = new EditPanel(this, controller);

        mainPanel.add(setModePanel, SET_MODE_TITLE);
        mainPanel.add(usePanel, USE_TITLE);
        mainPanel.add(constructPanel, CONSTRUCT_TITLE);
        mainPanel.add(editPanel, EDIT_TITLE);
        setContentPane(mainPanel);

        // сохранение базы моделей при выходе
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    ESModelLoader.saveInFile();
                } catch (IOException e2) {
                    System.out.println("saving");
                    e2.printStackTrace();
                }
                System.exit(0);
            }
        });

        pack();
        setMode(Mode.SET_MODE);
        setVisible(true);
    }

    public ConstructPanel getConstructPanel() {
        return constructPanel;
    }

    /**
     *  Метод, устанавливающий режим работы программы.
     */
    public void setMode(Mode mode) {
        switch(mode) {
            case SET_MODE:
                framePosition(WIDTH, HEIGHT);
                cardLayout.show(mainPanel, SET_MODE_TITLE);
                setResizable(false);
                setTitle(SET_MODE_TITLE);
                break;
            case USE:
                framePosition(WIDTH_CHILD, HEIGHT_CHILD);
                cardLayout.show(mainPanel, USE_TITLE);
                setResizable(true);
                setTitle(USE_TITLE);
                break;
            case CONSTRUCT:
                framePosition(WIDTH_CHILD, HEIGHT_CHILD);
                cardLayout.show(mainPanel, CONSTRUCT_TITLE);
                setResizable(true);
                setTitle(CONSTRUCT_TITLE);
                break;
            case EDIT:
                framePosition(WIDTH_CHILD, HEIGHT_CHILD);
                cardLayout.show(mainPanel, EDIT_TITLE);
                setResizable(true);
                setTitle(EDIT_TITLE);
                break;
            default:
                break;
        }
    }

    /**
     * Метод, устанавливающий размеры фрейма.
     * Размещает фрейм по центру экрана.
     * @param width
     * @param height
     */
    private void framePosition(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
    }

}
