package com.teammoeg.immersiveindustry.content.rotarykiln;

import java.util.Arrays;

import blusunrize.immersiveengineering.api.IEProperties.IEObjState;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.client.ieobj.BlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class RotaryKilnCallback implements BlockCallback<Boolean> {
	public static final RotaryKilnCallback INSTANCE=new RotaryKilnCallback();
	public RotaryKilnCallback() {
	}

	@Override
	public Boolean getDefaultKey() {
		return false;
	}

	@Override
	public Boolean extractKey(BlockAndTintGetter level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		return false;
	}
	private static final IEObjState normalDisplayList = new IEObjState(VisibilityList.hide(Arrays.asList("rotorInEnd")));
	
	private static final IEObjState rotorDisplayList = new IEObjState(VisibilityList.show(Arrays.asList("rotorInEnd")));
	@Override
	public IEObjState getIEOBJState(Boolean instance)
	{
		if(instance)
			return rotorDisplayList;
		return normalDisplayList;
	}

}
