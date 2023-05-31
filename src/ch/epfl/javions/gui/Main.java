package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.IIOException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Main extends Application {
    private static final String TILE_SERVER_URL = "tile.openstreetmap.org";
    private static final String CACHE_FOLDER = "tile-cache";
    private static final int INITIAL_ZOOM_LEVEL = 8;
    private static final int X_LEFT_TOP_CORNER = 33530;
    private static final int Y_LEFT_TOP_CORNER = 22500;
    private static final long ONE_SECOND = 1_000_000_000L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "Javions";
    private static final String RESOURCE = "/aircraft.zip";

    /**
     * The main method of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // StatusLine
        StatusLineController slc = new StatusLineController();

        long start = System.nanoTime();
        ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();
        // Read messages
        Thread thread = new Thread(() -> {
            List<String> arg = getParameters().getRaw();
            System.out.println(arg.isEmpty());
            if (arg.isEmpty()) {
                try {
                    readAllMessages(messageQueue);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    readAllMessages(arg.get(0), messageQueue, start);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        // Database creation
        URL u = getClass().getResource(RESOURCE);
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());
        ObjectProperty<ObservableAircraftState> sap = new SimpleObjectProperty<>();
        AircraftStateManager asm = new AircraftStateManager(db);
        // Tile
        Path tileCache = Path.of(CACHE_FOLDER);
        TileManager tm = new TileManager(tileCache, TILE_SERVER_URL);
        MapParameters mp = new MapParameters(INITIAL_ZOOM_LEVEL, X_LEFT_TOP_CORNER, Y_LEFT_TOP_CORNER);
        // Controller
        BaseMapController bmc = new BaseMapController(tm, mp);
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);

        atc.setOnDoubleClick(aircraft -> bmc.centerOn(aircraft.getPosition()));

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        StackPane stackPane = new StackPane(bmc.pane(), ac.pane());
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(atc.pane());
        borderPane.setTop(slc.pane());

        splitPane.getItems().addAll(stackPane, borderPane);

        // Primary Stage setup
        primaryStage.setScene(new Scene(splitPane));
        primaryStage.setTitle(TITLE);
        primaryStage.setMinWidth(WIDTH);
        primaryStage.setMinHeight(HEIGHT);
        primaryStage.show();

        new AnimationTimer() {
            private long lastUpdate = 0;

            /**
             * Handle method that updates the aircraft states and the tableview
             * @param now the current time in nanoseconds
             */
            @Override
            public void handle(long now) {
                for (int i = 0; i < 30; i += 1) {
                    if (!messageQueue.isEmpty()) {
                        Message message = MessageParser.parse(messageQueue.poll());
                        if (message != null) {
                            System.out.println("bien");
                            slc.messageCountProperty().add(1L);
                            asm.updateWithMessage(message);
                        }
                        // Purge 1 time per second aircraft for which no message has been received for one minute
                        if (System.nanoTime() - lastUpdate > ONE_SECOND) {
                            asm.purge();
                            lastUpdate = System.nanoTime();
                        }
                    }
                }
            }
        }.start();
    }

    public void readAllMessages(String fileName, ConcurrentLinkedQueue messageQueue, long start) throws IOException {
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            RawMessage temp;
            byte[] bytes = new byte[RawMessage.LENGTH];
            long i = 0;
            while (i < s.available()) {
                i++;
                long stamp = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                temp = new RawMessage(stamp, message);
                while (temp.timeStampNs() > (System.nanoTime() - start)) {
                    Thread.sleep(1);
                }
                messageQueue.add(temp);
            }
        } catch (IIOException e) {
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    // Lecture des messages des fichiers
    private static List<RawMessage> readAllMessages(String fileName)
            throws IOException {
        List<RawMessage> list = new ArrayList<>();

        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(fileName)))) {
            RawMessage temp;
            byte[] bytes = new byte[RawMessage.LENGTH];
            int i = 0;
            while (i < s.available()) {
                i++;
                long stamp = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                temp = new RawMessage(stamp, message);
                list.add(temp);
            }
        } catch (IIOException e) {
            System.out.println("File not found");
        }
        return list;
    }
    */
    // Lecture des messages de la console
    private static void readAllMessages(ConcurrentLinkedQueue<RawMessage> messagesQueue) throws IOException {
        AdsbDemodulator demodulator = new AdsbDemodulator(System.in);
        RawMessage temp;
        while ((temp = demodulator.nextMessage()) != null) {
            messagesQueue.add(temp);
        }
    }

}

