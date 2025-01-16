package com.teammoeg.immersiveindustry.content.rotarykiln;

import java.util.function.Consumer;
import java.util.function.Function;

import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.IIUtil;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class RotaryKilnLogic implements IMultiblockLogic<RotaryKilnState>, IClientTickableComponent<RotaryKilnState>, IServerTickableComponent<RotaryKilnState> {
	static final CapabilityFacing itemout = new CapabilityFacing(1, 0, 6,RelativeBlockFace.BACK);
	static final CapabilityFacing powerin = new CapabilityFacing(1, 0, 0,RelativeBlockFace.FRONT);
	static final CapabilityFacing itemin = new CapabilityFacing(1, 2, 0,RelativeBlockFace.UP);
	static final CapabilityFacing fluidout = new CapabilityFacing(1, 2, 4,RelativeBlockFace.UP);
	
	public RotaryKilnLogic() {
	}

	@Override
	public void tickServer(IMultiblockContext<RotaryKilnState> context) {
		RotaryKilnState state=context.getState();
		boolean update=false;
		boolean lastActive=state.active;
		state.active=false;
		boolean needRun=false;
		int energycost=0;
		//if process 1 complete then output
		if(state.state.isEnabled(context)) {
			if(state.processes[1]!=null&&state.processes[1].process>=state.processes[1].processMax) {
				RotaryKilnRecipe rcp=state.processes[1].getRecipe(context.getLevel().getRawLevel());
				ItemStack process1Slot=state.inventory.getStackInSlot(2);
				ItemStack outSlot=state.inventory.getStackInSlot(3);
				ItemStack secoutSlot=state.inventory.getStackInSlot(4);
				if((outSlot.isEmpty()||rcp.output.isEmpty()||ItemHandlerHelper.canItemStacksStack(outSlot, rcp.output))) {
					if((secoutSlot.isEmpty()||rcp.secoutput==null||ItemHandlerHelper.canItemStacksStack(secoutSlot,rcp.secoutput.stack().get()))) {
						if(state.tankout.isEmpty()||rcp.output_fluid.isEmpty()||state.tankout.fill(rcp.output_fluid, FluidAction.SIMULATE)==rcp.output_fluid.getAmount()) {
						int maxoutcount=outSlot.getMaxStackSize()-outSlot.getCount();
						int maxsecoutcount=secoutSlot.getMaxStackSize()-secoutSlot.getCount();
						int maxouttimes=64;
						if(!rcp.output.isEmpty())
							maxouttimes=Math.min(maxoutcount/rcp.output.getCount(), maxouttimes);
						if(rcp.secoutput!=null)
							maxouttimes=(int) Math.min(maxsecoutcount/(rcp.secoutput.stack().get().getCount()*rcp.secoutput.chance()), maxouttimes);
						if(!rcp.output_fluid.isEmpty())
							maxouttimes=(int) Math.min(state.tankout.getSpace()/rcp.output_fluid.getAmount(), maxouttimes);
						maxouttimes=Math.min(process1Slot.getCount()/rcp.input.getCount(),maxouttimes);
						if(maxouttimes>0) {
							int inputNum=maxouttimes*rcp.input.getCount();
							process1Slot.shrink(inputNum);
							state.inventory.setStackInSlotNoChange(2, process1Slot);
							if(!rcp.output.isEmpty())
								IIUtil.insertToOutput(state.inventory, 3, rcp.output.copyWithCount(maxouttimes*rcp.output.getCount()));
							if(!rcp.output_fluid.isEmpty()) {
								state.tankout.fill(new FluidStack(rcp.output_fluid,rcp.output_fluid.getAmount()*maxouttimes), FluidAction.EXECUTE);
							}
							if(rcp.secoutput!=null) {
								@SuppressWarnings("resource")
								RandomSource rnd=context.getLevel().getRawLevel().random;
								int tcount=0;
								for(int i=0;i<maxouttimes;i++) {
									if(rcp.secoutput.chance()>=rnd.nextFloat()) {
										tcount++;
									}
								}
								IIUtil.insertToOutput(state.inventory, 4, rcp.secoutput.stack().get().copyWithCount(tcount*rcp.secoutput.stack().get().getCount()));
							}
							update=true;
						}
						if(process1Slot.isEmpty()) {
							state.processes[1]=null;
							update=true;
						}
						}
					}
				}

				
			}
			//if process 0 over half and process 1 is empty, move 0 to 1
			if(state.processes[0]!=null&&state.processes[0].process>=state.processes[0].processMax/2&&state.processes[1]==null) {
				state.processes[1]=state.processes[0];
				state.processes[0]=null;
				ItemStack process0Slot=state.inventory.getStackInSlot(1);
				state.inventory.setStackInSlot(1, ItemStack.EMPTY);
				state.inventory.setStackInSlot(2, process0Slot);
				update=true;
			}
			//if process 0 is empty, start process 0
			if(state.processes[0]==null&&!state.inventory.getStackInSlot(0).isEmpty()) {
				ItemStack inputSlot=state.inventory.getStackInSlot(0);
				RotaryKilnRecipe rcp=RotaryKilnRecipe.findRecipe(context.getLevel().getRawLevel(), inputSlot);
				state.processes[0]=new RotaryKilnProcess(rcp);
				int inCount=inputSlot.getCount()/rcp.input.getCount();
				ItemStack process0Slot=inputSlot.copyWithCount(inCount*rcp.input.getCount());
				state.inventory.setStackInSlot(1, process0Slot);
				inputSlot.shrink(inCount);
				state.inventory.setStackInSlot(0, inputSlot);
				update=true;
			}
			

			if(state.processes[0]!=null) {
				energycost=Math.max(state.processes[0].powerUsage,energycost);
				needRun=true;
			}
			if(state.processes[1]!=null) {
				energycost=Math.max(state.processes[1].powerUsage,energycost);
				needRun=true;
			}
			if(needRun) {
				int extracted=state.energyStorage.extractEnergy(energycost, true);
				if(extracted>=energycost) {
					state.energyStorage.extractEnergy(energycost, false);
					state.active=true;
					if(state.processes[0]!=null&&state.processes[0].process<state.processes[0].processMax) 
						state.processes[0].process++;
					if(state.processes[1]!=null&&state.processes[1].process<state.processes[1].processMax) 
						state.processes[1].process++;
					update=true;
				}
			}
		}
		

		update|=tryOutput(context);
		if(update)
			context.markMasterDirty();
		if(lastActive!=state.active) {
			context.requestMasterBESync();
		}
		
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<RotaryKilnState> ctx, CapabilityPosition position, Capability<T> cap) {
		return ctx.getState().capabilities.getCapability(cap, position, ctx);
	}

	@Override
	public void tickClient(IMultiblockContext<RotaryKilnState> context) {
		RotaryKilnState state=context.getState();
		if (state.active) {
			state.angle += 10;
			if (state.angle >= 360)
				state.angle = 0;
		}
	}

	@Override
	public RotaryKilnState createInitialState(IInitialMultiblockContext<RotaryKilnState> capabilitySource) {
		return new RotaryKilnState(capabilitySource);
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
	
		return t->Shapes.block();
	}
	public boolean tryOutput(IMultiblockContext<RotaryKilnState> context) {
		RotaryKilnState state=context.getState();
		boolean update = FluidUtils.multiblockFluidOutput(state.outFluidCap, state.tankout, 0, 0, null);
		if(context.getLevel().shouldTickModulo(8)){
			final IItemHandlerModifiable inventory = state.inventory;
			update|=IIUtil.outputItem(inventory, state.outInvCap, 3);
			update|=IIUtil.outputItem(inventory, state.outInvCap, 4);
		}
		return update;
	}

	@Override
	public void dropExtraItems(RotaryKilnState state, Consumer<ItemStack> drop) {
		MBInventoryUtils.dropItems(state.inventory, drop);
	}
}
