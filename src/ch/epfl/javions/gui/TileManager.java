package ch.epfl.javions.gui;

import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A tile manager.
 *
 * @author Kevan Lam (356395)
 */
public final class TileManager {
    public static final double TILE_SIZE = 256;
    private final Path path;
    private final String server;

    public final static int MAX_MEMORY = 100;
    private final Map<TileId, Image> tiles = new LinkedHashMap<>(MAX_MEMORY, 0.75f, true);

    /**
     * Represents a tile identifier consisting of zoom level, x-coordinate, and y-coordinate.
     */
    record TileId(int zoom, int x, int y) {
        public TileId {
            Preconditions.checkArgument(isValid(zoom, x, y));
        }

        /**
         * Checks if the given zoom, x-coordinate, and y-coordinate values form a valid tile ID.
         *
         * @param zoom the zoom level
         * @param x    the x-coordinate
         * @param y    the y-coordinate
         * @return true if the tile ID is valid, false otherwise
         */
        public static boolean isValid(int zoom, int x, int y) {
            return zoom >= 0 && zoom < 20 && x >= 0 && x < (1 << zoom) && y >= 0 && y < (1 << zoom);
        }
    }

    /**
     * Create a new tile manager with the given path and server.
     *
     * @param path   the path to store the tiles
     * @param server the server to download the tiles from
     */
    public TileManager(Path path, String server) {
        this.path = path;
        this.server = server;
    }

    /**
     * Return the tile image for the given tile id.
     *
     * @param tileId the tile id
     * @return the tile image
     * @throws IOException if an I/O error occurs
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        Files.createDirectories(path);
        String filename = "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y + ".png";
        String pathName = path + filename;

        Image image;
        if (tiles.containsKey(tileId)) {
            image = tiles.get(tileId);
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