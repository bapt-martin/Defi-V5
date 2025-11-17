import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Document {
    public static void objReader(Path path) {
//        Paths.get("C:\\Users\\marti\\Desktop\\premier test.obj");
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        objReader(Paths.get("C:\\Users\\marti\\Desktop\\premier test.obj"));
        System.out.println("c");
    }
}
