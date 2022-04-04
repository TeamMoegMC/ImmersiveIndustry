package com.teammoeg.immersiveindustry.content.rotarykiln;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorageAdvanced;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.teammoeg.immersiveindustry.IIContent.IIMultiblocks;
import com.teammoeg.immersiveindustry.IIContent.IITileTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class RotaryKilnTileEntity extends MultiblockPartTileEntity<RotaryKilnTileEntity>
		implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
		IEBlockInterfaces.IInteractionObjectIE {
	int angle;//angle for animation in degrees
	private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
	public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(32000);
	EnergyHelper.IEForgeEnergyWrapper wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, null);
	public FluidTank[] tankout = new FluidTank[]{new FluidTank(16000)};
	private static BlockPos itmeout = new BlockPos(1, 0, 7);
	private static BlockPos fluidout = new BlockPos(1, 3, 4);

	private CapabilityReference<IItemHandler> outputItemCap = CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateY();
				return new DirectionalBlockPos(this.getBlockPosForPos(itmeout), fw);
			}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

	private CapabilityReference<IFluidHandler> outputfFluidCap = CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateYCCW();
				return new DirectionalBlockPos(this.getBlockPosForPos(fluidout), fw);
			}, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);

	public RotaryKilnTileEntity() {
		super(IIMultiblocks.ROTARY_KILN, IITileTypes.ROTARY_KILN.get(), false);
	}

	@Override
	protected boolean canDrainTankFrom(int i, Direction side) {
		RotaryKilnTileEntity master = master();
		if (master != null) {
			if (side == null || side == Direction.DOWN) {
				return true;
			}
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
		RotaryKilnTileEntity master = master();
		if (master != null) {
			if (fluidout.down().equals(this.posInMultiblock)) {
				return master.tankout;
			}
		}
		return new FluidTank[0];
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		BlockPos bp = this.getPos();
		return new AxisAlignedBB(bp.getX() - (getFacing().getAxis() == Axis.Z ? 1 : 3),
				bp.getY(),
				bp.getZ() - (getFacing().getAxis() == Axis.X ? 1 : 3),
				bp.getX() + (getFacing().getAxis() == Axis.Z ? 3 : 1),
				bp.getY() + 2,
				bp.getZ() + (getFacing().getAxis() == Axis.X ? 3 : 1));
	}

	@Override
	public void tick() {
		checkForNeedlessTicking();
		angle += 10;
		if (angle >= 360)
			angle = 0;
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
		return ImmutableSet.of(new BlockPos(5, 1, 1));
	}

	@Nonnull
	@Override
	public FluxStorage getFluxStorage() {
		RotaryKilnTileEntity master = this.master();
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
		ItemStackHelper.loadAllItems(nbt, inventory);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		energyStorage.writeToNBT(nbt);
		nbt.put("tankout", tankout[0].writeToNBT(new CompoundNBT()));
		ItemStackHelper.saveAllItems(nbt, inventory);
	}

	@Override
	public boolean isStackValid(int i, ItemStack itemStack) {
		return true;
	}

	@Override
	public int getSlotLimit(int i) {
		return 64;
	}

	@Override
	public void doGraphicalUpdates() {

	}
}
