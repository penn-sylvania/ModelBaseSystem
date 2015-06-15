package model.factory;

import model.methods.IMethod;
import model.methods.RungeKutta;
import model.models.ESModelLoader;
import model.data.EquationSystem;
import model.data.IData;

public class MethodFactory {

    public static IMethod getMethod(String task) {
        IData data = ESModelLoader.getModel(task).getEquationSystem();
        if(data instanceof EquationSystem){
            return new RungeKutta((EquationSystem)data);
        } else {
            return null;
        }
    }

}