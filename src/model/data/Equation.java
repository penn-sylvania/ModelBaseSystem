package model.data;

import model.parser.Parser;
import model.parser.ParserException;

import java.io.Serializable;
import java.util.Map;

public class Equation implements Serializable {

    private Parser parser;

    private String exp;

    public Equation(Parser parser) {
        this.parser = parser;
        exp = parser.getExp();
    }

    public Double evaluate(Map<Name, Double> k, double x, double... y) {
        try {
            return parser.evaluate(x, y);
        } catch (ParserException e) {
            System.out.println("ParserException: " + e);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: ");
        }
        return null;
    }

    @Override
    public String toString() {
        return exp;
    }

}
