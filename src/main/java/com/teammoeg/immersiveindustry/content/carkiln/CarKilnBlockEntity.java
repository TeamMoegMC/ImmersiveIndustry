package com.teammoeg.immersiveindustry.content.carkiln;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorageAdvanced;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class CarKilnBlockEntity extends MultiblockBEType<CarKilnBlockEntity>
		implements IEBlockInterfaces.IBlockBounds, EnergyHelper.IIEInternalFluxHandler, IIEInventory,
		IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IMultiblockState {
	public int processMax = 0;
	public int process = 0;
	public int tickEnergy = 0;
	public short modelState;
	int pos;//animation process from 0-52, 0=idle 52=working
	private NonNullList<ItemStack> inventory = NonNullList.withSize(9, ItemStack.EMPTY);
	public FluxStorageAdvanced energyStorage = new FluxStorageAdvanced(32000);
	EnergyHelper.IEForgeEnergyWrapper wrapper = new EnergyHelper.IEForgeEnergyWrapper(this, null);
	boolean resultsempty=true;
	NonNullList<ItemStack> results = NonNullList.withSize(9, ItemStack.EMPTY);
	public FluidTank[] tankinput = new FluidTank[]{new FluidTank(16000)};

	public CarKilnBlockEntity() {
		super(IIMultiblocks.CAR_KILN, IITileTypes.CAR_KILN.get(), false);
	}

	@SuppressWarnings("unchecked")
	private CapabilityReference<IItemHandler>[] outputItemCaps = new CapabilityReference[]{CapabilityReference.forTileEntityAt(this,
			() -> {
				Direction fw = getFacing().rotateY();
				return new DirectionalBlockPos(this.getBlockPosForPos(new BlockPos(0, 0, 5)), fw);
			}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY),
		CapabilityReference.forTileEntityAt(this,
				() -> {
					Direction fw = getFacing().rotateY();
					return new DirectionalBlockPos(this.getBlockPosForPos(new BlockPos(1, 0, 5)), fw);
				}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY),
		CapabilityReference.forTileEntityAt(this,
				() -> {
					Direction fw = getFacing().rotateY();
					return new DirectionalBlockPos(this.getBlockPosForPos(new BlockPos(2, 0, 5)), fw);
				}, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)} ;

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		BlockPos bp = this.getPos();
		if (!isDummy()) {
			return new AxisAlignedBB(
					bp.getX() - (getFacing().getAxis() == Axis.Z ? 1 : 3),
					bp.getY() - 1,
					bp.getZ() - (getFacing().getAxis() == Axis.X ? 1 : 3),
					bp.getX() + (getFacing().getAxis() == Axis.Z ? 2 : 4),
					bp.getY() + 5,
					bp.getZ() + (getFacing().getAxis() == Axis.X ? 2 : 4));
		}
		return new AxisAlignedBB(bp);
	}
	@Override
	public void tick() {
		checkForNeedlessTicking();
		if (!isDummy()) {
			if (!world.isRemote) {
				int energyConsume = this.tickEnergy;
				tryOutput();
				if (!isRSDisabled() && energyStorage.getEnergyStored() >= energyConsume) {
					if (process > 0) {
						if (process > 52 && process < processMax - 23 && resultsempty) {
							CarKilnRecipe recipe = CarKilnRecipe.findRecipe(inventory, tankinput[0].getFluid(), 0, 4);
							if (recipe == null) {
								process = processMax - process;
								processMax = 0;
								return;
							}
							float[] maxprocs = new float[recipe.inputs.length];
							int j=0;
							for(IngredientWithSize iws:recipe.inputs) {
								for(int i=0;i<4;i++) {
									if(iws.testIgnoringSize(inventory.get(i))) {
										maxprocs[j]+=inventory.get(i).getCount()/(float)iws.getCount();
									}
								}
								j++;
							}
							//check max process this time,let's say it 32
							int procnum=32;
							for(int i=0;i<maxprocs.length;i++) {
								procnum=Math.min(procnum,(int)maxprocs[i]);
							}
							if(recipe.input_fluid.getAmount()>0)
								procnum=Math.min(procnum,(tankinput[0].getFluidAmount()-recipe.start_fluid_cost)/recipe.input_fluid.getAmount());
							modelState=(short) procnum;
							//take items;
							for(IngredientWithSize iws:recipe.inputs) {
								int required=iws.getCount()*procnum;
								for(int i=0;i<4;i++) {
									ItemStack cr=inventory.get(i);
									if(iws.testIgnoringSize(cr)) {
										if(required>=cr.getCount()) {
											required-=cr.getCount();
											inventory.set(i, ItemStack.EMPTY);
										} else {
											cr.shrink(required);
											required = 0;
										}
										if (required == 0)
											break;
									}
								}
							}
							if (!recipe.input_fluid.isEmpty()) {
								int tcost=recipe.input_fluid.getAmount()*procnum+recipe.start_fluid_cost;
								
								tankinput[0].drain(tcost,FluidAction.EXECUTE);
							}
							int wked = processMax - process;
							processMax = recipe.time + 104;
							process = processMax - wked;
							int i=0;
							for(ItemStack is:recipe.output) {
								results.set(i++,ItemHandlerHelper.copyStackWithSize(is,is.getCount() * procnum));
							}
							checkResultEmpty();
						}
						process--;
						energyStorage.extractEnergy(energyConsume, false);

						this.markContainingBlockForUpdate(null);
						return;
					}
					modelState=0;
					if(processMax!=0) {
						process = processMax = 0;
						this.markContainingBlockForUpdate(null);
					}
					this.tickEnergy = 0;
					if (!resultsempty) {
						results.replaceAll(result->{
							if(result.isEmpty())return result;
							for (int i = 4; i < 9; i++) {
								ItemStack is = inventory.get(i);
								if (is.isEmpty()) {
									inventory.set(i, result.split(result.getMaxStackSize()));
								} else if (ItemHandlerHelper.canItemStacksStack(is, result)) {
									int fill = is.getMaxStackSize() - is.getCount();
									if (fill >= result.getCount()) {
										is.grow(result.getCount());
										return ItemStack.EMPTY;
									}
									is.grow(fill);
									result.shrink(fill);
								}
							}
							return result;
						});
						checkResultEmpty();
					}
					if(!resultsempty)
						return;
					
					//check has recipe
					CarKilnRecipe recipe = CarKilnRecipe.findRecipe(inventory,tankinput[0].getFluid(),0, 4);
					if (recipe != null) {
						process = processMax = recipe.time + 104;
						modelState=0;
						for(int i=0;i<4;i++)
							modelState+=inventory.get(i).getCount();
						for(int i=0;i<4;i++) {}
						this.tickEnergy = recipe.tickEnergy;
						this.markContainingBlockForUpdate(null);
					}
				} else if (process > 0) {
					process = Math.min(process + 1, processMax - 24);
					this.markContainingBlockForUpdate(null);
				}
			} else {
				int ptm=processMax-process;
				if (process<53)
					pos=process;
				else if (ptm < 53)
					pos=ptm;
				else
					pos=52;
			}
		}
	}

	public void tryOutput() {
		boolean update = false;
		if (this.world.getGameTime() % 8L == 0L) {
			for(CapabilityReference<IItemHandler> i:outputItemCaps)
			if (i.isPresent()) {
				for (int slot = 4; slot < 9; ++slot) {
					if (!this.inventory.get(slot).isEmpty()) {
						ItemStack stack = ItemHandlerHelper.copyStackWithSize(this.inventory.get(slot), 1);
						stack = Utils.insertStackIntoInventory(i, stack, false);
						if (stack.isEmpty()) {
							this.inventory.get(slot).shrink(1);
							if (this.inventory.get(slot).getCount() <= 0) {
								this.inventory.set(slot, ItemStack.EMPTY);
							}
							update |= true;
							break;
						}
					}
				}
			}
		}
		if (update) {
			this.markDirty();
			this.markContainingBlockForUpdate(null);
		}
	}
	@Override
	protected boolean canDrainTankFrom(int arg0, Direction arg1) {
		return false;
	}

	@Override
	protected boolean canFillTankFrom(int i, Direction side, FluidStack fluidStack) {
		CarKilnBlockEntity master = master();
		if (master != null) {
			return true;
		}
		return false;
	}

	@Override
	protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
		CarKilnBlockEntity master = master();
		if (master != null) {
			if (this.posInMultiblock.getY() == 3 && this.posInMultiblock.getX() == 1 && this.posInMultiblock.getZ() == 0
					&& (side == null || side == getFacing())) {
				return master.tankinput;
			}
		}
		return new FluidTank[0];
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
		return ImmutableSet.of(new BlockPos(1, 1, 0));
	}

	@Override
	public void postEnergyTransferUpdate(int energy, boolean simulate) {
		if (!simulate) {
			this.updateMasterBlock(null, energy != 0);
		}
	}

	@Override
	public Set<BlockPos> getRedstonePos() {
		return ImmutableSet.of(new BlockPos(0, 1, 2));
	}

	public boolean isEnergyPos() {
		return this.getEnergyPos().contains(this.posInMultiblock);
	}

	@Nonnull
	@Override
	public FluxStorage getFluxStorage() {
		CarKilnBlockEntity master = this.master();
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
	public void checkResultEmpty() {
		resultsempty=true;
		for(ItemStack is:results)
			if(!is.isEmpty())
				resultsempty=false;
	}
	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.readCustomNBT(nbt, descPacket);
		energyStorage.readFromNBT(nbt);

		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		modelState=nbt.getShort("modelNum");
		tankinput[0].readFromNBT(nbt.getCompound("tankinput"));
		if (!descPacket) {
			if(nbt.contains("result"))
				results.set(0,ItemStack.read(nbt.getCompound("result")));
			else if(nbt.contains("results"))
				ItemStackHelper.loadAllItems(nbt.getCompound("results"),results);
			checkResultEmpty();
			tickEnergy = nbt.getInt("tickEnergy");
			ItemStackHelper.loadAllItems(nbt, inventory);
		}
	}

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
		super.writeCustomNBT(nbt, descPacket);
		energyStorage.writeToNBT(nbt);

		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putShort("modelNum",modelState);
		nbt.put("tankinput", tankinput[0].writeToNBT(new CompoundNBT()));
		if (!descPacket) {
			nbt.put("results", ItemStackHelper.saveAllItems(new CompoundNBT(),results));
			
			nbt.putInt("tickEnergy", tickEnergy);
			ItemStackHelper.saveAllItems(nbt, inventory);
		}
	}

    LazyOptional<IItemHandler> inHandler = registerConstantCap(new IEInventoryHandler(4, this, 0, true, false));
    LazyOptional<IItemHandler> outHandler = registerConstantCap(new IEInventoryHandler(5, this, 4, false, true));

    @Nonnull
    @Override
    public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
    	if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
	        if (facing != null && facing.getYOffset() == 0&&this.posInMultiblock.getZ() == 4) {
	        	
	                if (this.posInMultiblock.getY() == 1 &&this.posInMultiblock.getX()!=1&&facing.getAxis()!=this.getFacing().getAxis())
	                    return inHandler.cast();
	                else if (this.posInMultiblock.getY() == 0&&facing.getAxis()==this.getFacing().getAxis())
	                    return outHandler.cast();
	                return LazyOptional.empty();
	        }
    	}
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
    	if(i>4)return false;
        return CarKilnRecipe.isValidInput(itemStack);
	}

	@Override
	public int getSlotLimit(int i) {
		return 64;
	}

	@Override
	public void doGraphicalUpdates() {

	}

	@Override
	public int[] getCurrentProcessesStep() {
		CarKilnBlockEntity master = master();
		if (master != this && master != null)
			return master.getCurrentProcessesStep();
		return new int[]{processMax - process};
	}

	@Override
	public int[] getCurrentProcessesMax() {
		CarKilnBlockEntity master = master();
		if (master != this && master != null)
			return master.getCurrentProcessesMax();
		return new int[]{processMax};
	}
}
