package team.unstudio.jblockly.util;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.BlockSlot;
import team.unstudio.jblockly.ConnectionType;
import team.unstudio.jblockly.SlotType;

import java.util.ArrayList;
import java.util.List;

public class BlockBuilder {
    private Paint fill;
    private Paint stroke;
    private ConnectionType connection;
    private List<Node> nodes;

    public BlockBuilder(){
        nodes = new ArrayList<>();
        fill = Color.GRAY;
        stroke = Color.BLACK;
        connection = ConnectionType.NONE;
    }

    public Block build(){
        Block bk = new Block();
        bk.getChildren().addAll(nodes);
        bk.setFill(fill);
        bk.setStroke(stroke);
        bk.setConnectionType(connection);
        return bk;
    }

    public BlockBuilder addNode(Node node){
        nodes.add(node);
        return this;
    }

    public BlockBuilder addText(String text){
        return addNode(new Label(text));
    }

    public BlockBuilder addSlot(SlotType type){
        return addNode(new BlockSlot(type));
    }

    public BlockBuilder addSlot(SlotType type, Block linkedBlock){
        return addNode(new BlockSlot(type, linkedBlock));
    }

    public BlockBuilder setFill(Paint fill){
        this.fill = fill;
        return this;
    }

    public BlockBuilder setStroke(Paint stroke){
        this.stroke = stroke;
        return this;
    }
    public BlockBuilder setConnectionType(ConnectionType type){
        connection = type;
        return this;
    }
}
