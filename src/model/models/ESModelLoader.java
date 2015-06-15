package model.models;

import model.data.Name;

import java.io.*;
import java.net.URL;
import java.util.*;

public class ESModelLoader {

    private static final String FILENAME = "model-base.out";
    private static final List<IModelObserver> OBSERVERS = new ArrayList<IModelObserver>();
    public static Set<ESModel> MODELS = new HashSet<ESModel>();

    public static void defaultLoad() {
        MODELS.add(new ESModel(
                new Name(
                        "Информационное противоборство (2 источника информации)",
                        "Модель позволяет получить динамику информационной борьбы во времени, " +
                                "а также определить ее конечный результат - «победителя» или «побежденного». " +
                                "Победителем считается тот, кто к моменту полного охвата изучаемой общности " +
                                "обеими видами информации сумел распространить свою информацию среди большего, " +
                                "чем соперник, числа членов общности."
                ),
                new HashMap<Name, Double>() {
                    {
                        put(new Name("n0", "численность социальной общности"), 20000.0);
                        put(new Name("a1", "интенсивность внешних источников канала №1"), 1.0);
                        put(new Name("a2", "интенсивность внешних источников канала №2"), 0.1);
                        put(new Name("b1", "интенсивность межличностных коммуникаций канала №1"), 0.002);
                        put(new Name("b2", "интенсивность межличностных коммуникаций канала №2"), 0.0045);
                    }
                },
                new String[]{
                        "(a1 + b1 * y1) * (n0 - y1 - y2)",
                        "(a2 + b2 * y2) * (n0 - y1 - y2)"
                })
        );

        MODELS.add(new ESModel(
                new Name(
                        "Закон охлаждения Ньютона",
                        "Скорость изменения температуры тела пропорциональна " +
                                "разности температур тела и окружающей среды. " +
                                "Температура тела уменьшается экспоненциально по мере охлаждения, " +
                                "приближаясь к температуре окружающей среды."
                ),
                new HashMap<Name, Double>() {
                    {
                        put(new Name("A", "температура окружающей среды"), 20.0);
                        put(new Name("K", "коэффициент теплопроводности"), 10.0);
                    }
                },
                new String[]{
                        "K * (A - y)"
                })
        );

        MODELS.add(new ESModel(
                new Name(
                        "Задача пловца",
                        "Пусть ширина реки равна W=2*a. Линии x=±a представляют собой берега реки, " +
                                "а ось y проходит через середину реки. Уравнение Vr=Vo*(1-x^2/a^2) " +
                                "позволяет проверить, что вода течет быстрее всего в центре, " +
                                "где скорость воды Vr=Vo, и что Vr=0 на каждом берегу реки. " +
                                "Предположим, что пловец начинает плыть из точки (-a, 0) на западном берегу " +
                                "и плывет на восток с постоянной скоростью Vs относительно воды. " +
                                "Данная модель позволяет определить траекторию пловца при пересечении реки."
                ),
                new HashMap<Name, Double>() {
                    {
                        put(new Name("Vo", "скорость воды в середине реки"), 2.0);
                        put(new Name("Vs", "скорость пловца"), 10.0);
                        put(new Name("W", "ширина реки"), 100.0);
                    }
                },
                new String[]{
                        "((Vo / Vs) * (1.0 - x^2 / (W^2 / 4.0)))"
                })
        );
    }

    public static Name[] getNames() {
        Name[] names = new Name[MODELS.size()];
        Object[] models = MODELS.toArray();
        for (int i = 0; i < names.length; i++){
            names[i] = ((ESModel)models[i]).name;
        }
        return names;
    }

    public static String[] getTasks() {
        Name[] names = ESModelLoader.getNames();
        String[] tasks = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            tasks[i] = names[i].shortName;
        }
        return tasks;
    }

    public static ESModel getModel(String task) {
        for (ESModel model : MODELS) {
            if(model.name.shortName.equals(task)) {
                return model;
            }
        }
        return null;
    }

    public static void addModel(ESModel model) {
        MODELS.add(model);
        for (IModelObserver observer : OBSERVERS) {
            observer.update(getTasks());
        }
    }

    public static void deleteModel(ESModel model) {
        MODELS.remove(model);
        for (IModelObserver observer : OBSERVERS) {
            observer.update(getTasks());
        }
    }

    public static boolean containsModel(ESModel model) {
        return MODELS.contains(model);
    }

    public static void addObserver(IModelObserver o) {
        OBSERVERS.add(o);
    }

    public static void saveInFile() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILENAME));
        out.writeObject(MODELS);
        out.close();
    }

    public static void loadFromFile() throws ClassNotFoundException, IOException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILENAME));
        MODELS = (HashSet)in.readObject();
        in.close();
    }

    public static void print() {
        for (ESModel model : MODELS) {
            System.out.println(model);
        }
    }

}