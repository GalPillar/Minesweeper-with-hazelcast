package application;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
	private static int gridSize = 10;
	private static Tile[][] grid;
	private static Stage main;
	private static VBox vbox = new VBox();

	public static ReplicatedMap<Integer, Long> replicatedMap;
	public static HazelcastInstance instance;
	public static ExecutorService executorService = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() {
		if (instance != null){
			instance.shutdown();
		}
	}

	@Override
	public void start(Stage stage) {
		grid = new Tile[gridSize][gridSize];
		main = stage;
		main.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		main.setTitle("Minesweeper multiplayer - By Gal Pillar");
		vbox.getChildren().addAll(createContent());
		Scene scene = new Scene(vbox);
		scene.getStylesheets().add("/application.css");
		main.setScene(scene);
		main.setResizable(false);
		main.sizeToScene();
		main.show();
	}

	/**
	 * Create all the tiles and assign bombs accordingly
	 * 
	 * @return root - The playing field
	 */
	private static Parent createContent() {
		Config config = new Config();
		config.getGroupConfig().setName("1");
		NetworkConfig network = config.getNetworkConfig();
		network.setPort(5701).setPortCount(20);
		network.setPortAutoIncrement(true);
		JoinConfig join = network.getJoin();
		join.getTcpIpConfig()
				.addMember("localhost")
				.setEnabled(true);
		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
		replicatedMap = hazelcastInstance.getReplicatedMap("1");

		Pane root = new Pane();
		root.setPrefSize(gridSize * 40, gridSize * 40);

		// Create all tile and buttons on the grid assign tiles to be bombs based on
		// percentage
		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				Tile tile;
				if (replicatedMap.containsKey(mapLocation(x, y))){
					tile = new Tile(x, y, (Long)replicatedMap.get(mapLocation(x, y)));
				} else {
					tile = new Tile(x, y);
				}
				grid[x][y] = tile;
				root.getChildren().add(tile);
				replicatedMap.addEntryListener(tile, mapLocation(x, y));
			}
		}
		return root;
	}
// (index, tileProperty) -> tile.onChange(0L), mapLocation(x, y)
	public static Integer mapLocation(Integer x, Integer y){
		return x*10 + y;
	}
}
