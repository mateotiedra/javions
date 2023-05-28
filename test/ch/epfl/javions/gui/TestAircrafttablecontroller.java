package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class TestAircrafttablecontroller extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    static List<RawMessage> readAllMessages(String fileName)
            throws IOException {
        List<RawMessage> list = new ArrayList<>();
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            AircraftStateManager asm = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));
            int i = 0;
            while (i < s.available()) {
                i++;
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                list.add(new RawMessage(timeStampNs, message));
            }
        }
        return list;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Création de la base de données
        URL dbUrl = getClass().getResource("/aircraft.zip");
        assert dbUrl != null;
        String f = Path.of(dbUrl.toURI()).toString();
        var db = new AircraftDatabase(f);

        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(9, 67448, 46324);
        BaseMapController bmc = new BaseMapController(tm, mp);
        AircraftController ac = new AircraftController(mp, asm.states(), sap);
        var root = new StackPane(bmc.pane(), ac.pane());
        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        SplitPane pane = new SplitPane(root, atc.pane());
        pane.setOrientation(Orientation.VERTICAL);
        primaryStage.setScene(new Scene(pane));
        primaryStage.show();

        var mi = readAllMessages("resources/messages_20230318_0915.bin").iterator();

        // Animation des aéronefs
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < 2; i += 1) {
                    Message m = MessageParser.parse(mi.next());
                    if (m != null) asm.updateWithMessage(m);
                    asm.purge();
                }
            }
        }.start();
    }
}