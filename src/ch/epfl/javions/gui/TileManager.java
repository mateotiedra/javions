package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {
    private final Path path;
    private final String Server;
    private Image image = null;

    public final static int MAX_MEMORY = 100;
    private Map tiles = new LinkedHashMap<TileId, Image>(MAX_MEMORY, 0.75f, true);

    record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            return zoom >= 0 && zoom < 20 && x >= 0 && x < (1 << zoom) && y >= 0 && y < (1 << zoom);
        }
    }

    public TileManager(Path path, String Server) throws IOException {
        this.path = path;
        this.Server = Server;
        Files.createDirectories(path);
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        String filename = tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png";

        if (tiles.containsKey(tileId)) {
            image = (Image) tiles.get(tileId);
            return image;
        } else if (Files.exists(Path.of(filename))) {
            image = new Image(new FileInputStream(filename));
            if (!(tiles.size() == MAX_MEMORY)) {
                tiles.put(tileId, image);
                tiles.keySet().iterator().next();
            } else {
                tiles.keySet().iterator().next();
                tiles.keySet().iterator().remove();
                tiles.put(tileId, image);
            }
            return image;
        }

        URL u = new URL("https://tile.openstreetmap.org/" + filename);
        byte[] bytes;
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        try (InputStream i = c.getInputStream()) {
            bytes = i.readAllBytes();
        }
        try (OutputStream out = new FileOutputStream(filename)) {
            while (!Files.exists(Path.of(filename))) {
                if (Files.exists(Path.of(String.valueOf(tileId.zoom)))) {
                    if (Files.exists(Path.of(tileId.zoom + "/" + tileId.x))) {
                        out.write(bytes);
                    } else {
                        Files.createDirectories(Path.of(tileId.zoom + "/" + tileId.x));
                    }
                } else {
                    Files.createDirectories(Path.of(String.valueOf(tileId.zoom)));
                }
            }
        }
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            image = new Image(is);
            return image;
        }
    }
}