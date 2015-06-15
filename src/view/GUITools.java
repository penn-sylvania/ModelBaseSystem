package view;


import javax.swing.*;
import java.awt.*;

public class GUITools {

    /**
     *  Принимает массив ссылок на кнопки JButton и придает им нужный
     *  отступ от границ слева и справа.
     */
    public static void createRecommendedMargin(JButton[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            Insets margin = buttons[i].getMargin(); //  расстояние от текста до границ кнопки
            margin.left = 12;
            margin.right = 12;
            buttons[i].setMargin(margin);
        }
    }

    /**
     *  Придает группе компонентов одинаковые размеры (минимальные,
     *  предпочтительные и максимальные). Компоненты принимают размер самого
     *  большого (по ширине) компонента в группе.
     *  @param components
     */
    public static void makeSameSize(JComponent[] components) {
        // получение ширины компонентов
        int[] sizes = new int[components.length];
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = components[i].getPreferredSize().width;
        }
        // определение максимального размера
        int maxSizePos = maximumElementPosition(sizes);
        Dimension maxSize = components[maxSizePos].getPreferredSize();
        // придание одинаковых размеров
        for (int i = 0; i < components.length; i++) {
            components[i].setPreferredSize(maxSize);
            components[i].setMinimumSize(maxSize);
            components[i].setMaximumSize(maxSize);
        }
    }

    /**
     *  Исправляет размеры текстового поля JTextField.
     *  @param field
     */
    public static void fixTextFieldSize(JTextField field) {
        Dimension size = field.getPreferredSize();
        // чтобы текстовое поле по-прежнему могло увеличивать свой размер в длину
        size.width = field.getMaximumSize().width;
        // теперь текстовое поле не станет выше своей оптимальной высоты
        field.setMaximumSize(size);
    }

    /**
     * Вспомогательный метод для определения позиции максимального элемента массива.
     * @param array массив ширин компонентов
     * @return максимальное значение
     */
    private static int maximumElementPosition(int[] array) {
        int maxPos = 0;
        for (int i=1; i < array.length; i++) {
            if (array[i] > array [maxPos]) maxPos = i;
        }
        return maxPos;
    }

}
