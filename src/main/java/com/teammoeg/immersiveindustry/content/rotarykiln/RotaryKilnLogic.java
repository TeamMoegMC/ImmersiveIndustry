package com.teammoeg.immersiveindustry.content.rotarykiln;

import java.util.function.Function;

import com.teammoeg.immersiveindustry.util.CapabilityFacing;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RotaryKilnLogic implements IMultiblockLogic<RotaryKilnState>, IClientTickableComponent<RotaryKilnState>, IServerTickableComponent<RotaryKilnState> {
	private static CapabilityFacing itemout = new CapabilityFacing(1, 0, 7,RelativeBlockFace.LEFT);
	private static CapabilityFacing fluidout = new CapabilityFacing(1, 3, 4,RelativeBlockFace.DOWN);
	
	public RotaryKilnLogic() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tickServer(IMultiblockContext<RotaryKilnState> context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickClient(IMultiblockContext<RotaryKilnState> context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RotaryKilnState createInitialState(IInitialMultiblockContext<RotaryKilnState> capabilitySource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
		// TODO Auto-generated method stub
		return null;
	}

}
