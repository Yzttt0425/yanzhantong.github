package text;

import org.junit.Test;
import static org.junit.Assert.*;

public class SimilarityCalculatorTest {

    // 测试完全相同的文本（相似度应为 1.00）
    @Test
    public void testCalculateSimilarity_IdenticalText() {
        String text1 = "这是一篇原创论文，讨论人工智能的应用。";
        String text2 = "这是一篇原创论文，讨论人工智能的应用。";
        double similarity = SimilarityCalculator.calculateSimilarity(text1, text2);
        assertEquals(1.00, similarity, 0.01); // 允许0.01的误差
    }

    // 测试完全不同的文本（相似度应为 0.00）
    @Test
    public void testCalculateSimilarity_CompletelyDifferent() {
        String text1 = "人工智能";
        String text2 = "量子计算";
        double similarity = SimilarityCalculator.calculateSimilarity(text1, text2);
        assertEquals(0.00, similarity, 0.01);
    }

    // 测试部分相似的文本（相似度应为 0.5~0.8 之间，具体值需计算）
    @Test
    public void testCalculateSimilarity_PartiallySimilar() {
        String text1 = "这是一篇原创论文，讨论人工智能在医疗领域的应用。";
        String text2 = "这是一篇抄袭论文，讨论人工智能在医疗领域的应用。";
        double similarity = SimilarityCalculator.calculateSimilarity(text1, text2);
        // 核心短语（人工智能、医疗领域、应用）相同，相似度应较高
        assertTrue(similarity > 0.7 && similarity < 0.9);
    }

    // 测试空文本场景
    @Test
    public void testCalculateSimilarity_EmptyText() {
        // 两个空文本（相似度 1.00）
        double sim1 = SimilarityCalculator.calculateSimilarity("", "");
        assertEquals(1.00, sim1, 0.01);

        // 一个空文本，一个非空文本（相似度 0.00）
        double sim2 = SimilarityCalculator.calculateSimilarity("测试", "");
        assertEquals(0.00, sim2, 0.01);

        double sim3 = SimilarityCalculator.calculateSimilarity(null, "测试");
        assertEquals(0.00, sim3, 0.01);
    }

    // 测试混合语言（中英文+数字）的相似度
    @Test
    public void testCalculateSimilarity_MixedLanguage() {
        String text1 = "AI（人工智能）的发展始于1956年的Dartmouth会议。";
        String text2 = "人工智能（AI）的发展始于1956年的Dartmouth会议。";
        double similarity = SimilarityCalculator.calculateSimilarity(text1, text2);
        assertEquals(1.00, similarity, 0.01); // 仅语序微调，核心gram相同
    }

    // 测试高频gram的权重放大效果
    @Test
    public void testCalculateSimilarity_HighFrequencyGram() {
        // text1 和 text2 中 "人工智能" 均出现3次（高频gram，权重1.2）
        String text1 = "人工智能 人工智能 人工智能 机器学习";
        String text2 = "人工智能 人工智能 人工智能 深度学习";
        double similarity = SimilarityCalculator.calculateSimilarity(text1, text2);
        // 高频gram权重放大后，相似度应高于普通情况
        assertTrue(similarity > 0.75);
    }
    private String generateLongText(String baseParagraph, int repeatCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            sb.append(baseParagraph);
            if ((i + 1) % 10 == 0) sb.append("\n"); // 每10段加换行（模拟段落结构）
        }
        return sb.toString();
    }

    /**
     * 测试1万字完全相同的长文本
     * 预期：相似度1.00，程序不崩溃
     */
    @Test
    public void testCalculateSimilarity_LongIdenticalText() {
        // 基础段落（约50字），重复200次 → 约1万字
        String base = "深度学习框架是构建神经网络的工具，主流框架包括TensorFlow、PyTorch和Keras，简化了模型开发流程。";
        String text1 = generateLongText(base, 200);
        String text2 = generateLongText(base, 200);

        double sim = SimilarityCalculator.calculateSimilarity(text1, text2);
        assertEquals("长文本完全相同相似度错误", 1.00, sim, 0.2); 
       
    }

    /**
     * 测试90%相似的长文本（10%内容修改）
     * 预期：相似度0.85~0.95
     */
    @Test
    public void testCalculateSimilarity_LongHighSimilar() {
        // 基础段落（约50字），生成1万字原创文本
        String originalBase = "数据挖掘是从大量数据中提取价值信息的过程，结合统计学、AI和数据库技术，用于商业决策。";
        String original = generateLongText(originalBase, 200);

        // 生成90%相似的抄袭文本（每10段修改1段）
        String modifiedBase = "数据挖掘是从海量数据中提取有用信息的技术，融合统计学、人工智能和数据库方法，服务于商业分析。";
        StringBuilder plagiarized = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            if (i % 10 == 0) {
                plagiarized.append(modifiedBase);
            } else {
                plagiarized.append(originalBase);
            }
            if ((i + 1) % 10 == 0) plagiarized.append("\n");
        }

        double sim = SimilarityCalculator.calculateSimilarity(original, plagiarized.toString());
        
        assertTrue("长文本高相似度计算异常", sim > 0.8 && sim < 0.95);
    }

   
    private String generateLong(String base, int repeat) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            sb.append(base);
        }
        return sb.toString();
    }

    /** 部分相似的长文本（50%内容相同）→ 相似度应在 0.4~0.6 之间 */
    @Test
    public void testPartialSimilarLongText() {
        String sameBase = "人工智能的发展分为弱AI、强AI和超AI三个阶段。";
        String diffBase1 = "弱AI专注特定任务，如语音助手、图像识别。";
        String diffBase2 = "强AI追求通用智能，能像人类一样推理和学习。";

        StringBuilder text1 = new StringBuilder();
        StringBuilder text2 = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            text1.append(sameBase).append(diffBase1);
            text2.append(sameBase).append(diffBase2);
        }

        double sim = SimilarityCalculator.calculateSimilarity(text1.toString(), text2.toString());
        assertTrue("部分相似长文本相似度异常", sim > 0.4 && sim < 0.6);
    }

}