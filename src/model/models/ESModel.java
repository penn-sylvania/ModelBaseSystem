package model.models;

import model.data.*;
import model.parser.Parser;
import model.parser.ParserException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ESModel implements Serializable {

    public final Name name;
    public final Map<Name, Double> k;
    public final EquationSystem equationSystem;

    public ESModel(Name name, Map<Name, Double> k, final String[] exp) {
        this.name = name;
        this.k = k;
        List<Equation> equations = new ArrayList<Equation>();

        for (int i = 0; i < exp.length; i++) {
            Parser parser = new Parser(exp[i], k);
            equations.add(new Equation(parser));
        }
        equationSystem = new EquationSystem(equations, k);
    }

    public IData getEquationSystem() {
        return equationSystem;
    }

    @Override
    public boolean equals(Object name) {
        if(!(name instanceof ESModel)) {
            return false;
        }
        if(this == name) {
            return true;
        }
        if(this.name.equals(((ESModel)name).name)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * 17 + name.hashCode();
    }

    @Override
    public String toString() {
        return name + "\n" + k + "\n" + equationSystem;
    }

}
