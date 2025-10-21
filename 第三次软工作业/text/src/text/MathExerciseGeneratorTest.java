package text;

import static org.junit.Assert.*;
import org.junit.Test;

import text.MathExerciseGenerator.Fraction;



public class MathExerciseGeneratorTest {

    // 1. 分数简化测试（带整数部分的假分数）
    @Test
    public void testFractionSimplifyWithMixedNumber() {
        Fraction f = new Fraction(1, 5, 3); // 1 + 5/3 = 8/3 → 2'2/3
        assertEquals(2, f.integer);
        assertEquals(2, f.numerator);
        assertEquals(3, f.denominator);
    }

    // 2. 分数简化测试（负分数）
    @Test
    public void testFractionSimplifyWithNegative() {
        Fraction f = new Fraction(0, -7, 4); // -7/4 → -1'3/4
        assertEquals(-1, f.integer);
        assertEquals(3, f.numerator);
        assertEquals(4, f.denominator);
    }

    // 3. 分数加法（纯分数相加）
    @Test
    public void testFractionAddPureFractions() {
        Fraction f1 = new Fraction(0, 1, 4); // 1/4
        Fraction f2 = new Fraction(0, 1, 6); // 1/6
        Fraction result = f1.add(f2); // 1/4 + 1/6 = 5/12
        assertEquals(new Fraction(0, 5, 12), result);
    }

    // 4. 分数加法（整数与带分数相加）
    @Test
    public void testFractionAddIntegerAndMixed() {
        Fraction f1 = new Fraction(3); // 3
        Fraction f2 = new Fraction(2, 1, 5); // 2'1/5 = 11/5
        Fraction result = f1.add(f2); // 3 + 11/5 = 26/5 = 5'1/5
        assertEquals(new Fraction(5, 1, 5), result);
    }

    // 5. 分数减法（结果为纯分数）
    @Test
    public void testFractionSubtractToPure() {
        Fraction f1 = new Fraction(1, 1, 2); // 3/2
        Fraction f2 = new Fraction(1); // 1
        Fraction result = f1.subtract(f2); // 3/2 - 1 = 1/2
        assertEquals(new Fraction(0, 1, 2), result);
    }

   


    // 6. 表达式计算（多层括号）
    @Test
    public void testCalculateNestedParentheses() {
        String expr = "((1/2 + 3/4) × 2) - 1'1/3"; 
        // 内层：1/2+3/4=5/4 → 5/4×2=5/2 → 5/2 - 4/3=7/6=1'1/6
        Fraction result = MathExerciseGenerator.calculateExpression(expr);
        assertEquals(new Fraction(1, 1, 6), result);
    }

    // 7. 表达式计算（运算符混合）
    @Test
    public void testCalculateMixedOperators() {
        String expr = "3 × 1/3 + 2'1/2 ÷ 5"; 
        // 3×1/3=1 → 2'1/2÷5=5/2÷5=1/2 → 1+1/2=3/2=1'1/2
        Fraction result = MathExerciseGenerator.calculateExpression(expr);
        assertEquals(new Fraction(1, 1, 2), result);
    }

    // 8. 分数解析（边界格式）
    @Test
    public void testFractionParseEdgeCases() {
        Fraction f1 = Fraction.parse("0"); // 整数0
        assertEquals(new Fraction(0), f1);

        Fraction f2 = Fraction.parse("-0/1"); // 负零（应简化为0）
        assertEquals(new Fraction(0), f2);

        Fraction f3 = Fraction.parse("100'99/100"); // 大整数部分
        assertEquals(new Fraction(100, 99, 100), f3);
    }

   

   

    // 9. 分母为1的分数运算
    @Test
    public void testFractionWithDenominatorOne() {
        Fraction f1 = new Fraction(0, 5, 1); // 5/1 = 5
        Fraction f2 = new Fraction(0, 3, 1); // 3/1 = 3
        assertEquals(new Fraction(8), f1.add(f2)); // 5+3=8
    }

    // 10. 除法分母为零异常
    @Test(expected = ArithmeticException.class)
    public void testDivideByZeroException() {
        Fraction f1 = new Fraction(2, 1, 3);
        Fraction f2 = new Fraction(0); // 0
        f1.divide(f2); // 除数为0，抛出异常
    }

    // 11. 题目批改完整流程（模拟多题）
    @Test
    public void testGradeMultipleExercises() {
        // 题目1：正确
        String exercise1 = "2 + 3 × 4 ="; // 2+12=14
        Fraction correct1 = MathExerciseGenerator.calculateExpression(exercise1);
        Fraction user1 = Fraction.parse("14");
        assertTrue(correct1.equals(user1));

        // 题目2：错误（运算顺序错误）
        String exercise2 = "(2 + 3) × 4 ="; // 5×4=20
        Fraction correct2 = MathExerciseGenerator.calculateExpression(exercise2);
        Fraction user2 = Fraction.parse("14"); // 错误答案（2+3×4的结果）
        assertFalse(correct2.equals(user2));

        // 题目3：正确（分数）
        String exercise3 = "1/2 ÷ 3 ="; // 1/2÷3=1/6
        Fraction correct3 = MathExerciseGenerator.calculateExpression(exercise3);
        Fraction user3 = Fraction.parse("1/6");
        assertTrue(correct3.equals(user3));
    }
}