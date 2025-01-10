package com.teammoeg.immersiveindustry.content.rotarykiln;

import blusunrize.immersiveengineering.api.IEEnums;
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
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.IIContent.IITileTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RotaryKilnBlockEntity extends MultiblockPartTileEntity<RotaryKilnBlockEntity>
		implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
		IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile {
	public int processMax = 0;
	public int process = 0;
	public int cd = 0;
	public int tickEnergy = 0;
	boolean active;
	public int angle;// angle for animation in degrees
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	private List<RotaryKilnProcess> processes = new ArrayList<>();
	public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(32000);
	EnergyHelper.IEForgeEnergyWrapper wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, null);
	public FluidTank[] tankout = new FluidTank[]{new FluidTank(32000)};
	private static BlockPos itemout = new BlockPos(1, 0, 7);
	private static BlockPos fluidout = new BlockPos(1, 3, 4);

	private CapabilityReference<IItemHandler> outputItemCap = CapabilityReference.forTileEntityAt(this, () -> {
		Direction fw = getFacing().rotateY();
		return new DirectionalBlockPos(this.getBlockPosForPos(itemout), fw);
	}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

	private CapabilityReference<IFluidHandler> outputfCap = CapabilityReference.forTileEntityAt(this, () -> {
		return new DirectionalBlockPos(this.getBlockPosForPos(fluidout),Direction.DOWN);
	}, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

	public RotaryKilnBlockEntity() {
		super(IIMultiblocks.ROTARY_KILN, IITileTypes.ROTARY_KILN.get(), false);
	}

	@Override
	protected boolean canDrainTankFrom(int i, Direction side) {
		RotaryKilnBlockEntity master = master();
		if (master != null) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean canFillTankFrom(int arg0, Direction arg1, FluidStack arg2) {
		return false;
	}


	@Nonnull
	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
		RotaryKilnBlockEntity master = master();
		if (master != null) {
			if (this.posInMultiblock.getZ() == 4 && this.posInMultiblock.getY() == 2
					&& this.posInMultiblock.getX() == 1) {
				return master.tankout;
			}
		}
		return new FluidTank[0];
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		BlockPos bp = this.getPos();
		if (!isDummy()) {
			return new AxisAlignedBB(bp.getX() - (getFacing().getAxis() == Axis.Z ? 1 : 3), bp.getY() - 1,
					bp.getZ() - (getFacing().getAxis() == Axis.X ? 1 : 3),
					bp.getX() + (getFacing().getAxis() == Axis.Z ? 2 : 4), bp.getY() + 3,
					bp.getZ() + (getFacing().getAxis() == Axis.X ? 2 : 4));
		}
		return new AxisAlignedBB(bp);
	}
	private ItemStack fillInSlot(int slot,ItemStack tofill) {
		ItemStack out = inventory.get(slot);
		
		if (out.isEmpty()) {
			inventory.set(slot, tofill);
			return ItemStack.EMPTY;
		} else if (out.getCount() < out.getMaxStackSize()
				&& ItemHandlerHelper.canItemStacksStack(out, tofill)) {
			int amount = Math.min(out.getMaxStackSize() - out.getCount(), tofill.getCount());
			tofill.shrink(amount);
			out.grow(amount);
		}
		return tofill;
	}
	@Override
	public void tick() {
		checkForNeedlessTicking();
		if (!isDummy()) {
			if (!world.isRemote) {
				int energyConsume = this.tickEnergy;
				tryOutput();
				boolean flag = false;
				if (!isRSDisabled() && energyStorage.getEnergyStored() >= energyConsume) {
					process = processMax = 0;
					if (!processes.isEmpty()) {
						int lp = 0;
						int lpm = 0;
						for (RotaryKilnProcess p : processes) {
							lp = p.tick(lp + lpm / 16 - 2);
							lpm = p.processMax;
						}
						if(!processes.isEmpty()) {
							RotaryKilnProcess p=processes.get(0);
							processMax=p.processMax;
							process=p.process;
							if(p.finished()) {
								p.result=fillInSlot(3,p.result);
								p.sresult=fillInSlot(4,p.sresult);
								if (!p.fresult.isEmpty())
									p.fresult.shrink(tankout[0].fill(p.fresult, FluidAction.EXECUTE));
							}
						}
						energyStorage.extractEnergy(energyConsume, false);
						if (!active)
							active = true;
						processes.removeIf(RotaryKilnProcess::removable);
						this.markContainingBlockForUpdate(null);
					} else {
						
						if (active) {
							active = false;
							this.tickEnergy = 0;
							flag = true;
						}
					}
					if (cd > 0)
						cd--;
					if (!inventory.get(2).isEmpty()) {
						if (processes.size() < 16 && cd <= 0) {
							RotaryKilnRecipe recipe = getRecipe(2);
							if (recipe != null) {
								inventory.get(2).shrink(recipe.input.getCount());
								processes.add(
										new RotaryKilnProcess(recipe.output.copy(), recipe.output_fluid.copy(), recipe.time,recipe.secoutput,recipe.secoutputchance));
								cd = recipe.time / 16;
								this.tickEnergy = Math.max(tickEnergy, recipe.tickEnergy);
							}
						}
					} else if (!inventory.get(0).isEmpty() || !inventory.get(1).isEmpty()) {
						inventory.set(2, inventory.get(1));
						inventory.set(1, inventory.get(0).split(16));
					}
					if (flag)
						this.markContainingBlockForUpdate(null);
				} else if (active) {
					active = false;
					this.markContainingBlockForUpdate(null);
				}
			} else if (active) {
				angle += 10;
				if (angle >= 360)
					angle = 0;
			}
		}
	}

	public void tryOutput() {
		boolean update = false;
		if (this.tankout[0].getFluidAmount() > 0) {
			FluidStack out = Utils.copyFluidStackWithAmount(this.tankout[0].getFluid(),
					Math.min(this.tankout[0].getFluidAmount(), 80), false);
			if (outputfCap.isPresent()) {
				IFluidHandler output = outputfCap.getNullable();
				int accepted = output.fill(out, FluidAction.SIMULATE);

				if (accepted > 0) {
					int drained = output.fill(
							Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false),
							FluidAction.EXECUTE);
					this.tankout[0].drain(drained, FluidAction.EXECUTE);
					out.shrink(accepted);
					update |= true;
				}
			}
		}

		if (this.outputItemCap.isPresent() && this.world.getGameTime() % 8L == 0L) {

			if (!this.inventory.get(3).isEmpty()) {
				ItemStack stack = ItemHandlerHelper.copyStackWithSize(this.inventory.get(3), 1);
				stack = Utils.insertStackIntoInventory(this.outputItemCap, stack, false);
				if (stack.isEmpty()) {
					this.inventory.get(3).shrink(1);
					if (this.inventory.get(3).getCount() <= 0) {
						this.inventory.set(3, ItemStack.EMPTY);
					}
					update |= true;
				}
				
			}
			if (!this.inventory.get(4).isEmpty()) {
				ItemStack stack = ItemHandlerHelper.copyStackWithSize(this.inventory.get(4), 1);
				stack = Utils.insertStackIntoInventory(this.outputItemCap, stack, false);
				if (stack.isEmpty()) {
					this.inventory.get(4).shrink(1);
					if (this.inventory.get(4).getCount() <= 0) {
						this.inventory.set(4, ItemStack.EMPTY);
					}
					update |= true;
				}
			}

		}
		if (update) {
			this.markDirty();
			this.markContainingBlockForUpdate(null);
		}
	}

	@Nonnull
	@Override
	public VoxelShape getBlockBounds(@Nullable ISelectionContext iSelectionContext) {
		return VoxelShapes.fullCube();
	}

	@Nullable
	@Override
	public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
		return master();
	}

	@Override
	public boolean canUseGui(PlayerEntity playerEntity) {
		return formed;
	}

	@Nonnull
	@Override
	public IEEnums.IOSideConfig getEnergySideConfig(Direction facing) {
		return this.formed && this.isEnergyPos() ? IEEnums.IOSideConfig.INPUT : IEEnums.IOSideConfig.NONE;
	}

	public Set<BlockPos> getEnergyPos() {
		return ImmutableSet.of(new BlockPos(1, 0, 0));
	}

	public boolean isEnergyPos() {
		return this.getEnergyPos().contains(this.posInMultiblock);
	}

	@Override
	public void postEnergyTransferUpdate(int energy, boolean simulate) {
		if (!simulate) {
			this.updateMasterBlock(null, energy != 0);
		}
	}

	@Override
	public Set<BlockPos> getRedstonePos() {
		return ImmutableSet.of(new BlockPos(0, 1, 5));
	}

	@Nonnull
	@Override
	public FluxStorage getFluxStorage() {
		RotaryKilnBlockEntity master = this.master();
		return master != null ? master.energyStorage : this.energyStorage;
	}

	@Nullable
	@Override
	public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction facing) {
		return this.formed && this.isEnergyPos() ? this.wrapper : null;
	}

	@Nullable
	@Override
	public NonNullList<ItemStack> getInventory() {
		if (master() != null)
			return master().inventory;
		return this.inventory;
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		energyStorage.readFromNBT(nbt);
		tankout[0].readFromNBT(nbt.getCompound("tankout"));
		active = nbt.getBoolean("active");
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		if (!descPacket) {
			cd = nbt.getInt("next");
			tickEnergy = nbt.getInt("tickEnergy");
			ListNBT r = nbt.getList("queue", 10);
			processes.clear();
			r.stream().map(e -> (CompoundNBT) e).map(RotaryKilnProcess::new).forEach(processes::add);
			ItemStackHelper.loadAllItems(nbt, inventory);
		}
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		energyStorage.writeToNBT(nbt);
		nbt.put("tankout", tankout[0].writeToNBT(new CompoundNBT()));
		nbt.putBoolean("active", active);
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		if (!descPacket) {
			nbt.putInt("next", cd);
			nbt.putInt("tickEnergy", tickEnergy);
			ListNBT nl = new ListNBT();
			for (RotaryKilnProcess p : processes)
				nl.add(p.serialize());
			nbt.put("queue", nl);
			ItemStackHelper.saveAllItems(nbt, inventory);
		}

	}

	LazyOptional<IItemHandler> inHandler = registerConstantCap(new IEInventoryHandler(1, this, 0, true, false));
	LazyOptional<IItemHandler> outHandler = registerConstantCap(new IEInventoryHandler(2, this, 3, false, true));

	@Nonnull
	@Override
	public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {

		if (facing != null && this.posInMultiblock.getX() == 1) {
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (this.posInMultiblock.getZ() == 0 && this.posInMultiblock.getY() == 2)
					return inHandler.cast();
				else if (this.posInMultiblock.getZ() == 6 && this.posInMultiblock.getY() == 0)
					return outHandler.cast();
				return LazyOptional.empty();
			}
		}
		return super.getCapability(capability, facing);
	}

	@Nullable
	public RotaryKilnRecipe getRecipe(int slot) {
		RotaryKilnRecipe recipe = RotaryKilnRecipe.findRecipe(inventory.get(slot));
		if (recipe == null)
			return null;
		return recipe;
	}

	@Override
	public int[] getCurrentProcessesStep() {
		RotaryKilnBlockEntity master = master();
		if (master != this && master != null)
			return master.getCurrentProcessesStep();
		return new int[] { processMax - process };
	}

	@Override
	public int[] getCurrentProcessesMax() {
		RotaryKilnBlockEntity master = master();
		if (master != this && master != null)
			return master.getCurrentProcessesMax();
		return new int[] { processMax };
	}

	@Override
	public boolean isStackValid(int i, ItemStack itemStack) {
		if (i == 0)
			return true;
		return false;
	}

	@Override
	public int getSlotLimit(int i) {
		return 64;
	}

	@Override
	public void doGraphicalUpdates() {

	}
}
