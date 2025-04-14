package com.teammoeg.immersiveindustry.content.chemical_reactor;

import java.util.function.Consumer;
import java.util.function.Function;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.content.electrolyzer.ElectrolyzerRecipe;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ChemicalLogic implements IClientTickableComponent<ChemicalState>, IMultiblockLogic<ChemicalState>, IServerTickableComponent<ChemicalState> {

	static final CapabilityFacing[] out = new CapabilityFacing[] {
		new CapabilityFacing(0, 1, 1, RelativeBlockFace.LEFT),
		new CapabilityFacing(2, 1, 1, RelativeBlockFace.RIGHT),
		new CapabilityFacing(1, 1, 2, RelativeBlockFace.BACK)
	};
	static final CapabilityFacing[] in = new CapabilityFacing[] {
		new CapabilityFacing(0, 2, 1, RelativeBlockFace.LEFT),
		new CapabilityFacing(2, 2, 1, RelativeBlockFace.RIGHT),
		new CapabilityFacing(1, 2, 2, RelativeBlockFace.BACK)
	};
	static final CapabilityFacing[] itemin=new CapabilityFacing[8];
	static {
		int num=0;
		for(int x=0;x<3;x++)
			for(int y=0;y<3;y++)
				if(x!=1||y!=1)
					itemin[num++]= new CapabilityFacing(x, 2, y, RelativeBlockFace.UP);
	}
	static final CapabilityFacing itemout= new CapabilityFacing(1, 0, 1, RelativeBlockFace.DOWN);
	static final CapabilityFacing energy = new CapabilityFacing(1, 3, 1, RelativeBlockFace.UP);
	public ChemicalLogic() {

	}

	@Override
	public void tickServer(IMultiblockContext<ChemicalState> context) {
		ChemicalState state = context.getState();

		int energyConsume = IIConfig.COMMON.chemicalBase.get();
		if (tryOutput(context))
			context.markMasterDirty();

		if (state.state.isEnabled(context) && state.energyStorage.getEnergyStored() >= energyConsume) {
			RecipeHandler<ChemicalRecipe> handler = state.recipe;
			ChangeDetectedItemHandler inventory = state.inventory;
			IFluidHandler fluidInput=state.recipeInputFluidHandler;
			
			if (handler.shouldTestRecipe()) {
				RecipeProcessResult<ChemicalRecipe> recipeResult = ChemicalRecipe.findRecipe(context.getLevel().getRawLevel(), inventory, fluidInput);
				handler.setRecipe(recipeResult);
				context.markMasterDirty();
			}
			boolean lastActive = state.active;
			state.active = false;
			if (handler.shouldTickProcess()) {
				ElectrolyzerRecipe rcp = ElectrolyzerRecipe.recipeList.getById(context.getLevel().getRawLevel(), handler.getLastRecipe());
				energyConsume = rcp.tickEnergy;
				if (state.energyStorage.extractEnergy(energyConsume, true) >= energyConsume)
					if (handler.tickProcess(1)) {
						state.energyStorage.extractEnergy(energyConsume, false);
						state.active = true;
					}

				if (handler.isRecipeFinished()) {
					RecipeProcessResult<ChemicalRecipe> recipeResult = handler.getRecipeResultCache();
					if (recipeResult != null) {
						ChemicalRecipe recipe = recipeResult.recipe();
						boolean isCompletable=true;
						if(recipe.outputs.length>0) {
							int num=3;
							for(ItemStack is:recipe.outputs) {
								if(!inventory.insertItem(num++, is, true).isEmpty()) {
									isCompletable=false;
									break;
								}
							}
						}
						if(isCompletable&&recipe.output_fluids.length>0) {
							int num=0;
							for(FluidStack fs:recipe.output_fluids) {
								if(state.outTank[num++].fill(fs, FluidAction.SIMULATE)<fs.getAmount()) {
									isCompletable=false;
									break;
								}
							}
						}
						if (isCompletable) {
							if(recipe.outputs.length>0) {
								int num=3;
								for(ItemStack is:recipe.outputs) {
									inventory.insertItem(num++, is, false);
								}
							}
							if(recipe.output_fluids.length>0) {
								int num=0;
								for(FluidStack fs:recipe.output_fluids) {
									state.outTank[num++].fill(fs, FluidAction.EXECUTE);
								}
							}
							handler.endProcess();
							recipeResult.runOperations(inventory,fluidInput);
							
						}

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

	public boolean tryOutput(IMultiblockContext<ChemicalState> context) {
		ChemicalState state = context.getState();
		boolean update = false;
		int num=0;
		for(CapabilityReference<IFluidHandler> i:state.outputFluidCap) {
			update|=FluidUtils.multiblockFluidOutput(i, state.outTank[num++], 0, 0, null);
		}
		if (context.getLevel().shouldTickModulo(8)) {
			final IItemHandlerModifiable inventory = state.inventory;
			update |= IIUtil.outputItem(inventory, state.outputItemCap, 3);
			update |= IIUtil.outputItem(inventory, state.outputItemCap, 4);
			update |= IIUtil.outputItem(inventory, state.outputItemCap, 5);
		}
		return update;
	}

	@Override
	public ChemicalState createInitialState(IInitialMultiblockContext<ChemicalState> capabilitySource) {
		return new ChemicalState(capabilitySource);
	}


	@Override
	public void tickClient(IMultiblockContext<ChemicalState> context) {
		ChemicalState state = context.getState();
		if(!state.active)
			return;
		final IMultiblockLevel level = context.getLevel();
		if(!state.isSoundPlaying.getAsBoolean())
		{
			final Vec3 soundPos = level.toAbsolute(new Vec3(1.5, 1.5, 1.5));
			state.isSoundPlaying = MultiblockSound.startSound(
					() -> state.active, context.isValid(), soundPos, IESounds.mixer, 0.075f
			);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(IMultiblockContext<ChemicalState> ctx, CapabilityPosition position, Capability<T> cap) {
		return ctx.getState().capabilities.getCapability(cap, position, ctx);
	}
	@Override
	public void dropExtraItems(ChemicalState state, Consumer<ItemStack> drop) {
		MBInventoryUtils.dropItems(state.inventory, drop);
	}

	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType) {
		return t->Shapes.block();
	}
}
