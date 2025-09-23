package text;

import org.junit.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileHandlerTest {
    // 测试读取不存在的文件，预期抛出FileNotFoundException
    @Test(expected = FileNotFoundException.class)
    public void testReadFile_FileNotFound() throws Exception {
        // 传入一个肯定不存在的文件路径（临时目录下的non_exist.txt）
        String nonExistentPath = System.getProperty("java.io.tmpdir") + "/non_exist.txt";
        FileHandler.readFile(nonExistentPath);
    }
    @Test(expected = IOException.class)
    public void testReadFile_ReadDirectory() throws Exception {
        // 获取系统临时目录（肯定是目录，而非文件）
        String tempDirPath = System.getProperty("java.io.tmpdir");
        FileHandler.readFile(tempDirPath);
    }
}