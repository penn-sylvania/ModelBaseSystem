package view;

import javax.swing.*;

/**
 *  Вспомогательный класс, содержащий методы для удобства работы с боксами
 */
public class BoxLayoutUtils {

    /**
     * Возвращает панель с установленным вертикальным блочным расположением.
     */
    public static JPanel createVerticalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    /**
     * Возвращает панель с установленным горизонтальным блочным расположением.
     */
    public static JPanel createHorizontalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }

    /**
     * Задает единое выравнивание по оси X для группы компонентов.
     * @param alignment флаг выравнивания
     * @param cs массив компонентов
     */
    public static void setGroupAlignmentX(float alignment, JComponent... cs) {
        for (JComponent c : cs) {
            c.setAlignmentX(alignment);
        }
    }

    /**
     * Задает единое выравнивание по оси Y для группы компонентов
     * @param alignment флаг выравнивания
     * @param cs массив компонентов
     */
    public static void setGroupAlignmentY(float alignment, JComponent... cs) {
        for (JComponent c : cs) {
            c.setAlignmentY(alignment);
        }
    }

}
