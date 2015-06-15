package model.parser;

import model.data.Name;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Parser implements Serializable {

    //  Тип лексемы
    enum TokenType {
        NONE,           //  FAIL
        DELIMITER,      //  Разделитель(+-*/^=, ")", "(" )
        VARIABLE,       //  Переменная
        NUMBER,         //  Число
        FUNCTION        //  Функция
    }

    //  Функция
    public enum Function {
        COS("cos"),
        ARCCOS("arccos"),
        SIN("sin"),
        ARCSIN("arcsin"),
        TG("tg"),
        ARCTG("arctg"),
        CTG("ctg"),
        SQRT("sqrt"),
        EXP("exp"),
        LOG("log"),
        LOG10("log10");

        private String desc;
        Function(String desc) {
            this.desc = desc;
        }
        @Override
        public String toString() {
            return desc;
        }
    }

    //  Тип синтаксической ошибки
    enum ErrorType {
        SYNTAXERROR("Syntax error"),                //  Синтаксическая ошибка (10 + 5 6 / 1)
        UNBALPARENS("Unbalanced Parentheses"),      //  Несовпадение количества открытых и закрытых скобок
        NOEXP("No Expression Present"),             //  Отсутствует выражение при запуске анализатора
        DIVBYZERO("Division by zero");              //  Ошибка деления на ноль

        private String desc;
        ErrorType(String desc) {
            this.desc = desc;
        }
    }

    //  Лексема, определяющая конец выражения
    private static final String EOF = "\0";

    private String exp;     //  Ссылка на строку с выражением
    private int expIdx;     //  Текущий индекс в выражении
    private String token;   //  Сохранение текущей лексемы
    private TokenType tokType;    //  Сохранение типа лексемы

    //  Переменные
    private Map<Name, Double> k;
    private Map<String, Double> vars = new HashMap<String, Double>();

    public Parser(String expStr, Map<Name, Double> k) {
        exp = expStr;
        this.k = k;
    }

    /**
     * @return символьное представление выражения
     */
    public String getExp() {
        return exp;
    }

    /**
     * Точка входа анализатора
     * @param x
     * @param y
     * @return результат просчета
     * @throws ParserException
     */
    public double evaluate(double x, double ... y) throws ParserException {
        double result;

        for(Map.Entry entry : k.entrySet()) {
            vars.put(((Name)entry.getKey()).shortName, (Double)entry.getValue());
        }
        vars.put("x", x);
        vars.put("X", x);
        if(y.length == 1) {
            vars.put("y", y[0]);
            vars.put("Y", y[0]);
        } else {
            for (int i = 0; i < y.length; i++) {
                vars.put("y" + (i + 1), y[i]);
                vars.put("Y" + (i + 1), y[i]);
            }
        }

        expIdx = 0;
        getToken("evaluate");

        if(token.equals(EOF)) {
            handleErr(ErrorType.NOEXP);   //  Нет выражения
        }

        //  Анализ и вычисление выражения
        result = evalExp2();

        if(!token.equals(EOF)) {  //  Последняя лексема должна быть EOF
            handleErr(ErrorType.SYNTAXERROR);
        }
        return result;
    }

    @Override
    public String toString(){
        return "exp = " + exp +
               ";  expIdx = " + expIdx +
               ";  token = " + token +
               ";  tokType = " + tokType;
    }

    /**
     * Получить следующую лексему
     */
    private void getToken(String method) {
        tokType = TokenType.NONE;
        token = "";

        //  Проверка на окончание выражения
        if(expIdx == exp.length()) {
            token = EOF;
            return;
        }
        //  Проверка на пробелы, если есть пробел - игнорируем его.
        while(expIdx < exp.length() && Character.isWhitespace(exp.charAt(expIdx))) {
            ++expIdx;
        }
        //  Проверка на окончание выражения
        if(expIdx == exp.length()) {
            token = EOF;
            return;
        }
        if(isDelim(exp.charAt(expIdx))) {        //  оператор
            token += exp.charAt(expIdx);
            expIdx++;
            tokType = TokenType.DELIMITER;
        } else if(Character.isLetter(exp.charAt(expIdx))){        //  переменная
            while(!isDelim(exp.charAt(expIdx))) {
                token += exp.charAt(expIdx);
                expIdx++;
                if(expIdx >= exp.length()) {
                    break;
                }
            }
            tokType = TokenType.VARIABLE;
            if(expIdx < exp.length() && exp.charAt(expIdx)=='(') {
                tokType = TokenType.FUNCTION;
            }
        } else if (Character.isDigit(exp.charAt(expIdx))){        //  число
            while(!isDelim(exp.charAt(expIdx))) {
                token += exp.charAt(expIdx);
                expIdx++;
                if(expIdx >= exp.length()) {
                    break;
                }
            }
            tokType = TokenType.NUMBER;
        } else {      // неизвестный символ
            token = EOF;
            return;
        }
    }

    /**
     * Возвращает true если c является раздилителем
     */
    private boolean isDelim(char c) {
        if((" +-/*%^=()".indexOf(c)) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Сложить или вычесть два терма
     * @return
     * @throws ParserException
     */
    private double evalExp2() throws ParserException {
        char op;
        double result;
        double partialResult;

        result = evalExp3();
        while((op = token.charAt(0)) == '+' || op == '-') {
            getToken("evalExp2");
            partialResult = evalExp3();
            switch(op) {
                case '-':
                    result -= partialResult;
                    break;
                case '+':
                    result += partialResult;
                    break;
            }
        }
        return result;
    }

    /**
     * Умножить или разделить два фактора
     * @return
     * @throws ParserException
     */
    private double evalExp3() throws ParserException {
        char op;
        double result;
        double partialResult;

        result = evalExp4();
        while((op = token.charAt(0)) == '*' || op == '/' | op == '%') {
            getToken("evalExp3");
            partialResult = evalExp4();
            switch(op) {
                case '*':
                    result *= partialResult;
                    break;
                case '/':
                    if(partialResult == 0.0) {
                        handleErr(ErrorType.DIVBYZERO);
                    }
                    result /= partialResult;
                    break;
                case '%':
                    if(partialResult == 0.0) {
                        handleErr(ErrorType.DIVBYZERO);
                    }
                    result %= partialResult;
                    break;
            }
        }
        return result;
    }

    /**
     * Выполнить возведение в степень
     * @return
     * @throws ParserException
     */
    private double evalExp4() throws ParserException {
        double result;
        double partialResult;
        double ex;
        int t;

        result = evalExp5();
        if(token.equals("^")) {
            getToken("evalExp4");
            partialResult = evalExp4();
            result = Math.pow(result, partialResult);
        }
        return result;
    }

    /**
     * Определить унарные + или -
     * @return
     * @throws ParserException
     */
    private double evalExp5() throws ParserException {
        double result;

        String op;
        op = " ";

        if((tokType == TokenType.DELIMITER) && token.equals("+") || token.equals("-")) {
            op = token;
            getToken("evalExp5");
        }
        result = evalExp6();
        if(op.equals("-")) {
            result =  -result;
        }
        return result;
    }

    /**
     * Обработать выражение в скобках
     * @return
     * @throws ParserException
     */
    private double evalExp6() throws ParserException {
        double result;

        if(token.equals("(")) {
            getToken("evalExp6 a");
            result = evalExp2();
            if(!token.equals(")")) {
                handleErr(ErrorType.UNBALPARENS);
            }
            getToken("evalExp6 b");
        } else {
            result = evalExp7();
        }
        return result;
    }

    /**
     * Вычисление функции
     * @return
     * @throws ParserException
     */
    private double evalExp7() throws ParserException {
        double result = 0.0;

        if(tokType == TokenType.FUNCTION) {
            String func = token;
            getToken("evalExp7 a");
            double resInParentheses = evalExp2();
            if(func.equals(Function.COS)) {
                result = Math.cos(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.ARCCOS)) {
                result = Math.acos(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.SIN)) {
                result = Math.sin(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.ARCSIN)) {
                result = Math.asin(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.TG)) {
                result = Math.tan(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.ARCTG)) {
                result = Math.atan(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.CTG)) {
                result = 1.0 / Math.tan(Math.toRadians(resInParentheses));
            } else if(func.equals(Function.SQRT)) {
                result = Math.sqrt(resInParentheses);
            } else if(func.equals(Function.EXP)) {
                result = Math.exp(resInParentheses);
            } else if(func.equals(Function.LOG)) {
                result = Math.log(resInParentheses);
            } else if(func.equals(Function.LOG10)) {
                result = Math.log10(resInParentheses);
            }
        } else {
            result = atom();
        }
        return result;
    }

    /**
     * Получить значение числа или переменной
     * @return
     * @throws ParserException
     */
    private double atom() throws ParserException {
        double result = 0.0;

        switch(tokType){
            case NUMBER:
                try {
                    result = Double.parseDouble(token);
                } catch(NumberFormatException exc) {
                    handleErr(ErrorType.SYNTAXERROR);
                }
                getToken("atom a");
                break;
            case VARIABLE:
                //  Возврат значения переменной
                result = vars.get(token);
                getToken("atom b");
                break;
            default:
                handleErr(ErrorType.SYNTAXERROR);
                break;
        }
        return result;
    }

    /**
     * Кинуть ошибку
     * @param error
     * @throws ParserException
     */
    private void handleErr(ErrorType error) throws ParserException{
        throw new ParserException(error.desc);
    }

}