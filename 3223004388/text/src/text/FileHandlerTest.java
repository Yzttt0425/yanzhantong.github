package text;

import org.junit.Test;
import java.io.FileNotFoundException;

public class FileHandlerTest {
    // 测试读取不存在的文件，预期抛出FileNotFoundException
    @Test(expected = FileNotFoundException.class)
    public void testReadFile_FileNotFound() throws Exception {
        // 传入一个肯定不存在的文件路径（临时目录下的non_exist.txt）
        String nonExistentPath = System.getProperty("java.io.tmpdir") + "/non_exist.txt";
        FileHandler.readFile(nonExistentPath);
    }
}