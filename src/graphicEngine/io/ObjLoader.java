package graphicEngine.io;

import graphicEngine.math.geometry.Mesh;
import graphicEngine.math.geometry.Vertex3D;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public abstract class ObjLoader {

    public static boolean isFileOpen(Path path) {
        try (var channel = FileChannel.open(path, StandardOpenOption.WRITE)) {
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    public static Mesh readObjFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            List<Vertex3D> vertices = new ArrayList<>();
            List<int[]> indicesFaces = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    char firstCharacter = line.charAt(0);
                    if (firstCharacter == 'v' || firstCharacter == 'f');

                    Scanner scan = new Scanner(line).useLocale(Locale.US);
                    scan.next();
                    switch (firstCharacter) {
                        case 'v' -> {
                            float x = scan.nextFloat();
                            float y = scan.nextFloat();
                            float z = scan.nextFloat();

                            vertices.add(new Vertex3D(x, y, z));
                        }
                        case 'f' -> {
                            int i1 = scan.nextInt()-1;
                            int i2 = scan.nextInt()-1;
                            int i3 = scan.nextInt()-1;

                            indicesFaces.add(new int[]{i1,i2,i3});
                        }
                        default -> {
                        }
                    }
                }
            }
            Mesh mesh = new Mesh();
            mesh.triConstruct(indicesFaces, vertices);

            return mesh;

        } catch (IOException e) {
            e.printStackTrace();
            return new Mesh();
        }
    }

    public static void main(String[] args) {
        Mesh mesh = readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\premier test.obj"));
    }
}
