package com.teammoeg.immersiveindustry.content.steamturbine;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SteamTurbineLogic implements IMultiblockLogic<SteamTurbineState>,IServerTickableComponent<SteamTurbineState>,IClientTickableComponent<SteamTurbineState> {
	static final CapabilityPosition FLUID_INPUT = new CapabilityPosition(2, 1, 0, RelativeBlockFace.FRONT);
	static final CapabilityFacing ENERGY_OUT1 =new CapabilityFacing(0, 1, 6,RelativeBlockFace.UP);
	static final CapabilityFacing ENERGY_OUT2 =new CapabilityFacing(2, 1, 6,RelativeBlockFace.UP);
	 
	public SteamTurbineLogic() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public SteamTurbineState createInitialState(IInitialMultiblockContext<SteamTurbineState> capabilitySource) {
		return new SteamTurbineState(capabilitySource);
	}
	static final Function<BlockPos,List<AABB>> origfunc=(posInMultiblock->{
		if (posInMultiblock.getZ() == 0 && posInMultiblock.getX() == 0) {
	        return posInMultiblock.getY() == 0 ? ImmutableList.of(new AABB(0D, 0D, 0D, 1.0D, 0.5D, 1.0D),
	                new AABB(0.5D, 0.5D, 0D, 0D, 1D, 1.0D)) : ImmutableList.of(new AABB(0D, 0D, 0D, 0.5D, 1D, 1.0D));
	    } else if (posInMultiblock.getZ() == 5 && posInMultiblock.getY() == 1) {
	        if (posInMultiblock.getX() % 2 == 0)
	            return Utils.flipBoxes(false, posInMultiblock.getX() == 2, new AABB(1D, 0D, 0D, 0.25D, 0.5D, 1.0D));
	        return ImmutableList.of(new AABB(0D, 0D, 0D, 1.0D, 0.5D, 1.0D));
	    } else if (posInMultiblock.getX() % 2 == 0 && posInMultiblock.getZ() != 6) {
	        if (posInMultiblock.getY() == 0)
	            return ImmutableList.of(new AABB(0D, 0D, 0D, 1.0D, 0.5D, 1.0D), Utils.flipBox(false, posInMultiblock.getX() == 2, new AABB(1D, 0.5D, 0D, 0.25D, 1D, 1.0D)));
	        else if (posInMultiblock.getZ() == 0) {
	            if (posInMultiblock.getY() == 2)
	                return ImmutableList.of(new AABB(0.25D, 0D, 0.8D, 0.75D, 0.7D, 1D));
	            return ImmutableList.of(new AABB(0.25D, 0.25D, 0.125D, 0.75D, 0.75D, 1D), new AABB(0.125D, 0.125D, 0D, 0.875D, 0.875D, 0.125D));
	        } else if (posInMultiblock.getY() == 1)
	            return Utils.flipBoxes(false, posInMultiblock.getX() == 2, new AABB(1D, 0D, 0D, 0.25D, 1D, 1.0D));
	        else return ImmutableList.of(new AABB(0.25D, 0D, 1D, 0.75D, 0.7D, 0D));
	    } else if (posInMultiblock.getY() == 2)
	        return ImmutableList.of(new AABB(0D, 0D, 1D, 1.0D, 0.5D, 0D));
	    else if (posInMultiblock.equals(new BlockPos(1, 0, 0)))
	        return ImmutableList.of(new AABB(0D, 0D, 0D, 1.0D, 0.5D, 1.0D));

	    else return ImmutableList.of(new AABB(0, 0, 0, 1.0D, 1.0D, 1.0D));
	});
	static final Function<BlockPos,VoxelShape> getterFunc=Util.memoize(origfunc.andThen(t->t.stream().map(Shapes::create).reduce(Shapes.empty(),Shapes::or)));
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
		return getterFunc;
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<SteamTurbineState> ctx, CapabilityPosition position, Capability<T> cap) {
		if(cap==ForgeCapabilities.FLUID_HANDLER&&FLUID_INPUT.equalsOrNullFace(position)) {
			return ctx.getState().fluidCap.cast(ctx);
		}
		if(cap==ForgeCapabilities.ENERGY&&position.side()==RelativeBlockFace.UP||position.side()==null) {
			if(ENERGY_OUT1.isCapabilityPosition(position)||ENERGY_OUT2.isCapabilityPosition(position)) {
				return ctx.getState().energyView.cast(ctx);
			}
		}
		return IMultiblockLogic.super.getCapability(ctx, position, cap);
	}

	@Override
	public void tickClient(IMultiblockContext<SteamTurbineState> context) {
		SteamTurbineState state=context.getState();
		if (!state.isSoundPlaying.getAsBoolean()) {
			final Vec3 soundPos = context.getLevel().toAbsolute(new Vec3(1.5, 1.5, 3.5));
			state.isSoundPlaying = MultiblockSound.startSound(
					() -> state.active, context.isValid(), soundPos, IESounds.dieselGenerator, 0.5f
			);
        }
	}

	@Override
	public void tickServer(IMultiblockContext<SteamTurbineState> context) {
		SteamTurbineState state=context.getState();
		boolean pactive=context.getState().active;
        if (state.rsstate.isEnabled(context)&& !state.tanks.isEmpty()) {
            List<IEnergyStorage> presentOutputs = state.energyOutputs.stream()
				.map(CapabilityReference::getNullable)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
            if (!presentOutputs.isEmpty()) {
            	int steam=IIConfig.COMMON.steamTurbineSteam.get();
                int out = IIConfig.COMMON.steamTurbineGenerator.get();
            	if(!presentOutputs.isEmpty()&&state.tanks.getFluidAmount() >= steam&&EnergyHelper.distributeFlux(presentOutputs, out, false) < out)
				{
            		state.active = true;
            		state.tanks.drain(steam, IFluidHandler.FluidAction.EXECUTE);
            		context.markMasterDirty();
				}else
					state.active = false;
            }else
            	state.active = false;
        } else if (state.active)
        	state. active = false;
        if(pactive!=state.active) {
        	context.markDirtyAndSync();
        }
		
	}



}
