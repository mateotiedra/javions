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
    public static final double TILE_SIZE = 256;
    private final Path path;
    private final String server;
    private Image image = null;

    public final static int MAX_MEMORY = 100;
    private Map tiles = new LinkedHashMap<TileId, Image>(MAX_MEMORY, 0.75f, true);

    record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            return zoom >= 0 && zoom < 20 && x >= 0 && x < (1 << zoom) && y >= 0 && y < (1 << zoom);
        }
    }

    public TileManager(Path path, String server) {
        this.path = path;
        this.server = server;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        Files.createDirectories(path);
        String filename = "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png";
        String pathName = path + filename;

        if (tiles.containsKey(tileId)) {
            image = (Image) tiles.get(tileId);
            return image;
        }
        if (Files.exists(Path.of(pathName))) {
            image = new Image(new FileInputStream(pathName));
            if ((tiles.size() == MAX_MEMORY)) {
                tiles.remove(tiles.keySet().iterator().next());
            }
            tiles.put(tileId, image);
            return image;
        }

        URL u = new URL("https://" + server + filename);
        byte[] bytes;
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        try (InputStream i = c.getInputStream()) {
            bytes = i.readAllBytes();
            Files.createDirectories(Path.of(path + "/" + tileId.zoom + "/" + tileId.x + "/"));
        }
        try (OutputStream out = new FileOutputStream(pathName)) {
            out.write(bytes);
        }
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            image = new Image(is);
        }
        if ((tiles.size() == MAX_MEMORY)) {
            tiles.remove(tiles.keySet().iterator().next());
        }
        tiles.put(tileId, image);
        return image;
    }
}