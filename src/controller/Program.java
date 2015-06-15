package controller;

import model.models.ESModelLoader;
import view.MainFrame;

import java.awt.*;

public class Program {

    public static void main(String[] args) {
        EventQueue.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        new MainFrame();
                    }

                });
    }
}
