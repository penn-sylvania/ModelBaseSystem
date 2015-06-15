package view;

import model.data.Name;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SetDataPanel extends JPanel {

    private JLabel[] labels;
    private JTextField[] textFields;
    private int size;
    private Map<Name, Double> k;

    public void build(Map<Name, Double> k) {
        clear();

        this.k = k;
        this.size = k.size();

        labels = new JLabel[size];
        textFields = new JTextField[size];
        Object[] kKeys = k.keySet().toArray();
        for(int i = 0; i < size; i++) {
            labels[i] = new JLabel("<html>" + kKeys[i].toString() + "</html>");
            textFields[i] = new JTextField(10);
            textFields[i].setText(Double.toString(k.get(kKeys[i])));
        }

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c =  new GridBagConstraints();
        setLayout(gbl);

        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill   = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth  = 1;
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(10, 10, 0, 0);
        c.weightx = 0.5;
        c.weighty = 0.0;

        for(int i = 0; i < size; i++) {
            gbl.setConstraints(labels[i], c);
            add(labels[i]);
            c.ipadx = 0;
            c.gridwidth  = GridBagConstraints.REMAINDER;
            gbl.setConstraints(textFields[i], c);
            add(textFields[i]);
            c.gridwidth  = 1;
        }
        validate();
        repaint();
    }

    public void clear() {
        for(int i = 0; i < size; i++) {
            remove(labels[i]);
            remove(textFields[i]);
        }
    }

    public Map<Name, Double> getData() {
        Object[] kKeys = k.keySet().toArray();
        for(int i = 0; i < size; i++) {
            k.put((Name)kKeys[i], Double.valueOf(textFields[i].getText()));
        }
        return k;
    }

}
