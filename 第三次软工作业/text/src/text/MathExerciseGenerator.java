package text;



import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathExerciseGenerator {
    private static final String EXERCISES_FILE = "Exercises.txt";
    private static final String ANSWERS_FILE = "Answers.txt";
    private static final String GRADE_FILE = "Grade.txt";

    static class Fraction {
        int integer;
        int numerator;
        int denominator;

        public Fraction(int integer, int numerator, int denominator) {
            this.integer = integer;
            this.numerator = numerator;
            this.denominator = denominator;
            simplify();
        }

        public Fraction(int integer) {
            this(integer, 0, 1);
        }

        public Fraction(int numerator, int denominator) {
            this(0, numerator, denominator);
        }

        private void simplify() {
            if (denominator == 0) {
                throw new ArithmeticException("分母不能为0");
            }

            if (denominator < 0) {
                numerator *= -1;
                denominator *= -1;
            }

            int totalNumerator = integer * denominator + numerator;
            integer = 0;
            numerator = totalNumerator;

            if (numerator == 0) {
                denominator = 1;
                return;
            }

            int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
            numerator /= gcd;
            denominator /= gcd;

            if (Math.abs(numerator) >= denominator) {
                integer = numerator / denominator;
                numerator = Math.abs(numerator % denominator);
            }
        }

        private int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }

        public Fraction add(Fraction other) {
            int num1 = this.integer * this.denominator + this.numerator;
            int den1 = this.denominator;
            int num2 = other.integer * other.denominator + other.numerator;
            int den2 = other.denominator;

            int newNum = num1 * den2 + num2 * den1;
            int newDen = den1 * den2;

            return new Fraction(0, newNum, newDen);
        }

        public Fraction subtract(Fraction other) {
            int num1 = this.integer * this.denominator + this.numerator;
            int den1 = this.denominator;
            int num2 = other.integer * other.denominator + other.numerator;
            int den2 = other.denominator;

            int newNum = num1 * den2 - num2 * den1;
            int newDen = den1 * den2;

            return new Fraction(0, newNum, newDen);
        }

        public Fraction multiply(Fraction other) {
            int num1 = this.integer * this.denominator + this.numerator;
            int den1 = this.denominator;
            int num2 = other.integer * other.denominator + other.numerator;
            int den2 = other.denominator;

            int newNum = num1 * num2;
            int newDen = den1 * den2;

            return new Fraction(0, newNum, newDen);
        }

        public Fraction divide(Fraction other) {
            int num1 = this.integer * this.denominator + this.numerator;
            int den1 = this.denominator;
            int num2 = other.integer * other.denominator + other.numerator;
            int den2 = other.denominator;

            if (num2 == 0) {
                throw new ArithmeticException("除数不能为0");
            }

            int newNum = num1 * den2;
            int newDen = den1 * num2;

            return new Fraction(0, newNum, newDen);
        }

        public boolean greaterOrEqual(Fraction other) {
            int num1 = this.integer * this.denominator + this.numerator;
            int den1 = this.denominator;
            int num2 = other.integer * other.denominator + other.numerator;
            int den2 = other.denominator;

            return (long) num1 * den2 >= (long) num2 * den1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Fraction fraction = (Fraction) obj;
            return integer == fraction.integer &&
                    numerator == fraction.numerator &&
                    denominator == fraction.denominator;
        }

        @Override
        public String toString() {
            if (numerator == 0) {
                return Integer.toString(integer);
            } else if (integer == 0) {
                return numerator + "/" + denominator;
            } else {
                return integer + "'" + numerator + "/" + denominator;
            }
        }

        public static Fraction parse(String s) {
            s = s.trim();
            if (s.contains("'")) {
                String[] parts = s.split("'");
                int integer = Integer.parseInt(parts[0]);
                String[] fracParts = parts[1].split("/");
                int numerator = Integer.parseInt(fracParts[0]);
                int denominator = Integer.parseInt(fracParts[1]);
                return new Fraction(integer, numerator, denominator);
            } else if (s.contains("/")) {
                String[] parts = s.split("/");
                int numerator = Integer.parseInt(parts[0]);
                int denominator = Integer.parseInt(parts[1]);
                return new Fraction(0, numerator, denominator);
            } else {
                return new Fraction(Integer.parseInt(s));
            }
        }
    }

    static class ExpressionResult {
        String expression;
        Fraction result;

        public ExpressionResult(String expression, Fraction result) {
            this.expression = expression;
            this.result = result;
        }
    }

    private static final Random random = new Random();

    public static void main(String[] args) {
        if (args.length >= 4 && args[0].equals("-e") && args[2].equals("-a")) {
            gradeExercises(args[1], args[3]);
            return;
        }

        int numQuestions = 10;
        Integer rangeLimit = null;

        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-n") && i + 1 < args.length) {
                try {
                    numQuestions = Integer.parseInt(args[i + 1]);
                    i += 2;
                } catch (NumberFormatException e) {
                    System.err.println("错误：-n参数必须是整数");
                    printUsage();
                    return;
                }
            } else if (args[i].equals("-r") && i + 1 < args.length) {
                try {
                    rangeLimit = Integer.parseInt(args[i + 1]);
                    if (rangeLimit < 1) {
                        System.err.println("错误：-r参数必须是大于等于1的整数");
                        printUsage();
                        return;
                    }
                    i += 2;
                } catch (NumberFormatException e) {
                    System.err.println("错误：-r参数必须是整数");
                    printUsage();
                    return;
                }
            } else {
                System.err.println("错误：无效的参数");
                printUsage();
                return;
            }
        }

        if (rangeLimit == null) {
            System.err.println("错误：必须提供-r参数指定范围");
            printUsage();
            return;
        }

        System.out.printf("正在生成%d道题目，范围为%d...%n", numQuestions, rangeLimit);
        List<String> exercises = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        Set<String> seenExpressions = new HashSet<>();

        while (exercises.size() < numQuestions) {
            ExpressionResult exprResult = generateExpression(rangeLimit, 3);
            String normalized = normalizeExpression(exprResult.expression);

            if (!seenExpressions.contains(normalized)) {
                seenExpressions.add(normalized);
                exercises.add(exprResult.expression + " =");
                
                // 使用相同的计算逻辑验证结果
                Fraction verifiedResult = calculateExpression(exprResult.expression);
                answers.add(verifiedResult.toString());
            }
        }

        try (BufferedWriter exerciseWriter = new BufferedWriter(new FileWriter(EXERCISES_FILE));
             BufferedWriter answerWriter = new BufferedWriter(new FileWriter(ANSWERS_FILE))) {

            for (String exercise : exercises) {
                exerciseWriter.write(exercise);
                exerciseWriter.newLine();
            }

            for (String answer : answers) {
                answerWriter.write(answer);
                answerWriter.newLine();
            }

            System.out.println("题目已保存到" + EXERCISES_FILE);
            System.out.println("答案已保存到" + ANSWERS_FILE);
        } catch (IOException e) {
            System.err.println("保存文件时出错：" + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("用法:");
        System.out.println("生成题目: java MathExerciseGenerator -n <题目数量> -r <范围>");
        System.out.println("批改题目: java MathExerciseGenerator -e <题目文件> -a <答案文件>");
    }

    private static Fraction generateNumber(int rangeLimit) {
        boolean isFraction = random.nextBoolean();

        if (!isFraction) {
            return new Fraction(random.nextInt(rangeLimit - 1) + 1);
        } else {
            int denominator = random.nextInt(rangeLimit - 2) + 2;
            int numerator = random.nextInt(denominator - 1) + 1;

            if (random.nextBoolean() && rangeLimit > 1) {
                int integerPart = random.nextInt(rangeLimit - 1) + 1;
                return new Fraction(integerPart, numerator, denominator);
            } else {
                return new Fraction(numerator, denominator);
            }
        }
    }

    private static ExpressionResult generateExpression(int rangeLimit, int maxOps) {
        int numOps = random.nextInt(maxOps) + 1;

        int opCount = random.nextInt(numOps) + 1;
        int leftOps = opCount - 1;
        int rightOps = numOps - opCount;

        ExpressionResult left = generateSubExpression(rangeLimit, leftOps);
        ExpressionResult right = generateSubExpression(rangeLimit, rightOps);

        String[] ops = {"+", "-", "×", "÷"};
        String op = ops[random.nextInt(ops.length)];

        // 对于减法，确保左操作数 >= 右操作数
        if (op.equals("-") && !left.result.greaterOrEqual(right.result)) {
            return generateExpression(rangeLimit, maxOps);
        }

        // 对于除法，确保结果是真分数
        if (op.equals("÷")) {
            try {
                Fraction result = left.result.divide(right.result);
                // 检查除法结果是否为真分数
                if (Math.abs(result.integer) > 0 || Math.abs(result.numerator) >= result.denominator) {
                    return generateExpression(rangeLimit, maxOps);
                }
            } catch (ArithmeticException e) {
                return generateExpression(rangeLimit, maxOps);
            }
        }

        Fraction result;
        switch (op) {
            case "+":
                result = left.result.add(right.result);
                break;
            case "-":
                result = left.result.subtract(right.result);
                break;
            case "×":
                result = left.result.multiply(right.result);
                break;
            case "÷":
                result = left.result.divide(right.result);
                break;
            default:
                throw new IllegalArgumentException("无效的运算符");
        }

        // 确保结果非负
        if (result.integer < 0 || (result.integer == 0 && result.numerator < 0)) {
            return generateExpression(rangeLimit, maxOps);
        }

        String leftExpr = left.expression;
        String rightExpr = right.expression;
        
        // 对于减法表达式，确保括号不会改变运算顺序导致负数
        if (op.equals("-")) {
            // 如果右表达式是加法或减法，需要加括号
            if (containsAddOrSubtract(right.expression)) {
                rightExpr = "(" + rightExpr + ")";
            }
        }
        
        // 对于其他情况，随机添加括号
        if (leftOps > 0 && random.nextBoolean() && !op.equals("-")) {
            leftExpr = "(" + leftExpr + ")";
        }
        
        if (rightOps > 0 && random.nextBoolean() && !op.equals("-")) {
            // 对于减法，右操作数如果是加法或减法需要加括号
            if (op.equals("-") && containsAddOrSubtract(right.expression)) {
                rightExpr = "(" + rightExpr + ")";
            } else if (!op.equals("-")) {
                rightExpr = "(" + rightExpr + ")";
            }
        }

        String expr = leftExpr + " " + op + " " + rightExpr;
        return new ExpressionResult(expr, result);
    }

    // 检查表达式是否包含加法或减法
    private static boolean containsAddOrSubtract(String expr) {
        return expr.contains("+") || expr.contains("-");
    }

    private static ExpressionResult generateSubExpression(int rangeLimit, int maxOps) {
        if (maxOps == 0) {
            Fraction num = generateNumber(rangeLimit);
            return new ExpressionResult(num.toString(), num);
        } else {
            return generateExpression(rangeLimit, maxOps);
        }
    }

   public static String normalizeExpression(String expr) {
        String normalized = expr.replaceAll("[()]", "");
        
        Pattern pattern = Pattern.compile("(.+) ([+×]) (.+)");
        Matcher matcher = pattern.matcher(normalized);
        
        if (matcher.matches()) {
            String left = matcher.group(1);
            String op = matcher.group(2);
            String right = matcher.group(3);
            
            String normalizedLeft = normalizeExpression(left);
            String normalizedRight = normalizeExpression(right);
            
            if (normalizedLeft.compareTo(normalizedRight) > 0) {
                return normalizedRight + " " + op + " " + normalizedLeft;
            }
            
            return normalizedLeft + " " + op + " " + normalizedRight;
        }
        
        return normalized;
    }

    private static void gradeExercises(String exerciseFile, String answerFile) {
        try {
            List<String> exercises = readLines(exerciseFile);
            List<String> answers = readLines(answerFile);

            if (exercises.size() != answers.size()) {
                throw new IllegalArgumentException("题目数量和答案数量不匹配");
            }

            List<Integer> correct = new ArrayList<>();
            List<Integer> wrong = new ArrayList<>();

            for (int i = 0; i < exercises.size(); i++) {
                String exercise = exercises.get(i);
                String answer = answers.get(i);

                try {
                    Fraction correctAnswer = calculateExpression(exercise);
                    Fraction userAnswer = Fraction.parse(answer);
                    if (correctAnswer.equals(userAnswer)) {
                        correct.add(i + 1);
                    } else {
                        wrong.add(i + 1);
                    }
                } catch (Exception e) {
                    System.err.println("解析第" + (i + 1) + "题时出错: " + e.getMessage());
                    wrong.add(i + 1);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(GRADE_FILE))) {
                writer.write(formatGradeLine("Correct", correct));
                writer.newLine();
                writer.write(formatGradeLine("Wrong", wrong));
            }

            System.out.println("批改完成，结果已保存到" + GRADE_FILE);

        } catch (IOException e) {
            System.err.println("批改过程出错：" + e.getMessage());
        }
    }

    private static List<String> readLines(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private static String formatGradeLine(String type, List<Integer> numbers) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(": ").append(numbers.size()).append(" (");
        for (int i = 0; i < numbers.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(numbers.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

    public static Fraction calculateExpression(String expr) {
        expr = expr.replace("=", "").trim();
        // 在运算符和括号周围添加空格，确保正确分词
        expr = expr.replaceAll("([()+\\-×÷])", " $1 ").replaceAll("\\s+", " ").trim();
        return evaluateExpressionWithStack(expr);
    }

    // 使用栈来处理运算符优先级
    private static Fraction evaluateExpressionWithStack(String expr) {
        // 将中缀表达式转换为后缀表达式（逆波兰表示法）
        List<Object> postfix = infixToPostfix(expr);
        // 计算后缀表达式
        return evaluatePostfix(postfix);
    }

    // 中缀转后缀
    private static List<Object> infixToPostfix(String expr) {
        List<Object> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        
        // 按空格分割token
        String[] tokens = expr.split("\\s+");
        
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;
            
            if (isOperator(token)) {
                while (!operators.isEmpty() && 
                       !operators.peek().equals("(") &&
                       precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop(); // 弹出 "("
                }
            } else {
                // 数字
                output.add(Fraction.parse(token));
            }
        }
        
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        
        return output;
    }

    // 计算后缀表达式
    private static Fraction evaluatePostfix(List<Object> postfix) {
        Stack<Fraction> stack = new Stack<>();
        
        for (Object item : postfix) {
            if (item instanceof Fraction) {
                stack.push((Fraction) item);
            } else if (item instanceof String) {
                String operator = (String) item;
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("表达式格式错误");
                }
                Fraction right = stack.pop();
                Fraction left = stack.pop();
                
                switch (operator) {
                    case "+":
                        stack.push(left.add(right));
                        break;
                    case "-":
                        stack.push(left.subtract(right));
                        break;
                    case "×":
                        stack.push(left.multiply(right));
                        break;
                    case "÷":
                        stack.push(left.divide(right));
                        break;
                    default:
                        throw new IllegalArgumentException("未知运算符: " + operator);
                }
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("表达式格式错误");
        }
        
        return stack.pop();
    }

    // 判断是否是运算符
    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("×") || token.equals("÷");
    }

    // 获取运算符优先级
    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
                return 2;
            default:
                return 0;
        }
    }
}

