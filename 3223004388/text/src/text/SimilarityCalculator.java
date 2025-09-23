package text;

import java.util.*;

public class SimilarityCalculator {
    /**
     * 基于混合n-gram的余弦相似度计算
     */
    public static double calculateSimilarity(String text1, String text2) {
        // 1. 混合n-gram分词
        List<String> grams1 = TextProcessor.segment(text1);
        List<String> grams2 = TextProcessor.segment(text2);

        // 2. 处理空文本
        if (grams1.isEmpty() && grams2.isEmpty()) {
            return 1.00;
        }
        if (grams1.isEmpty() || grams2.isEmpty()) {
            return 0.00;
        }

        // 3. 词频统计
        Map<String, Integer> freq1 = TextProcessor.buildWordFrequency(grams1);
        Map<String, Integer> freq2 = TextProcessor.buildWordFrequency(grams2);

        // 4. 计算余弦相似度（增加对高频gram的权重）
        Set<String> allGrams = new HashSet<>(freq1.keySet());
        allGrams.addAll(freq2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String gram : allGrams) {
            int count1 = freq1.getOrDefault(gram, 0);
            int count2 = freq2.getOrDefault(gram, 0);

            // 对高频gram（出现次数≥2）增加权重（1.2倍），放大共同特征
            double weight = (count1 >= 2 && count2 >= 2) ? 1.2 : 1.0;
            
            dotProduct += count1 * count2 * weight;  // 加权点积
            norm1 += Math.pow(count1 * weight, 2);   // 加权模长1
            norm2 += Math.pow(count2 * weight, 2);   // 加权模长2
        }

        // 避免除零错误
        if (norm1 == 0 || norm2 == 0) {
            return 0.00;
        }

        // 计算并保留两位小数
        double similarity = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return Math.round(similarity * 100.0) / 100.0;
    }
}
