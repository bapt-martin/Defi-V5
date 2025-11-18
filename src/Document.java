import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Scanner;

public abstract class Document {

    public static boolean isFileOpen(Path path) {
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            // Si l'ouverture exclusive réussit, le fichier n'est pas ouvert ailleurs
            return false;
        } catch (IOException e) {
            // IOException = probablement déjà ouvert ailleurs
            return true;
        }
    }

    public static void readObjFile(Path path, Mesh mesh) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // ignore lignes vides

                char firstCharacter = line.charAt(0);

                if (firstCharacter != 'v' && firstCharacter != 'f') continue;

                Scanner scan = new Scanner(line).useLocale(Locale.US);
                scan.next();
                switch (firstCharacter) {
                    case 'v' -> {
                        float x = scan.nextFloat();
                        float y = scan.nextFloat();
                        float z = scan.nextFloat();

                        mesh.getVertices().add(new Vertex3D(x, y, z));
                    }
                    case 'f' -> {
                        int i1 = scan.nextInt()-1;
                        int i2 = scan.nextInt()-1;
                        int i3 = scan.nextInt()-1;

                        mesh.getFaceIndices().add(new int[]{i1,i2,i3});
                    }
                    default -> {
                        // nothing ignore other lines
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Mesh mesh = new Mesh();
        readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\premier test.obj"), mesh);
        mesh.printMesh();
    }
}
