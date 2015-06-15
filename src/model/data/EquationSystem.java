package model.data;

import java.util.List;
import java.util.Map;

public class EquationSystem implements IData {

    private int size;
    private List<Equation> equations;
    private Map<Name, Double> k;

    public EquationSystem(List<Equation> equations, Map<Name, Double> k) {
        this.equations = equations;
        this.k = k;
        this.size = equations.size();
    }

    public List<Equation> getEquations() {
        return equations;
    }

    public int getSize() {
        return size;
    }

    public double getResult(int i, double x, double... y) {
        return equations.get(i).evaluate(k, x, y);
    }

    public Map<Name, Double> getK() {
        return k;
    }

    public void setK(Map<Name, Double> k) {
        this.k = k;
    }

}
