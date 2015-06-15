package controller;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.QuickChart;
import com.xeiam.xchart.SwingWrapper;
import model.data.Condition;
import model.data.Equation;
import model.data.EquationSystem;
import model.factory.MethodFactory;
import model.methods.IMethod;
import model.models.ESModel;
import model.models.ESModelLoader;
import model.data.Name;
import model.parser.Parser;
import view.ConstructPanel;

import java.util.*;

public class Controller {

    public enum Action {
        SAVE,
        EDIT
    }

    private static String[] TASKS;

    private IMethod method;
    private String currentTask;

    public Controller() {
        if (TASKS != null && TASKS.length > 0) {
            currentTask = TASKS[0];
            method = MethodFactory.getMethod(currentTask);
        } else {
            currentTask = "";
        }
    }

    /**
     *  ”станавливает текущую модель
     * @param task название модели
     */
    public void setTask(String task) {
        currentTask = task;
        method = MethodFactory.getMethod(currentTask);
    }

    /**
     * ¬ыполн€ет просчет
     * @param k карта переменных
     * @param xo начальное значение X
     * @param yo массив начальных значений Y
     * @param x0 лева€ граница интервала вычислени€
     * @param x1 права€ граница интервала вычислени€
     */
    public void onClickCalculate(Map<Name, Double> k, Double xo, Double[] yo, double x0, double x1) {
        method.setData(k);
        method.calculate(new Condition(xo, yo, x0, x1));
    }

    /**
     * ѕоказывает график на экране
     */
    public void onClickShowGraphic() {
        String[] seriesNames = new String[method.getSize()];
        if(seriesNames.length == 1) {
            seriesNames[0] = "y(x)";
        } else {
            for(int i = 0; i < seriesNames.length; i++) {
                seriesNames[i] = "y" + (i + 1) + "(x)";
            }
        }
        Chart chart = QuickChart.getChart(currentTask, "x", "y", seriesNames, getXData(), getYData());
        new SwingWrapper(chart).displayChart();
    }

    /**
     * —охран€ет модель в буфере Ѕазы ћоделей
     * @param action сохранение новой модели или отредактированной?
     * @param name название модели
     * @param dataType тип уравнений
     * @param kVector список переменных
     * @param expVector список уравнений
     * @return успешность сохранени€
     * @throws IllegalArgumentException
     * @throws DuplicateModelNameException
     * @throws IllegalVarNameException
     * @throws EmptyExpListException
     */
    public boolean saveModel(Action action, Name name, ConstructPanel.DataType dataType, List kVector, List expVector)
            throws IllegalArgumentException, DuplicateModelNameException, IllegalVarNameException, EmptyExpListException {

        ESModel model;

        if(name.shortName.trim().equals("")) {
            throw new IllegalArgumentException();
        }

        try {
            Map<Name, Double> k = new HashMap<Name, Double>();

            for (int i = 0; i < kVector.size(); i++) {
                Vector row = (Vector)kVector.get(i);
                if(row.get(0) != null) {
                    String n = row.get(0).toString();
                    if(!isVarValid(n)) {
                        throw new IllegalVarNameException(n, i);
                    }
                    String d = "";
                    double defValue = 0.0;
                    if(row.get(1) != null) {
                        d = row.get(1).toString();
                    }
                    if(row.get(2) != null) {
                        defValue = Double.parseDouble(row.get(2).toString());
                    }
                    Name varName = new Name(n, d);
                    k.put(varName, defValue);
                }
            }

            int size = expVector.size();
            String[] exp = new String[size * (dataType == ConstructPanel.DataType.DE1 ? 1 : 2)];

            boolean isEmptyExpList = true;
            for (int i = 0; i < size; i++) {
                Vector row = (Vector)expVector.get(i);
                if(row.get(1) != null) {
                    exp[i] = row.get(1).toString();
                    isEmptyExpList = false;
                }
            }
            if(isEmptyExpList) {
                throw new EmptyExpListException();
            }

            if(dataType == ConstructPanel.DataType.DE2) {
                for (int i = size; i < exp.length; i++) {
                    exp[i] = "y" + (i - size + 1);
                }
            }

            model = new ESModel(name, k, exp);
        } catch(NullPointerException e) {
            return false;
        } catch (IllegalVarNameException e) {
            throw new IllegalVarNameException(e.getVar(), e.getIndex());
        }

        if(action == Action.SAVE) {
            if(!ESModelLoader.containsModel(model)) {
                ESModelLoader.addModel(model);
                return true;
            } else {
                throw new DuplicateModelNameException(name.shortName);
            }
        } else if(action == Action.EDIT) {
            ESModelLoader.deleteModel(model);
            ESModelLoader.addModel(model);
            return true;
        }

        return false;

    }

    /**
     * ”дал€ет модель из буфера Ѕазы ћоделей
     * @param model модель
     */
    public void deleteModel(ESModel model) {
        ESModelLoader.deleteModel(model);
    }

    /**
     * @return выбранна€ модель
     */
    public ESModel getCurrentModel(String task) {
        return ESModelLoader.getModel(task);
    }

    /**
     * @return описание выбранной модели
     */
    public String getCurrentModelDescription(String task) {
        ESModel model = getCurrentModel(task);
        if(model != null) {
            return model.name.longName;
        }
        return "";
    }

    /**
     * @return карта переменных
     */
    public Map<Name, Double> getData() {
        if(method != null) {
            return method.getData();
        } else {
            return new LinkedHashMap<Name, Double>();
        }
    }

    /**
     * @return список уравнений
     */
    public List<Equation> getEquations() {
        ESModel model = getCurrentModel(currentTask);
        if(model != null) {
            return ((EquationSystem)model.getEquationSystem()).getEquations();
        } else {
            return new ArrayList<Equation>();
        }
    }

    /**
     * @return названи€ моделей Ѕазы ћоделей
     */
    public String[] getTasks() {
        TASKS = ESModelLoader.getTasks();
        return TASKS;
    }

    /**
     * @return массив значений X
     */
    public double[] getXData() {
        return method.getXData();
    }

    /**
     * @return массив значений Y
     */
    public double[][] getYData() {
        return method.getYData();
    }

    /**
     * ќпредел€ет, €вл€етс€ ли название переменной корректным
     * @param var название переменной
     * @return true or false
     */
    private boolean isVarValid(String var) {
        for(Parser.Function func : Parser.Function.values()) {
            if(var.equals(func.toString())) {
                return false;
            }
        }
        return true;
    }
}