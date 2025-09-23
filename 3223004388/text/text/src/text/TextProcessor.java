package text;

import java.util.*;

public class TextProcessor {
    // 停用词表（过滤无意义词汇）
    private static final Set<String> STOP_WORDS = new HashSet<>();
    static {
        STOP_WORDS.addAll(Arrays.asList("的", "是", "在", "这", "为", "了", "与", "和", "并", "或", "一个", "用于"));
    }

    /**
     * 增强版文本预处理：保留英文单词和数字的完整性
     */
    public static String preprocess(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        // 1. 保留中英文、数字、英文单词连接符（'）和空格，其他字符替换为空格
        String processed = text.replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5'\\s]", " ");
        // 2. 英文转小写
        processed = processed.toLowerCase();
        // 3. 合并连续空格
        return processed.replaceAll("\\s+", " ").trim();
    }

    /**
     * 混合n-gram分词（2-gram + 3-gram）：增强长序列匹配
     */
    public static List<String> segment(String text) {
        List<String> grams = new ArrayList<>();
        String processed = preprocess(text);
        if (processed.isEmpty()) {
            return grams;
        }

        // 1. 生成2-gram
        for (int i = 0; i < processed.length() - 1; i++) {
            String gram = processed.substring(i, i + 2);
            if (isValidGram(gram)) {
                grams.add(gram);
            }
        }

        // 2. 生成3-gram（增强长序列匹配）
        for (int i = 0; i < processed.length() - 2; i++) {
            String gram = processed.substring(i, i + 3);
            if (isValidGram(gram)) {
                grams.add(gram);
            }
        }

        return grams;
    }

    /**
     * 过滤无效gram（含停用词或纯空格）
     */
    private static boolean isValidGram(String gram) {
        if (gram.contains(" ")) {
            return false; // 过滤含空格的gram
        }
        // 检查gram是否包含停用词（如gram为"的是"，其中"的"是停用词）
        for (String stopWord : STOP_WORDS) {
            if (gram.contains(stopWord)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 词频统计（保持不变）
     */
    public static Map<String, Integer> buildWordFrequency(List<String> words) {
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        }
        return frequency;
    }
}
