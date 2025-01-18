package com.teammoeg.immersiveindustry.content.carkiln;

import java.util.function.Consumer;
import java.util.function.Function;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.content.crucible.CrucibleRecipe;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerRecipe;
import com.teammoeg.immersiveindustry.content.electrolyzer.IndustrialElectrolyzerState;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnLogic;
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
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
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
				}
				if(recipeResult!=null) {
					state.maxProcessCount=64;
					if(recipeResult.recipe().input_fluid!=null) {
						state.maxProcessCount=state.tank.getFluidAmount()/recipeResult.recipe().input_fluid.getAmount();
					}
					//System.out.println(recipeResult.recipe().maxProcess+","+recipeResult.getMaxRuns(inventory)+","+state.maxProcessCount);
					state.maxProcessCount=Math.min(Math.min(recipeResult.getMaxRuns(inventory), recipeResult.recipe().maxProcess),state.maxProcessCount);
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
						recipeResult.runOperations(inventory,state.maxProcessCount);
						if(recipe.input_fluid!=null)
						state.tank.drain(recipe.input_fluid.getAmount()*state.maxProcessCount, FluidAction.EXECUTE);
						for(ItemStack output:recipe.output) {
							ItemHandlerHelper.insertItem(state.result, output.copyWithCount(output.getCount()*state.maxProcessCount), false);
						}
						handler.endProcess();
						state.maxProcessCount=0;
						context.markDirtyAndSync();
					} else {
						handler.endProcess();
						state.maxProcessCount=0;
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
		CarKilnState state = context.getState();
		if(!state.active)
			return;
		final IMultiblockLevel level = context.getLevel();
		if(!state.isSoundPlaying.getAsBoolean())
		{
			final Vec3 soundPos = level.toAbsolute(new Vec3(1.5, 1.5, 1.5));
			state.isSoundPlaying = MultiblockSound.startSound(
					() -> state.active, context.isValid(), soundPos, IESounds.arcFurnace, 0.075f
			);
		}
	}

	@Override
	public CarKilnState createInitialState(IInitialMultiblockContext<CarKilnState> capabilitySource) {
		return new CarKilnState(capabilitySource);
	}
	static VoxelShape getShape(BlockPos pos) {
		if(pos.getZ()<4&&pos.getY()<2) {
			return Shapes.block();
		}
		if(pos.getY()==3&&pos.getX()==1&&pos.getZ()==0) {
			return Shapes.or(Shapes.box(0, 0, 0, 1, 1, .5), Shapes.box(.125, .125, .5, .875, .875, 1));
		}
		if(pos.getY()==2&&pos.getX()==1&&pos.getZ()==0) {
			return  Shapes.or(Shapes.box(0, 0, 0, 1, .5, 1), Shapes.box(.3125, 0, .0625, .6825, 1, .4375));
		}
		if(pos.getY()==3&&pos.getX()==1&&pos.getZ()==1) {
			return  Shapes.box(.125, 0, 0, .875, .875, .875);
		}
		if(pos.getY()==2&&pos.getX()==1&&pos.getZ()==1) {
			return  Shapes.or(Shapes.box(0, 0, 0, 1, .8125, 1), Shapes.box(.125, 0, .125, .875, 1, .875));
		}

		VoxelShape base;
		if(pos.getY()==2)
			base=Shapes.box(0, 0, 0, 1, .5, 1);
		else if(pos.getY()==0) {
			base=Shapes.box(0, 0, 0, 1, .75, 1);
		}else
			base=Shapes.empty();
		
		if(pos.getY()>=2) {
			if(pos.getX()==0)
				base=Shapes.or(base, Shapes.box(.125, 0, 0, .375, 1, 1));
			else if(pos.getX()==2)
				base=Shapes.or(base, Shapes.box(.625, 0, 0, .875, 1, 1));
			if(pos.getZ()==3) {
				base=Shapes.or(base, Shapes.box(0, 0, .5, 1, 1, 1));
				if(pos.getY()==4)
					base=Shapes.or(base, Shapes.box(0, .25, .25, 1, .75, 1));
			}
		}
		if(pos.getZ()==4) {
			if(pos.getX()==0) {
				base=Shapes.or(base, Shapes.box(0, 0, 0, .625, 1, 1));
			}else if(pos.getX()==2) {
				base=Shapes.or(base, Shapes.box(.375, 0, 0, 1, 1, 1));
			}
		}
		

		return base;
	}
	static final Function<BlockPos, VoxelShape> shapeCache=Util.memoize(CarKilnLogic::getShape);
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
	
		return shapeCache;
	}
	public boolean tryOutput(IMultiblockContext<CarKilnState> context) {
		CarKilnState state = context.getState();
		boolean update=false;
		if (context.getLevel().shouldTickModulo(16)) {
			final IItemHandlerModifiable inventory = state.inventory;
			for(CapabilityReference<IItemHandler> c:state.outputItemCap)
				if(c.isPresent()) {
					update|=IIUtil.outputItem(inventory, c, 4);
					update|=IIUtil.outputItem(inventory, c, 5);
					update|=IIUtil.outputItem(inventory, c, 6);
					update|=IIUtil.outputItem(inventory, c, 7);
					update|=IIUtil.outputItem(inventory, c, 8);
				}
		}
		return update;
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<CarKilnState> ctx, CapabilityPosition position, Capability<T> cap) {
		return ctx.getState().capabilities.getCapability(cap, position, ctx);
	}

	@Override
	public void dropExtraItems(CarKilnState state, Consumer<ItemStack> drop) {
		MBInventoryUtils.dropItems(state.inventory, drop);
		MBInventoryUtils.dropItems(state.result, drop);
	}
	
}
