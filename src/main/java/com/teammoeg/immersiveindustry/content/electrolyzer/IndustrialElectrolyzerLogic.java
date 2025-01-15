package com.teammoeg.immersiveindustry.content.electrolyzer;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.content.rotarykiln.RotaryKilnState;
import com.teammoeg.immersiveindustry.util.CapabilityFacing;
import com.teammoeg.immersiveindustry.util.ChangeDetectedItemHandler;
import com.teammoeg.immersiveindustry.util.IIUtil;
import com.teammoeg.immersiveindustry.util.RecipeHandler;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class IndustrialElectrolyzerLogic
	implements IMultiblockLogic<IndustrialElectrolyzerState>, IClientTickableComponent<IndustrialElectrolyzerState>, IServerTickableComponent<IndustrialElectrolyzerState> {
	static final CapabilityFacing out1 = new CapabilityFacing(2, 0, 3,RelativeBlockFace.RIGHT);
	static final CapabilityFacing out2 = new CapabilityFacing(0, 0, 3,RelativeBlockFace.LEFT);
	static final CapabilityFacing in1 = new CapabilityFacing(2, 0, 1,RelativeBlockFace.RIGHT);
	static final CapabilityFacing in2 = new CapabilityFacing(0, 0, 1,RelativeBlockFace.LEFT);
	static final CapabilityFacing energy =new CapabilityFacing(1, 1, 0,RelativeBlockFace.FRONT);
	public IndustrialElectrolyzerLogic() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tickServer(IMultiblockContext<IndustrialElectrolyzerState> context) {
		IndustrialElectrolyzerState state=context.getState();
		
		int energyConsume = IIConfig.COMMON.electrolyzerBase.get() * 6;
		double elconsume = IIConfig.COMMON.electrodeCost.get();
		tryOutput(context);
		boolean hasElectrode1=state.inventory.getStackInSlot(2).is(IndustrialElectrolyzerContainer.Electrode_Tag);
		boolean hasElectrode2=state.inventory.getStackInSlot(3).is(IndustrialElectrolyzerContainer.Electrode_Tag);
		if(state.hasElectrode1!=hasElectrode1||state.hasElectrode2!=hasElectrode2) {
			state.hasElectrode1=hasElectrode1;
			state.hasElectrode2=hasElectrode2;
			context.markDirtyAndSync();
		}
		
		if (!state.state.isEnabled(context) && state.energyStorage.getEnergyStored() >= energyConsume &&hasElectrode1&&hasElectrode2) {
			RecipeHandler<ElectrolyzerRecipe, Pair<ElectrolyzerRecipe, Map<Integer, Integer>>> handler=state.recipe;
			ChangeDetectedItemHandler inventory=state.inventory;
			if(handler.shouldTestRecipe()) {
				ElectrolyzerRecipe recipeResult = ElectrolyzerRecipe.findRecipe(context.getLevel().getRawLevel(), inventory.getStackInSlot(0), inventory.getStackInSlot(1), state.tank[0].getFluid(), true);
				handler.setRecipe(recipeResult);
				context.markMasterDirty();
			}
			RandomSource rs=context.getLevel().getRawLevel().random;
			boolean lastActive=state.active;
			state.active=false;
			if (handler.shouldTickProcess()) {
				ElectrolyzerRecipe rcp=ElectrolyzerRecipe.recipeList.getById(context.getLevel().getRawLevel(), handler.getLastRecipe());
				energyConsume=rcp.tickEnergy*6;
				if(state.energyStorage.extractEnergy(energyConsume, true)>=energyConsume)
					if(handler.tickProcess(8)) {
						int ele;
						int duracost = 0;
						if (elconsume > 0) {
							duracost = (int) elconsume;
							double npart = Mth.frac(elconsume);
							if (rs.nextDouble() < npart) {
								duracost++;
							}
						}
						if (duracost > 0)
		                    for (ele = 2; ele < 4; ++ele) {
		                        if (inventory.getStackInSlot(ele).hurt(1, rs, null)) {
		                        	inventory.setStackInSlot(ele, ItemStack.EMPTY);
		                        }
		                    }
						state.energyStorage.extractEnergy(energyConsume, false);
						state.active=true;
					}
				
				if(handler.isRecipeFinished()) {
					Pair<ElectrolyzerRecipe, Map<Integer, Integer>> recipeResult = handler.getRecipeResultCache();
					if (recipeResult != null) {
						ElectrolyzerRecipe recipe=recipeResult.getFirst();
						if (inventory.insertItem(4, recipe.output, true).isEmpty()&&state.tank[1].fill(recipe.output_fluid, FluidAction.SIMULATE)>=recipe.output_fluid.getAmount()) {
							inventory.insertItem(4, recipe.output, false);
							state.tank[1].fill(recipe.output_fluid, FluidAction.EXECUTE);
							handler.endProcess();
							 Map<Integer, Integer> ops=recipeResult.getSecond();
							 if(ops!=null) {
								 for(Entry<Integer, Integer> i:ops.entrySet()) {
									 ItemStack stack=inventory.getStackInSlot(i.getKey());
									 int ncount=stack.getCount() +i.getValue();
									 if(ncount>0)
										 inventory.setStackInSlot(i.getKey(), stack.copyWithCount(ncount));
									 else
										 inventory.setStackInSlot(i.getKey(), ItemStack.EMPTY);
								 }
							 }
							
						}
							
					}else {
						handler.endProcess();
					}
				}
				context.markMasterDirty();
			}
			if(state.active!=lastActive)
				context.requestMasterBESync();;

		} 
		
	}

	@Override
	public void tickClient(IMultiblockContext<IndustrialElectrolyzerState> context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IndustrialElectrolyzerState createInitialState(IInitialMultiblockContext<IndustrialElectrolyzerState> capabilitySource) {
		return new IndustrialElectrolyzerState(capabilitySource);
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
		return t->Shapes.block();
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<IndustrialElectrolyzerState> ctx, CapabilityPosition position, Capability<T> cap) {
		return ctx.getState().capabilities.getCapability(cap, position, ctx);
	}

	@Override
	public InteractionResult click(IMultiblockContext<IndustrialElectrolyzerState> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
		
		if(posInMultiblock.getY()==2) {
				if(player.getItemInHand(hand).is(IndustrialElectrolyzerContainer.Electrode_Tag)) {
					ChangeDetectedItemHandler handler=ctx.getState().inventory;
						for (int i = 2; i <= 3; i++) {
							if (handler.getStackInSlot(i).isEmpty()) {
								handler.setStackInSlot(i, ItemHandlerHelper.copyStackWithSize(player.getItemInHand(hand), 1));
								player.getItemInHand(hand).shrink(1);
								return InteractionResult.sidedSuccess(ctx.getLevel().getRawLevel().isClientSide);
							}
						}
				}
			}
		
		return IMultiblockLogic.super.click(ctx, posInMultiblock, player, hand, absoluteHit, isClient);
	}
	public boolean tryOutput(IMultiblockContext<IndustrialElectrolyzerState> context) {
		IndustrialElectrolyzerState state=context.getState();
		boolean update = FluidUtils.multiblockFluidOutput(state.outFluidCap1, state.tank[1], 0, 0, null);
		update |= FluidUtils.multiblockFluidOutput(state.outFluidCap2, state.tank[1], 0, 0, null);
		if(context.getLevel().shouldTickModulo(8)){
			final IItemHandlerModifiable inventory = state.inventory;
			update|=IIUtil.outputItem(inventory, state.outInvCap1, 4);
			update|=IIUtil.outputItem(inventory, state.outInvCap2, 4);
		}
		return update;
	}
}
