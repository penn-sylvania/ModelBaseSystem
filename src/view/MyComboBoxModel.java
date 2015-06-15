package view;

import model.models.ESModel;
import model.models.IModelObserver;

import javax.swing.*;

/**
 * Класс описывающий модель компонента JComboBox,
 * хранящего список имеющихся в Базе Моделей.
 * Также является наблюдателем, обновляющим список
 * при изменениях в Базе Моделей.
 */
public class MyComboBoxModel extends DefaultComboBoxModel implements IModelObserver {

    public MyComboBoxModel(String [] items) {
        super(items);
    }

    @Override
    public void update(String [] tasks) {
        removeAllElements();
        for (int i = 0; i < tasks.length; i++) {
            addElement(tasks[i]);
        }

        //addElement(model.name.shortName);
        //setSelectedItem(getElementAt(getSize() - 1));
    }

}
