/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Immersive Industry.
 *
 * Immersive Industry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Immersive Industry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Immersive Industry. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.immersiveindustry.content.electrolyzer;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorageAdvanced;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class IndustrialElectrolyzerTileEntity extends MultiblockPartTileEntity<IndustrialElectrolyzerTileEntity>
		implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
		IEBlockInterfaces.IInteractionObjectIE {
	public int process = 0;
	public int processMax = 0;
	public ItemStack result = ItemStack.EMPTY;
	public FluidStack resultFluid = FluidStack.EMPTY;
	public final int energyConsume;
	public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(32000);
	EnergyHelper.IEForgeEnergyWrapper wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, null);
	public FluidTank[] tank = new FluidTank[] { new FluidTank(16000, ElectrolyzerRecipe::isValidRecipeFluid),
			new FluidTank(16000) };
	private static BlockPos out1=new BlockPos(3,0,3);
	private static BlockPos out2=new BlockPos(-1,0,3);
	private CapabilityReference<IItemHandler> outputCap1 = CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateYCCW();
				return new DirectionalBlockPos(this.getBlockPosForPos(out1), fw);
			}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
	private CapabilityReference<IItemHandler> outputCap2 = CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateY();
				return new DirectionalBlockPos(this.getBlockPosForPos(out2),fw);
			}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
	private CapabilityReference<IFluidHandler> outputfCap1 = CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateYCCW();
				return new DirectionalBlockPos(this.getBlockPosForPos(out1), fw);
			}, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
	private CapabilityReference<IFluidHandler> outputfCap2 = CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateY();
				return new DirectionalBlockPos(this.getBlockPosForPos(out2),fw);
			}, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
	public IFluidTank[] iotank=new IFluidTank[] {
			new ICapFluidTank(tank[0]) {
				@Override
				public FluidStack drain(int maxDrain, FluidAction action) {
					return FluidStack.EMPTY;
				}
				@Override
				public FluidStack drain(FluidStack resource, FluidAction action) {
					return FluidStack.EMPTY;
				}
			},
			new ICapFluidTank(tank[1]) {
				@Override
				public int fill(FluidStack resource, FluidAction action) {
					return 0;
				}
			}
	};
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	static class ICapFluidTank implements IFluidTank{
		final FluidTank innertank;
		
		public ICapFluidTank(FluidTank tank) {
			this.innertank = tank;
		}

		@Override
		public FluidStack getFluid() {
			return innertank.getFluid();
		}

		@Override
		public int getFluidAmount() {
			return innertank.getFluidAmount();
		}

		@Override
		public int getCapacity() {
			return innertank.getCapacity();
		}

		@Override
		public boolean isFluidValid(FluidStack stack) {
			return innertank.isFluidValid(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return innertank.fill(resource, action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			return innertank.drain(maxDrain, action);
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			return innertank.drain(resource, action);
		}
		
	}
	public IndustrialElectrolyzerTileEntity() {
		super(IIContent.IIMultiblocks.IND_ELE, IIContent.IITileTypes.IND_ELE.get(), true);
		energyConsume = IIConfig.COMMON.electrolyzerConsume.get() * 2;
	}

	@Nonnull
	@Override
	public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx) {
		return VoxelShapes.fullCube();
	}

	@Nonnull
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
		IndustrialElectrolyzerTileEntity master = master();
		if (master != null) {
			if(side.getYOffset()==0&&this.offsetToMaster.getY()==-1&&this.offsetToMaster.getX()!=0) {
				if(this.offsetToMaster.getZ()==-1)
					return new IFluidTank[] { master.iotank[0] };
				if(this.offsetToMaster.getZ()==1)
					return new IFluidTank[] { master.iotank[1] };
			}
		}
		return new FluidTank[0];
	}

	@Override
	protected boolean canFillTankFrom(int i, Direction direction, FluidStack fluidStack) {
		return ElectrolyzerRecipe.isValidRecipeFluid(fluidStack);
	}

	@Override
	protected boolean canDrainTankFrom(int i, Direction direction) {
		return false;
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		energyStorage.readFromNBT(nbt);
		tank[0].readFromNBT(nbt.getCompound("tank0"));
		tank[1].readFromNBT(nbt.getCompound("tank1"));
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		result = ItemStack.read(nbt.getCompound("result"));
		resultFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("result_fluid"));
		ItemStackHelper.loadAllItems(nbt, inventory);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		energyStorage.writeToNBT(nbt);
		nbt.put("tank0", tank[0].writeToNBT(new CompoundNBT()));
		nbt.put("tank1", tank[1].writeToNBT(new CompoundNBT()));
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.put("result", result.serializeNBT());
		nbt.put("result_fluid", resultFluid.writeToNBT(new CompoundNBT()));
		ItemStackHelper.saveAllItems(nbt, inventory);
	}

	@Override
	public void tick() {
		checkForNeedlessTicking();
		if (!isDummy()) {
			if (!world.isRemote) {
				tryOutput();
				if (!isRSDisabled() && energyStorage.getEnergyStored() >= energyConsume) {
					if (process > 0) {
						process--;
						energyStorage.extractEnergy(energyConsume, false);
						this.markContainingBlockForUpdate(null);
						return;
					}
					if (!result.isEmpty()) {
						if (inventory.get(2).isEmpty()) {
							inventory.set(2, result);
							result = ItemStack.EMPTY;
							process = processMax = 0;
							this.markContainingBlockForUpdate(null);
						} else if (inventory.get(2).isItemEqual(result)) {
							inventory.get(2).grow(result.getCount());
							result = ItemStack.EMPTY;
							process = processMax = 0;
							this.markContainingBlockForUpdate(null);
						} else
							return;
					}
					if (!resultFluid.isEmpty()) {
						int filled = tank[1].fill(resultFluid, FluidAction.EXECUTE);
						if (filled < resultFluid.getAmount()) {
							resultFluid.shrink(filled);
							return;
						}
						resultFluid = FluidStack.EMPTY;
					}
					ElectrolyzerRecipe recipe = getRecipe();
					if (recipe != null) {
						
						if (recipe.inputs.length > 0) {
							outer: for (IngredientWithSize iws : recipe.inputs) {
								for (int i = 0; i < 2; i++) {
									if (iws.test(inventory.get(i))) {
										Utils.modifyInvStackSize(inventory, i, -iws.getCount());
										continue outer;
									}
								}
								// why not fit? fast fail.
								this.markContainingBlockForUpdate(null);
								return;
							}
						}
						this.processMax = this.process = recipe.time;
						if (recipe.input_fluid != null)
							tank[0].drain(recipe.input_fluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
						result = recipe.output.copy();
						if (recipe.output_fluid != null) {
							List<FluidStack> matching = recipe.output_fluid.getMatchingFluidStacks();
							if (!matching.isEmpty())
								resultFluid = matching.get(0).copy();
						}
						this.markContainingBlockForUpdate(null);
					}
				} else if (process > 0) {
					process = processMax;
					this.markContainingBlockForUpdate(null);
				}

			}
		}
	}
	public void tryOutput(){
		boolean update=false;
		if(this.tank[1].getFluidAmount() > 0)
		{
			FluidStack out = Utils.copyFluidStackWithAmount(this.tank[1].getFluid(), Math.min(this.tank[1].getFluidAmount(), 80), false);
			if(outputfCap1.isPresent()){
				IFluidHandler output=outputfCap1.getNullable();
				int accepted = output.fill(out, FluidAction.SIMULATE);

				if(accepted > 0)
				{
					int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
					this.tank[1].drain(drained, FluidAction.EXECUTE);
					out.shrink(accepted);
					update |=true;
				}
			}
			if(!out.isEmpty()&&outputfCap2.isPresent()){
				IFluidHandler output=outputfCap2.getNullable();
				int accepted = output.fill(out, FluidAction.SIMULATE);

				if(accepted > 0)
				{
					int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
					this.tank[1].drain(drained, FluidAction.EXECUTE);
					out.shrink(accepted);
					update |=true;
				}
			}
		}
		if(!inventory.get(2).isEmpty()&&world.getGameTime()%8==0)
		{
			boolean succeed=false;
			if(outputCap1.isPresent())
			{
				ItemStack stack = ItemHandlerHelper.copyStackWithSize(inventory.get(2), 1);
				stack = Utils.insertStackIntoInventory(outputCap1, stack, false);
				if(stack.isEmpty())
				{
					this.inventory.get(2).shrink(1);
					succeed=true;
					if(this.inventory.get(2).getCount() <= 0)
						this.inventory.set(2, ItemStack.EMPTY);
				}
			}
			if(!succeed&&outputCap2.isPresent())
			{
				ItemStack stack = ItemHandlerHelper.copyStackWithSize(inventory.get(2), 1);
				stack = Utils.insertStackIntoInventory(outputCap2, stack, false);
				if(stack.isEmpty())
				{
					this.inventory.get(2).shrink(1);
					if(this.inventory.get(2).getCount() <= 0)
						this.inventory.set(2, ItemStack.EMPTY);
				}
			}
		}

		if(update)
		{
			this.markDirty();
			this.markContainingBlockForUpdate(null);
		}
	}
	@Nullable
	public ElectrolyzerRecipe getRecipe() {
		ElectrolyzerRecipe recipe = ElectrolyzerRecipe.findRecipe(inventory.get(0), inventory.get(1),
				tank[0].getFluid(), true);
		if (recipe == null)
			return null;
		if (inventory.get(2).isEmpty() || (ItemStack.areItemsEqual(inventory.get(2), recipe.output)
				&& inventory.get(2).getCount() + recipe.output.getCount() <= getSlotLimit(2))) {
			return recipe;
		}
		return null;
	}

	LazyOptional<IItemHandler> inHandler = registerConstantCap(new IEInventoryHandler(2, this , 0, true , false));
	LazyOptional<IItemHandler> outHandler = registerConstantCap(new IEInventoryHandler(1, this , 2, false, true));
	@Nonnull
	@Override
	public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
		if(facing.getYOffset()==0&&this.offsetToMaster.getY()==-1&&this.offsetToMaster.getX()!=0) {
			if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (this.offsetToMaster.getZ()==-1)
					return inHandler.cast();
				else if(this.offsetToMaster.getZ()==1)
					return outHandler.cast();
				return LazyOptional.empty();
			}
		}
		return super.getCapability(capability, facing);
	}

	@Nullable
	@Override
	public NonNullList<ItemStack> getInventory() {
		if(master()!=null)
			return master().inventory;
		return null;
	}

	@Override
	public boolean isStackValid(int slot, ItemStack stack) {
		if (slot == 0)
			return ElectrolyzerRecipe.isValidRecipeInput(stack);
		else if (slot == 1)
			return ElectrolyzerRecipe.isValidRecipeInput(stack);
		return false;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public void doGraphicalUpdates() {

	}

	@Nonnull
	@Override
	public IEEnums.IOSideConfig getEnergySideConfig(Direction facing) {
		return this.formed && this.isEnergyPos() ? IEEnums.IOSideConfig.INPUT : IEEnums.IOSideConfig.NONE;
	}

	@Nonnull
	@Override
	public FluxStorage getFluxStorage() {
		IndustrialElectrolyzerTileEntity master = this.master();
		return master != null ? master.energyStorage : this.energyStorage;
	}

	public boolean isEnergyPos() {
		return this.getEnergyPos().contains(this.posInMultiblock);
	}

	public Set<BlockPos> getEnergyPos() {
		return ImmutableSet.of(new BlockPos(1, 1, 0));
	}

	@Override
	public Set<BlockPos> getRedstonePos() {
		return ImmutableSet.of(new BlockPos(1, 1, 0));
	}

	@Nullable
	@Override
	public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
		return this.formed && this.isEnergyPos() ? this.wrapper : null;
	}

	public void postEnergyTransferUpdate(int energy, boolean simulate) {
		if (!simulate) {
			this.updateMasterBlock(null, energy != 0);
		}

	}

	@Nullable
	@Override
	public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
		return master();
	}

	@Override
	public boolean canUseGui(PlayerEntity player) {
		return formed;
	}
}