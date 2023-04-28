package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {
    private Path Path;
    private DirectoryStream<Path> stream;
    private String Server;
    Map tiles = new LinkedHashMap<TileId, Image>(100);

    record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            return zoom >= 0 && zoom < 20 && x >= 0 && x < (1 << zoom) && y >= 0 && y < (1 << zoom);
        }
    }

    public TileManager(Path Path, String Server) throws IOException {
        this.Path = Path;
        this.Server = Server;
        this.stream = Files.createDirectories(Path);
    }

    public Image imageForTileAt(TileId tileId) {
        try (URL u = new URL("https://tile.openstreetmap.org/17/67927/46357.png");
             URLConnection c = u.openConnection();
             c.setRequestProperty("User-Agent", "Javions");
             InputStream i = c.getInputStream();) {

        } catch (IOException e) {
            System.out.println("Error");
            return null;
        }
        return null;
    }
}
