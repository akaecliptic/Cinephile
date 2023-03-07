package akaecliptic.dev.cinephile.auxil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public abstract class IO {
    public static List<String> readFileLines(String file) throws IOException {
        var loader = IO.class.getClassLoader();
        assert loader != null;
        var location = loader.getResource(file).getPath();
        return Files.readAllLines(new File(location).toPath());
    }

    public static String createDatabase(String dir) throws IOException {
        var location = dir + "/databases/Cinephile.db";
        var file = new File(location);
        var parent = file.getParentFile();

        if ( parent == null || !parent.mkdirs() ) throw new IOException("File could not be created at location: " + location);

        if ( file.createNewFile() ) return location;
        else throw new IOException("File could not be created at location: " + location);
    }
}
