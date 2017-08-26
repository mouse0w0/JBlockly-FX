package team.unstudio.jblockly.component.behavior;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import team.unstudio.jblockly.component.BlockList;

public class BlockListBehavior extends BehaviorBase<BlockList>{
	
    protected static final List<KeyBinding> KEY_BINDINGS = new ArrayList<KeyBinding>();
    
	public BlockListBehavior(BlockList control) {
		super(control, KEY_BINDINGS);
	}

}
