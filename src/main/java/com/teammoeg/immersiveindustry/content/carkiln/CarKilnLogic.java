package com.teammoeg.immersiveindustry.content.carkiln;

import java.util.function.Function;

import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerRecipe;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerState;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.IIUtil;
import com.teammoeg.immersiveindustry.util.RecipeHandler;
import com.teammoeg.immersiveindustry.util.RecipeProcessResult;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class CarKilnLogic implements IMultiblockLogic<CarKilnState>, IClientTickableComponent<CarKilnState>, IServerTickableComponent<CarKilnState> {
	 public static final CapabilityFacing[] outputItemCaps = new CapabilityFacing[]{
		new CapabilityFacing(0,0,4,RelativeBlockFace.BACK),
		new CapabilityFacing(1,0,4,RelativeBlockFace.BACK),
		new CapabilityFacing(2,0,4,RelativeBlockFace.BACK)} ;
	 public static final CapabilityFacing[] inputItemCaps = new CapabilityFacing[]{
		new CapabilityFacing(0,1,4,RelativeBlockFace.RIGHT),
		new CapabilityFacing(2,1,4,RelativeBlockFace.LEFT)} ;
	 public static final  CapabilityFacing inputFluidCap = new CapabilityFacing(1,3,0,RelativeBlockFace.FRONT) ;
	 public static final CapabilityFacing inputEnergyCap = new CapabilityFacing(1,1,0,RelativeBlockFace.FRONT) ;
	 
	public CarKilnLogic() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tickServer(IMultiblockContext<CarKilnState> context) {
		CarKilnState state=context.getState();
		ChangeDetectedItemHandler inventory = state.inventory;
		int energyConsume=IIConfig.COMMON.carKilnBase.get();
		tryOutput(context);
		boolean shouldEarlyExit=false;
		for(int i=0;i<state.result.getSlots();i++) {
			ItemStack cur=state.result.getStackInSlot(i);
			if(!cur.isEmpty()) {
				shouldEarlyExit=true;
				state.result.setStackInSlot(i, ItemHandlerHelper.insertItem(state.resultwrap, cur, false));
			}
		}
		if (!shouldEarlyExit&&state.state.isEnabled(context) && state.energyStorage.getEnergyStored() >= energyConsume) {
			RecipeHandler<CarKilnRecipe> handler = state.recipe;
			
			if (handler.shouldTestRecipe()) {
				RecipeProcessResult<CarKilnRecipe> recipeResult = CarKilnRecipe.findRecipe(context.getLevel().getRawLevel(), inventory, state.tank.getFluid());
				if(handler.setRecipe(recipeResult)) {
					state.maxProcessCount=0;
					if(recipeResult!=null) {
						state.maxProcessCount=64;
						if(recipeResult.recipe().input_fluid!=null) {
							state.maxProcessCount=state.tank.getFluidAmount()/recipeResult.recipe().input_fluid.getAmount();
						}
						state.maxProcessCount=Math.min(Math.min(recipeResult.getMaxRuns(inventory), recipeResult.recipe().maxProcess),state.maxProcessCount);
						
					}
				}
				context.markDirtyAndSync();
			}
			RandomSource rs = context.getLevel().getRawLevel().random;
			boolean lastActive = state.active;
			state.active = false;
			if (handler.shouldTickProcess()) {
				CarKilnRecipe rcp = CarKilnRecipe.recipeList.getById(context.getLevel().getRawLevel(), handler.getLastRecipe());
				energyConsume = rcp.tickEnergy;
				if (state.energyStorage.extractEnergy(energyConsume, true) >= energyConsume)
					if (handler.tickProcess(1)) {
						state.energyStorage.extractEnergy(energyConsume, false);
						state.active = true;
						int ptm=handler.getProcess();
						int process=handler.getFinishedProgress();
						int lastProcess=state.pos;
						if (process<53)
							state.pos=process;
						else if (ptm < 53)
							state.pos=ptm;
						else
							state.pos=52;
						if(lastProcess!=state.pos)
							context.requestMasterBESync();
						context.markMasterDirty();
					}

				if (handler.isRecipeFinished()) {
					RecipeProcessResult<CarKilnRecipe> recipeResult = handler.getRecipeResultCache();
					if (recipeResult != null) {
						CarKilnRecipe recipe = recipeResult.recipe();
						recipeResult.runOperations(inventory);
						if(recipe.input_fluid!=null)
						state.tank.drain(recipe.input_fluid.getAmount(), FluidAction.EXECUTE);
						for(ItemStack output:recipe.output) {
							ItemHandlerHelper.insertItem(state.result, output.copyWithCount(output.getCount()*state.maxProcessCount), false);
						}
						handler.endProcess();
						state.maxProcessCount=0;
						context.markDirtyAndSync();
					} else {
						handler.endProcess();
					}
				}
				context.markMasterDirty();
			}
			if (state.active != lastActive)
				context.requestMasterBESync();
		}
		
	}

	@Override
	public void tickClient(IMultiblockContext<CarKilnState> context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CarKilnState createInitialState(IInitialMultiblockContext<CarKilnState> capabilitySource) {
		return new CarKilnState(capabilitySource);
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
		return t->Shapes.block();
	}
	public boolean tryOutput(IMultiblockContext<CarKilnState> context) {
		CarKilnState state = context.getState();
		boolean update=false;
		if (context.getLevel().shouldTickModulo(16)) {
			final IItemHandlerModifiable inventory = state.inventory;
			for(CapabilityReference<IItemHandler> c:state.outputItemCap)
				if(c.isPresent())
					for (int slot = 4; slot < 9; ++slot) {
						update|=IIUtil.outputItem(inventory, c, 4);
					}
		}
		return update;
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<CarKilnState> ctx, CapabilityPosition position, Capability<T> cap) {
		return ctx.getState().capabilities.getCapability(cap, position, ctx);
	}
	
}
