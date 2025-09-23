package text;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {
    /**
     * 读取文件内容
     * @param filePath 文件路径
     * @return 文件内容字符串
     * @throws IOException 如果文件不存在或读取失败
     */
    public static String readFile(String filePath) throws IOException {
        // 检查文件是否存在
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }
        
        if (!file.isFile()) {
            throw new IOException("路径不是一个文件: " + filePath);
        }
        
        if (!file.canRead()) {
            throw new IOException("没有文件读取权限: " + filePath);
        }
        
        // 读取文件内容
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /**
     * 写入内容到文件
     * @param filePath 文件路径
     * @param content 要写入的内容
     * @throws IOException 如果写入失败
     */
    public static void writeFile(String filePath, String content) throws IOException {
        // 确保父目录存在
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // 写入文件
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
    }
}
