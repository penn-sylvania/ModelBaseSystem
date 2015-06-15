package controller;


public class DuplicateModelNameException extends Exception {

    private String name;

    public DuplicateModelNameException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
