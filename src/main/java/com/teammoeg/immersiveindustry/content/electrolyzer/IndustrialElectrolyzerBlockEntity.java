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
import com.google.common.collect.ImmutableSet;
import com.teammoeg.immersiveindustry.IIConfig;
import com.teammoeg.immersiveindustry.IIContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class IndustrialElectrolyzerBlockEntity extends MultiblockPartTileEntity<IndustrialElectrolyzerBlockEntity>
		implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
		IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile {
	public int process = 0;
	public int processMax = 0;
	public int tickEnergy = 0;
	public ItemStack result = ItemStack.EMPTY;
	public FluidStack resultFluid = FluidStack.EMPTY;
	public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(32000);
	EnergyHelper.IEForgeEnergyWrapper wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, null);
	public FluidTank[] tank = new FluidTank[]{new FluidTank(16000, ElectrolyzerRecipe::isValidRecipeFluid),
			new FluidTank(16000)};
	private static BlockPos out1 = new BlockPos(3, 0, 3);
	private static BlockPos out2 = new BlockPos(-1, 0, 3);

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
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	public IndustrialElectrolyzerBlockEntity() {
		super(IIContent.IIMultiblocks.IND_ELE, IIContent.IITileTypes.IND_ELE.get(), true);
		
	}

	@Nonnull
	@Override
	public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx) {
		return VoxelShapes.fullCube();
	}

	@Nonnull
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
		IndustrialElectrolyzerBlockEntity master = master();
		if (master != null) {
			if(side.getYOffset()==0&&this.posInMultiblock.getY()==0&&this.posInMultiblock.getX()!=1) {
				if(this.posInMultiblock.getZ()==1)
					return master.tank;
				if(this.posInMultiblock.getZ()==3)
					return master.tank;
			}
		}
		return new FluidTank[0];
	}

	@Override
	protected boolean canFillTankFrom(int i, Direction side, FluidStack fluidStack) {
		IndustrialElectrolyzerBlockEntity master = master();
		if(i!=0)return false;
		if (master != null) {
			if(side.getYOffset()==0&&this.posInMultiblock.getY()==0&&this.posInMultiblock.getX()!=1) {
				if(this.posInMultiblock.getZ()==1)
					return ElectrolyzerRecipe.isValidRecipeFluid(fluidStack);
			}
		}
		return false;
	}

	@Override
	protected boolean canDrainTankFrom(int i, Direction side) {
		IndustrialElectrolyzerBlockEntity master = master();
		if(i!=1)return false;
		if (master != null) {
			if(side.getYOffset()==0&&this.posInMultiblock.getY()==0&&this.posInMultiblock.getX()!=1) {
				if(this.posInMultiblock.getZ()==3)
					return true;
			}
		}
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
		if (!descPacket) {
			result = ItemStack.read(nbt.getCompound("result"));
			resultFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("result_fluid"));
			tickEnergy = nbt.getInt("tickEnergy");
			ItemStackHelper.loadAllItems(nbt, inventory);
		}
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		energyStorage.writeToNBT(nbt);
		nbt.put("tank0", tank[0].writeToNBT(new CompoundNBT()));
		nbt.put("tank1", tank[1].writeToNBT(new CompoundNBT()));
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		if (!descPacket) {
			nbt.put("result", result.serializeNBT());
			nbt.put("result_fluid", resultFluid.writeToNBT(new CompoundNBT()));
			nbt.putInt("tickEnergy", tickEnergy);
			ItemStackHelper.saveAllItems(nbt, inventory);
		}
	}

	@Override
	public void tick() {
		checkForNeedlessTicking();
		if (!isDummy()) {
			if (!world.isRemote) {
				int energyConsume = this.tickEnergy * 6;
				double elconsume = IIConfig.COMMON.electrodeCost.get();
				tryOutput();
				if (!isRSDisabled() && energyStorage.getEnergyStored() >= energyConsume && hasElectrodes()) {
					if (process > 0) {
						int ele;
						int duracost = 0;
						if (elconsume > 0) {
							duracost = (int) elconsume;
							double npart = MathHelper.frac(elconsume);
							if (this.getWorld().rand.nextInt(1000) < npart * 1000) {
								duracost++;
							}
						}
						
						if (duracost > 0)
                            for (ele = 2; ele < 4; ++ele) {
                                if (this.inventory.get(ele).attemptDamageItem(duracost, Utils.RAND, null)) {
                                    this.inventory.set(ele, ItemStack.EMPTY);
                                }
                            }
						process -= 8;
						energyStorage.extractEnergy(energyConsume, false);
						this.markContainingBlockForUpdate(null);
						return;
					}
					if (!result.isEmpty()) {
                        if (inventory.get(4).isEmpty()) {
                            inventory.set(4, result);
                            result = ItemStack.EMPTY;
							process = processMax = 0;
							tickEnergy = 0;
							this.markContainingBlockForUpdate(null);
                        } else if (inventory.get(4).isItemEqual(result)) {
							inventory.get(4).grow(result.getCount());
							result = ItemStack.EMPTY;
							process = processMax = 0;
							tickEnergy = 0;
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
							ItemStack[] consumed=new ItemStack[recipe.inputs.length];
							int ix=-1;
							outer: for (IngredientWithSize iws : recipe.inputs) {
								for (int i = 0; i < 2; i++) {
									if (iws.test(inventory.get(i))) {
										consumed[++ix]=ItemHandlerHelper.copyStackWithSize(inventory.get(i),iws.getCount());
										Utils.modifyInvStackSize(inventory, i, -iws.getCount());
										continue outer;
									}
								}
								// why not fit? fast fail.
								ff:for(ItemStack is:consumed) {
									if(is==null)continue;
									for (int i = 0; i < 2; i++) {
										if(ItemHandlerHelper.canItemStacksStack(is,inventory.get(i))) {
											Utils.modifyInvStackSize(inventory, i,is.getCount());
											continue ff;
										}
									}
									for (int i = 0; i < 2; i++) {
										if(inventory.get(i).isEmpty()) {
											inventory.set(i, is);
											break;
										}
									}
								}
								this.markContainingBlockForUpdate(null);
								return;
							}
						}
						this.processMax = this.process = recipe.time;
						this.tickEnergy = recipe.tickEnergy;
						if (recipe.input_fluid != null)
							tank[0].drain(recipe.input_fluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
						result = recipe.output.copy();
						if (recipe.output_fluid != null) {
							FluidStack matching = recipe.output_fluid;
							if (!matching.isEmpty())
								resultFluid = matching.copy();
						}
						this.markContainingBlockForUpdate(null);
					}
				} else if (process != processMax) {
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

                if (accepted > 0) {
                    int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false), FluidAction.EXECUTE);
                    this.tank[1].drain(drained, FluidAction.EXECUTE);
                    out.shrink(accepted);
                    update |= true;
                }
            }
        }
        if (!inventory.get(4).isEmpty() && world.getGameTime() % 8 == 0) {
            boolean succeed = false;
            if (outputCap1.isPresent()) {
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(inventory.get(4), 1);
                stack = Utils.insertStackIntoInventory(outputCap1, stack, false);
                if (stack.isEmpty()) {
                    this.inventory.get(4).shrink(1);
                    succeed = true;
                    if (this.inventory.get(4).getCount() <= 0)
                        this.inventory.set(4, ItemStack.EMPTY);
                }
            }
			if(!succeed&&outputCap2.isPresent()) {
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(inventory.get(4), 1);
                stack = Utils.insertStackIntoInventory(outputCap2, stack, false);
                if (stack.isEmpty()) {
                    this.inventory.get(4).shrink(1);
                    if (this.inventory.get(4).getCount() <= 0)
                        this.inventory.set(4, ItemStack.EMPTY);
                }
            }
		}

		if (update) {
			this.markDirty();
			this.markContainingBlockForUpdate(null);
		}
	}

	public boolean hasElectrodes() {
        for (int i = 2; i < 4; ++i) {
            if (this.inventory.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

	@Nullable
	public ElectrolyzerRecipe getRecipe() {
        ElectrolyzerRecipe recipe = ElectrolyzerRecipe.findRecipe(inventory.get(0), inventory.get(1),
                tank[0].getFluid(), true);
        if (recipe == null)
            return null;
        if (inventory.get(4).isEmpty() || (ItemStack.areItemsEqual(inventory.get(4), recipe.output)
                && inventory.get(4).getCount() + recipe.output.getCount() <= getSlotLimit(4))) {
            return recipe;
        }
        return null;
    }

    LazyOptional<IItemHandler> inHandler = registerConstantCap(new IEInventoryHandler(2, this, 0, true, false));
    LazyOptional<IItemHandler> outHandler = registerConstantCap(new IEInventoryHandler(1, this, 4, false, true));

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {

        if (facing != null && facing.getYOffset() == 0 && this.posInMultiblock.getY() == 0 && this.posInMultiblock.getX() != 1) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                if (this.posInMultiblock.getZ() == 1)
                    return inHandler.cast();
                else if (this.posInMultiblock.getZ() == 3)
                    return outHandler.cast();
				return LazyOptional.empty();
			}
		}
		return super.getCapability(capability, facing);
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		BlockPos bp = this.getPos();
		if (!isDummy()) {
			return new AxisAlignedBB(bp.getX() - (getFacing().getAxis() == Axis.Z ? 2 : 1),
					bp.getY() - 1,
					bp.getZ() - (getFacing().getAxis() == Axis.X ? 2 : 1),
					bp.getX() + (getFacing().getAxis() == Axis.Z ? 3 : 2),
					bp.getY() + 2,
					bp.getZ() + (getFacing().getAxis() == Axis.X ? 3 : 2));
		}
		return new AxisAlignedBB(bp);
	}
	@Nullable
	@Override
	public NonNullList<ItemStack> getInventory() {
		if(master()!=null)
			return master().inventory;
		return this.inventory;
	}

	@Override
	public boolean isStackValid(int slot, ItemStack stack) {
		if(master()==null)return false;
		ItemStack s0=master().inventory.get(0);
		ItemStack s1=master().inventory.get(1);
		if (slot == 0)
			return stack.isItemEqual(s0)||(!stack.isItemEqual(s1))&&ElectrolyzerRecipe.isValidRecipeInput(stack);
		else if (slot == 1)
			return stack.isItemEqual(s1)||(!stack.isItemEqual(s0))&&ElectrolyzerRecipe.isValidRecipeInput(stack);
		return false;
	}


	@Nonnull
	@Override
	public IEEnums.IOSideConfig getEnergySideConfig(Direction facing) {
		return this.formed && this.isEnergyPos() ? IEEnums.IOSideConfig.INPUT : IEEnums.IOSideConfig.NONE;
	}

	@Nonnull
	@Override
	public FluxStorage getFluxStorage() {
		IndustrialElectrolyzerBlockEntity master = this.master();
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
		return ImmutableSet.of(new BlockPos(1, 1, 4));
	}

	@Nullable
	@Override
	public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
		return this.formed && this.isEnergyPos() ? this.wrapper : null;
	}

	@Override
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

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public void doGraphicalUpdates() {

	}

	@Override
	public int[] getCurrentProcessesStep() {
		IndustrialElectrolyzerBlockEntity master = master();
		if (master != this && master != null)
			return master.getCurrentProcessesStep();
		return new int[]{processMax - process};
	}

	@Override
	public int[] getCurrentProcessesMax() {
		IndustrialElectrolyzerBlockEntity master = master();
		if (master != this && master != null)
			return master.getCurrentProcessesMax();
		return new int[]{processMax};
	}
}