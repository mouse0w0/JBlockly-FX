package team.unstudio.jblockly.component.skin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SkinBase;
import team.unstudio.jblockly.Block;
import team.unstudio.jblockly.component.BlockList;
import team.unstudio.jblockly.provider.IBlockProvider;

public class BlockListSkin extends SkinBase<BlockList> {

	private Map<IBlockProvider, Block> providerToBlock = new LinkedHashMap<>();

	public BlockListSkin(BlockList control) {
		super(control);
		init();
		updateChildren();
	}
	
	public ObservableList<IBlockProvider> getProviders(){
		return getSkinnable().providersProperty();
	}
	
	private void init() {
		initBlock();
		
		getProviders().addListener(new ListChangeListener<IBlockProvider>() {

			@Override
			public void onChanged(Change<? extends IBlockProvider> c) {
				while(c.next()){
					c.getAddedSubList().forEach(BlockListSkin.this::updateProvider);
					c.getRemoved().forEach(BlockListSkin.this::removeProvider);
				}
			}
		});
	}
	
	private final ChangeListener<Parent> parentChangeListener = (observable,oldValue,newValue)->{
		if(newValue!=null&&!getSkinnable().equals(newValue))
			validateCache();
	};
	
	private void initBlock(){
		providerToBlock.clear();
		getProviders().forEach(this::updateProvider);
	}
	
	private void updateProvider(IBlockProvider provider){
		if(providerToBlock.containsKey(provider))
			providerToBlock.get(provider).parentProperty().removeListener(parentChangeListener);
		
		Block block = provider.build();
		block.parentProperty().addListener(parentChangeListener);
		providerToBlock.put(provider, block);
	}
	
	private void removeProvider(IBlockProvider provider){
		providerToBlock.get(provider).parentProperty().removeListener(parentChangeListener);
		providerToBlock.remove(provider);
	}
	
	private void validateCache(){
		providerToBlock.entrySet().stream()
		.filter(entry->!getSkinnable().equals(entry.getValue().getParent()))
		.forEach(entry->updateProvider(entry.getKey()));
	}

	private void updateChildren() {
		Predicate<Block> filter = getSkinnable().getFilter();
		if(filter == null)
			getChildren().setAll(providerToBlock.values());
		else 
			getChildren().setAll(providerToBlock.values().stream().filter(filter).collect(Collectors.toList()));
	}

	private boolean layouting;

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		if (layouting)
			return;
		layouting = true;

		updateChildren();

		double spacing = getSkinnable().getSpacing();
		double x = contentX, y = contentY;

		for (Node node : providerToBlock.values()) {
			double width = node.prefWidth(-1), height = node.prefHeight(-1);
			layoutInArea(node, x, y, width, height, 0, HPos.LEFT, VPos.TOP);
			y += height + spacing;
		}

		layouting = false;
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double width = 0;

		for (Node node : providerToBlock.values()) {
			double twidth = node.prefWidth(-1);
			if (width < twidth)
				width = twidth;
		}

		return leftInset + width + rightInset;
	}

	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double spacing = getSkinnable().getSpacing();
		double height = 0;

		for (Node node : providerToBlock.values()) {
			double theight = node.prefHeight(-1);
			height += theight + spacing;
		}

		return topInset + height + bottomInset;
	}

	@Override
	public void dispose() {
		providerToBlock.values().forEach(block->block.parentProperty().removeListener(parentChangeListener));
		providerToBlock.clear();
	}
}
