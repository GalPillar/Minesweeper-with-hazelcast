package application;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.Optional;


class Tile extends StackPane implements EntryListener {

	Button btn = new Button();
	int x, y = 0;
	Long num;

	public Tile(int x, int y, Long num){
		this.x = x;
		this.y = y;
		this.num = num;
		initTile(x, y, Optional.of(num));
	}

	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		initTile(x, y, Optional.empty());
	}

	private void initTile(int x, int y, Optional<Long> num) {
		btn.setMinHeight(40);
		btn.setMinWidth(40);
		btn.setOnMouseClicked(e -> {
			onClick(e);
		});
		getChildren().addAll(btn);
		setTranslateX(x * 40);
		setTranslateY(y * 40);
		num.ifPresent(aLong -> btn.setText(aLong.toString()));
	}

	private void onClick(MouseEvent e) {
		btn.setBackground(null);
		num = Math.round(Math.random()*100);
		btn.setText(num.toString());
		Main.replicatedMap.put(Main.mapLocation(x, y), num);
	}

	public void onChange(Long value){
		btn.setBackground(null);
		btn.setText(null);
		btn.setText(value.toString());
	}

	@Override
	public void entryAdded(EntryEvent event) {
		Platform.runLater(() -> onChange((Long)event.getValue()));
	}

	@Override
	public void entryEvicted(EntryEvent event) {

	}

	@Override
	public void entryRemoved(EntryEvent event) {

	}

	@Override
	public void entryUpdated(EntryEvent event) {
		Platform.runLater(() -> onChange((Long)event.getValue()));
	}

	@Override
	public void mapCleared(MapEvent event) {
	}

	@Override
	public void mapEvicted(MapEvent event) {
	}
}